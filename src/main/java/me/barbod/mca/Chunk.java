package me.barbod.mca;

import me.barbod.nbt.io.NBTDeserializer;
import me.barbod.nbt.io.NBTSerializer;
import me.barbod.nbt.io.NamedTag;
import me.barbod.nbt.tag.JCompoundTag;
import me.barbod.nbt.tag.JListTag;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static me.barbod.mca.LoadFlags.*;

public class Chunk implements Iterable<Section>{
    public static final int DEFAULT_DATA_VERSION = 2567;

    private boolean partial;
    private boolean raw;

    private int lastMCAUpdate;

    private JCompoundTag data;

    private int dataVersion;
    private long lastUpdate;
    private long inhabitedTime;
    private int[] biomes;
    private JCompoundTag heightMaps;
    private JCompoundTag carvingMasks;
    private Map<Integer, Section> sections = new TreeMap<>();
    private JListTag<JCompoundTag> entities;
    private JListTag<JCompoundTag> tileEntities;
    private JListTag<JCompoundTag> tileTicks;
    private JListTag<JCompoundTag> liquidTicks;
    private JListTag<JListTag<?>> lights;
    private JListTag<JListTag<?>> liquidsToBeTicked;
    private JListTag<JListTag<?>> toBeTicked;
    private JListTag<JListTag<?>> postProcessing;
    private String status;
    private JCompoundTag structures;

    Chunk(int lastMCAUpdate) {
        this.lastMCAUpdate = lastMCAUpdate;
    }

    public Chunk(JCompoundTag data) {
        this.data = data;
        initReferences(ALL_DATA);
    }

    private void initReferences(long loadFlags) {
        if (data == null) {
            throw new NullPointerException("data cannot be null");
        }

        if ((loadFlags != ALL_DATA) && (loadFlags & RAW) != 0) {
            raw = true;
            return;
        }

        JCompoundTag level;
        if ((level = data.getCompoundTag("Level")) == null) {
            throw new IllegalArgumentException("data does not contain \"Level\" tag");
        }
        dataVersion = data.getInt("DataVersion");
        inhabitedTime = level.getLong("InhabitedTime");
        lastUpdate = level.getLong("LastUpdate");
        if ((loadFlags & BIOMES) != 0) {
            biomes = level.getIntArray("Biomes");
        }
        if ((loadFlags & HEIGHTMAPS) != 0) {
            heightMaps = level.getCompoundTag("Heightmaps");
        }
        if ((loadFlags & CARVING_MASKS) != 0) {
            carvingMasks = level.getCompoundTag("CarvingMasks");
        }
        if ((loadFlags & ENTITIES) != 0) {
            entities = level.containsKey("Entities") ? level.getListTag("Entities").asCompoundTagList() : null;
        }
        if ((loadFlags & TILE_ENTITIES) != 0) {
            tileEntities = level.containsKey("TileEntities") ? level.getListTag("TileEntities").asCompoundTagList() : null;
        }
        if ((loadFlags & TILE_TICKS) != 0) {
            tileTicks = level.containsKey("TileTicks") ? level.getListTag("TileTicks").asCompoundTagList() : null;
        }
        if ((loadFlags & LIQUID_TICKS) != 0) {
            liquidTicks = level.containsKey("LiquidTicks") ? level.getListTag("LiquidTicks").asCompoundTagList() : null;
        }
        if ((loadFlags & LIGHTS) != 0) {
            lights = level.containsKey("Lights") ? level.getListTag("Lights").asListTagList() : null;
        }
        if ((loadFlags & LIQUIDS_TO_BE_TICKED) != 0) {
            liquidsToBeTicked = level.containsKey("LiquidsToBeTicked") ? level.getListTag("LiquidsToBeTicked").asListTagList() : null;
        }
        if ((loadFlags & TO_BE_TICKED) != 0) {
            toBeTicked = level.containsKey("ToBeTicked") ? level.getListTag("ToBeTicked").asListTagList() : null;
        }
        if ((loadFlags & POST_PROCESSING) != 0) {
            postProcessing = level.containsKey("PostProcessing") ? level.getListTag("PostProcessing").asListTagList() : null;
        }
        status = level.getString("Status");
        if ((loadFlags & STRUCTURES) != 0) {
            structures = level.getCompoundTag("Structures");
        }
        if ((loadFlags & (BLOCK_LIGHTS|BLOCK_STATES|SKY_LIGHT)) != 0 && level.containsKey("Sections")) {
            for (JCompoundTag section : level.getListTag("Sections").asCompoundTagList()) {
                int sectionIndex = section.getNumber("Y").byteValue();
                Section newSection = new Section(section, dataVersion, loadFlags);
                sections.put(sectionIndex, newSection);
            }
        }

        if (loadFlags != ALL_DATA) {
            data = null;
            partial = true;
        }
    }

