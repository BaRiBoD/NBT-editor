package me.barbod.nbt.io;

import me.barbod.io.Serializer;
import me.barbod.nbt.tag.JTag;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class NBTSerializer implements Serializer<NamedTag> {
    private boolean compressed, littleEndian;

    public NBTSerializer() {
        this(true);
    }

    public NBTSerializer(boolean compressed) {
        this.compressed = compressed;
    }

    public NBTSerializer(boolean compressed, boolean littleEndian) {
        this.compressed = compressed;
        this.littleEndian = littleEndian;
    }

    @Override
    public void toStream(NamedTag object, OutputStream out) throws IOException {
        NBTOutput nbtOut;
        OutputStream output;
        if (compressed) {
            output = new GZIPOutputStream(out, true);
        } else {
            output = out;
        }

        if (littleEndian) {
            nbtOut = new LittleEndianNBTOutputStream(output);
        } else {
            nbtOut = new NBTOutputStream(output);
        }
        nbtOut.writeTag(object,     JTag.DEFAULT_MAX_DEPTH);
        nbtOut.flush();
    }
}
