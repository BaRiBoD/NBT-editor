package me.barbod.nbt.io;

import me.barbod.io.ExceptionBiFunction;
import me.barbod.io.MaxDepthIO;
import me.barbod.nbt.tag.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class NBTInputStream extends DataInputStream implements NBTInput, MaxDepthIO {
    private static Map<Byte, ExceptionBiFunction<NBTInputStream, Integer, ? extends JTag<?>, IOException>> readers = new HashMap<>();
    private static Map<Byte, Class<?>> idClassMapping = new HashMap<>();

    static {
        put(JEndTag.ID, (i, d) -> JEndTag.INSTANCE, JEndTag.class);
        put(JByteTag.ID, (i, d) -> readByte(i), JByteTag.class);
        put(JShortTag.ID, (i, d) -> readShort(i), JShortTag.class);
        put(JIntTag.ID, (i, d) -> readInt(i), JIntTag.class);
        put(JLongTag.ID, (i, d) -> readLong(i), JLongTag.class);
        put(JFloatTag.ID, (i, d) -> readFloat(i), JFloatTag.class);
        put(JDoubleTag.ID, (i, d) -> readDouble(i), JDoubleTag.class);
        put(JByteArrayTag.ID, (i, d) -> readByteArray(i), JByteArrayTag.class);
        put(JStringTag.ID, (i, d) -> readString(i), JStringTag.class);
        put(JListTag.ID, NBTInputStream::readListTag, JListTag.class);
        put(JCompoundTag.ID, NBTInputStream::readCompound, JCompoundTag.class);
        put(JIntArrayTag.ID, (i, d) -> readIntArray(i), JIntArrayTag.class);
        put(JLongArrayTag.ID, (i, d) -> readLongArray(i), JLongArrayTag.class);
    }

    private static void put(byte id, ExceptionBiFunction<NBTInputStream, Integer, ? extends JTag<?>, IOException> reader, Class<?> clazz) {
        readers.put(id, reader);
        idClassMapping.put(id, clazz);
    }

    public NBTInputStream(InputStream in) {
        super(in);
    }

    public NamedTag readTag(int maxDepth) throws IOException {
        byte id = readByte();
        return new NamedTag(readUTF(), readTag(id, maxDepth));
    }

    public JTag<?> readRawTag(int maxDepth) throws IOException {
        byte id = readByte();
        return readTag(id, maxDepth);
    }

    private JTag<?> readTag(byte type, int maxDepth) throws IOException {
        ExceptionBiFunction<NBTInputStream, Integer, ? extends JTag<?>, IOException> f;
        if ((f = readers.get(type)) == null) {
            throw new IOException("invalid tag id \"" + type + "\"");
        }
        return f.accept(this, maxDepth);
    }

    private static JByteTag readByte(NBTInputStream in) throws IOException {
        return new JByteTag(in.readByte());
    }

    private static JShortTag readShort(NBTInputStream in) throws IOException {
        return new JShortTag(in.readShort());
    }

    private static JIntTag readInt(NBTInputStream in) throws IOException {
        return new JIntTag(in.readInt());
    }

    private static JLongTag readLong(NBTInputStream in) throws IOException {
        return new JLongTag(in.readLong());
    }

    private static JFloatTag readFloat(NBTInputStream in) throws IOException {
        return new JFloatTag(in.readFloat());
    }

    private static JDoubleTag readDouble(NBTInputStream in) throws IOException {
        return new JDoubleTag(in.readDouble());
    }

    private static JStringTag readString(NBTInputStream in) throws IOException {
        return new JStringTag(in.readUTF());
    }

    private static JByteArrayTag readByteArray(NBTInputStream in) throws IOException {
        JByteArrayTag bat = new JByteArrayTag(new byte[in.readInt()]);
        in.readFully(bat.getValue());
        return bat;
    }

    private static JIntArrayTag readIntArray(NBTInputStream in) throws IOException {
        int l = in.readInt();
        int[] data = new int[l];
        JIntArrayTag iat = new JIntArrayTag(data);
        for (int i = 0; i < l; i++) {
            data[i] = in.readInt();
        }
        return iat;
    }

    private static JLongArrayTag readLongArray(NBTInputStream in) throws IOException {
        int l = in.readInt();
        long[] data = new long[l];
        JLongArrayTag iat = new JLongArrayTag(data);
        for (int i = 0; i < l; i++) {
            data[i] = in.readLong();
        }
        return iat;
    }

    private static JListTag<?> readListTag(NBTInputStream in, int maxDepth) throws IOException {
        byte listType = in.readByte();
        JListTag<?> list = JListTag.createUnchecked(idClassMapping.get(listType));
        int length = in.readInt();
        if (length < 0) {
            length = 0;
        }
        for (int i = 0; i < length; i++) {
            list.addUnchecked(in.readTag(listType, in.decrementMaxDepth(maxDepth)));
        }
        return list;
    }

    private static JCompoundTag readCompound(NBTInputStream in, int maxDepth) throws IOException {
        JCompoundTag comp = new JCompoundTag();
        for (int id = in.readByte() & 0xFF; id != 0; id = in.readByte() & 0xFF) {
            String key = in.readUTF();
            JTag<?> element = in.readTag((byte) id, in.decrementMaxDepth(maxDepth));
            comp.put(key, element);
        }
        return comp;
    }
}