    public int serialize(RandomAccessFile raf, int xPos, int zPos) throws IOException {
        if (partial) {
            throw new UnsupportedOperationException("Partially loaded chunks cannot be serialized");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
        try (BufferedOutputStream nbtOut = new BufferedOutputStream(CompressionType.ZLIB.compress(baos))) {
            new NBTSerializer(false).toStream(new NamedTag(null, updateHandle(xPos, zPos)), nbtOut);
        }
        byte[] rawData = baos.toByteArray();
        raf.writeInt(rawData.length + 1);
        raf.writeByte(CompressionType.ZLIB.getID());
        raf.write(rawData);
        return rawData.length + 5;
    }

    public void deserialize(RandomAccessFile raf) throws IOException {
        deserialize(raf, ALL_DATA);
    }

    public void deserialize(RandomAccessFile raf, long loadFlags) throws IOException {
        byte compressionTypeByte = raf.readByte();
        CompressionType compressionType = CompressionType.getFromID(compressionTypeByte);
        if (compressionType == null) {
            throw new IOException("invalid compression type " + compressionTypeByte);
        }
        BufferedInputStream dis = new BufferedInputStream(compressionType.decompress(new FileInputStream(raf.getFD())));
        NamedTag tag = new NBTDeserializer(false).fromStream(dis);
        if (tag != null && tag.getTag() instanceof JCompoundTag) {
            data = (JCompoundTag) tag.getTag();
            initReferences(loadFlags);
        } else {
            throw new IOException("invalid data tag: " + (tag == null ? "null" : tag.getClass().getName()));
        }
    }

    @Deprecated
    public int getBiomeAt(int blockX, int blockZ) {
        if (dataVersion < 2202) {
            if (biomes == null || biomes.length != 256) {
                return -1;
            }
            return biomes[getBlockIndex(blockX, blockZ)];
        } else {
            throw new IllegalStateException("cannot get biome using Chunk#getBiomeAt(int,int) from biome data with DataVersion of 2202 or higher, use Chunk#getBiomeAt(int,int,int) instead");
        }
    }

    public int getBiomeAt(int blockX, int blockY, int blockZ) {
        if (dataVersion < 2202) {
            if (biomes == null || biomes.length != 256) {
                return -1;
            }
            return biomes[getBlockIndex(blockX, blockZ)];
        } else {
            if (biomes == null || biomes.length != 1024) {
                return -1;
            }
            int biomeX = (blockX & 0xF) >> 2;
            int biomeY = (blockY & 0xF) >> 2;
            int biomeZ = (blockZ & 0xF) >> 2;

            return biomes[getBiomeIndex(biomeX, biomeY, biomeZ)];
        }
    }

    @Deprecated
    public void setBiomeAt(int blockX, int blockZ, int biomeID) {
        checkRaw();
        if (dataVersion < 2202) {
            if (biomes == null || biomes.length != 256) {
                biomes = new int[256];
                Arrays.fill(biomes, -1);
            }
            biomes[getBlockIndex(blockX, blockZ)] = biomeID;
        } else {
            if (biomes == null || biomes.length != 1024) {
                biomes = new int[1024];
                Arrays.fill(biomes, -1);
            }

            int biomeX = (blockX & 0xF) >> 2;
            int biomeZ = (blockZ & 0xF) >> 2;

            for (int y = 0; y < 64; y++) {
                biomes[getBiomeIndex(biomeX, y, biomeZ)] = biomeID;
            }
        }
    }

    public void setBiomeAt(int blockX, int blockY, int blockZ, int biomeID) {
        checkRaw();
        if (dataVersion < 2202) {
            if (biomes == null || biomes.length != 256) {
                biomes = new int[256];
                Arrays.fill(biomes, -1);
            }
            biomes[getBlockIndex(blockX, blockZ)] = biomeID;
        } else {
            if (biomes == null || biomes.length != 1024) {
                biomes = new int[1024];
                Arrays.fill(biomes, -1);
            }

            int biomeX = (blockX & 0xF) >> 2;
            int biomeZ = (blockZ & 0xF) >> 2;

            biomes[getBiomeIndex(biomeX, blockY, biomeZ)] = biomeID;
        }
    }

    int getBiomeIndex(int biomeX, int biomeY, int biomeZ) {
        return biomeY * 16 + biomeZ * 4 + biomeX;
    }

    public JCompoundTag getBlockStateAt(int blockX, int blockY, int blockZ) {
        Section section = sections.get(MCAUtil.blockToChunk(blockY));
        if (section == null) {
            return null;
        }
        return section.getBlockStateAt(blockX, blockY, blockZ);
    }

    public void setBlockStateAt(int blockX, int blockY, int blockZ, JCompoundTag state, boolean cleanup) {
        checkRaw();
        int sectionIndex = MCAUtil.blockToChunk(blockY);
        Section section = sections.get(sectionIndex);
        if (section == null) {
            sections.put(sectionIndex, section = Section.newSection());
        }
        section.setBlockStateAt(blockX, blockY, blockZ, state, cleanup);
    }

    public int getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(int dataVersion) {
        checkRaw();
        this.dataVersion = dataVersion;
        for (Section section : sections.values()) {
            if (section != null) {
                section.dataVersion = dataVersion;
            }
        }
    }

    public int getLastMCAUpdate() {
        return lastMCAUpdate;
    }

    public void setLastMCAUpdate(int lastMCAUpdate) {
        checkRaw();
        this.lastMCAUpdate = lastMCAUpdate;
    }

    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        checkRaw();
        this.status = status;
    }

    public Section getSection(int sectionY) {
        return sections.get(sectionY);
    }

    public void setSection(int sectionY, Section section) {
        checkRaw();
        sections.put(sectionY, section);
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        checkRaw();
        this.lastUpdate = lastUpdate;
    }

    public long getInhabitedTime() {
        return inhabitedTime;
    }

    public void setInhabitedTime(long inhabitedTime) {
        checkRaw();
        this.inhabitedTime = inhabitedTime;
    }

    public int[] getBiomes() {
        return biomes;
    }

    public void setBiomes(int[] biomes) {
        checkRaw();
        if (biomes != null) {
            if (dataVersion < 2202 && biomes.length != 256 || dataVersion >= 2202 && biomes.length != 1024) {
                throw new IllegalArgumentException("biomes array must have a length of " + (dataVersion < 2202 ? "256" : "1024"));
            }
        }
        this.biomes = biomes;
    }

    public JCompoundTag getHeightMaps() {
        return heightMaps;
    }

    public void setHeightMaps(JCompoundTag heightMaps) {
        checkRaw();
        this.heightMaps = heightMaps;
    }

    public JCompoundTag getCarvingMasks() {
        return carvingMasks;
    }

    public void setCarvingMasks(JCompoundTag carvingMasks) {
        checkRaw();
        this.carvingMasks = carvingMasks;
    }

    public JListTag<JCompoundTag> getEntities() {
        return entities;
    }

    public void setEntities(JListTag<JCompoundTag> entities) {
        checkRaw();
        this.entities = entities;
    }

    public JListTag<JCompoundTag> getTileEntities() {
        return tileEntities;
    }

    public void setTileEntities(JListTag<JCompoundTag> tileEntities) {
        checkRaw();
        this.tileEntities = tileEntities;
    }

    public JListTag<JCompoundTag> getTileTicks() {
        return tileTicks;
    }

    public void setTileTicks(JListTag<JCompoundTag> tileTicks) {
        checkRaw();
        this.tileTicks = tileTicks;
    }

    public JListTag<JCompoundTag> getLiquidTicks() {
        return liquidTicks;
    }

    public void setLiquidTicks(JListTag<JCompoundTag> liquidTicks) {
        checkRaw();
        this.liquidTicks = liquidTicks;
    }

    public JListTag<JListTag<?>> getLights() {
        return lights;
    }

    public void setLights(JListTag<JListTag<?>> lights) {
        checkRaw();
        this.lights = lights;
    }

    public JListTag<JListTag<?>> getLiquidsToBeTicked() {
        return liquidsToBeTicked;
    }

    public void setLiquidsToBeTicked(JListTag<JListTag<?>> liquidsToBeTicked) {
        checkRaw();
        this.liquidsToBeTicked = liquidsToBeTicked;
    }

    public JListTag<JListTag<?>> getToBeTicked() {
        return toBeTicked;
    }

    public void setToBeTicked(JListTag<JListTag<?>> toBeTicked) {
        checkRaw();
        this.toBeTicked = toBeTicked;
    }

    public JListTag<JListTag<?>> getPostProcessing() {
        return postProcessing;
    }

    public void setPostProcessing(JListTag<JListTag<?>> postProcessing) {
        checkRaw();
        this.postProcessing = postProcessing;
    }

    public JCompoundTag getStructures() {
        return structures;
    }

    public void setStructures(JCompoundTag structures) {
        checkRaw();
        this.structures = structures;
    }

    int getBlockIndex(int blockX, int blockZ) {
        return (blockZ & 0xF) * 16 + (blockX & 0xF);
    }

    public void cleanupPalettesAndBlockStates() {
        checkRaw();
        for (Section section : sections.values()) {
            if (section != null) {
                section.cleanupPaletteAndBlockStates();
            }
        }
    }

    private void checkRaw() {
        if (raw) {
            throw new UnsupportedOperationException("cannot update field when working with raw data");
        }
    }

    public static Chunk newChunk() {
        return newChunk(DEFAULT_DATA_VERSION);
    }

    public static Chunk newChunk(int dataVersion) {
        Chunk c = new Chunk(0);
        c.dataVersion = dataVersion;
        c.data = new JCompoundTag();
        c.data.put("Level", new JCompoundTag());
        c.status = "mobs_spawned";
        return c;
    }

    public JCompoundTag getHandle() {
        return data;
    }

    public JCompoundTag updateHandle(int xPos, int zPos) {
        if (raw) {
            return data;
        }

        data.putInt("DataVersion", dataVersion);
        JCompoundTag level = data.getCompoundTag("Level");
        level.putInt("xPos", xPos);
        level.putInt("zPos", zPos);
        level.putLong("LastUpdate", lastUpdate);
        level.putLong("InhabitedTime", inhabitedTime);
        if (dataVersion < 2202) {
            if (biomes != null && biomes.length == 256) {
                level.putIntArray("Biomes", biomes);
            }
        } else {
            if (biomes != null && biomes.length == 1024) {
                level.putIntArray("Biomes", biomes);
            }
        }
        if (heightMaps != null) {
            level.put("Heightmaps", heightMaps);
        }
        if (carvingMasks != null) {
            level.put("CarvingMasks", carvingMasks);
        }
        if (entities != null) {
            level.put("Entities", entities);
        }
        if (tileEntities != null) {
            level.put("TileEntities", tileEntities);
        }
        if (tileTicks != null) {
            level.put("TileTicks", tileTicks);
        }
        if (liquidTicks != null) {
            level.put("LiquidTicks", liquidTicks);
        }
        if (lights != null) {
            level.put("Lights", lights);
        }
        if (liquidsToBeTicked != null) {
            level.put("LiquidsToBeTicked", liquidsToBeTicked);
        }
        if (toBeTicked != null) {
            level.put("ToBeTicked", toBeTicked);
        }
        if (postProcessing != null) {
            level.put("PostProcessing", postProcessing);
        }
        level.putString("Status", status);
        if (structures != null) {
            level.put("Structures", structures);
        }
        JListTag<JCompoundTag> sections = new JListTag<>(JCompoundTag.class);
        for (Section section : this.sections.values()) {
            if (section != null) {
                sections.add(section.updateHandle());
            }
        }
        level.put("Sections", sections);
        return data;
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.values().iterator();
    }
}
