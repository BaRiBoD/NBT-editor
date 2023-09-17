package me.barbod.nbt.io;

import me.barbod.io.ExceptionTriConsumer;
import me.barbod.io.MaxDepthIO;
import me.barbod.nbt.tag.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class NBTOutputStream extends DataOutputStream implements NBTOutput, MaxDepthIO {
    private static Map<Byte, ExceptionTriConsumer<NBTOutputStream, JTag<?>, Integer, IOException>> writers = new HashMap<>();
    private static Map<Class<?>, Byte> classIdMapping = new HashMap<>();

    static {
        put(JEndTag.ID, (o, t, d) -> {}, JEndTag.class);
        put(JByteTag.ID, (o, t, d) -> writeByte(o, t), JByteTag.class);
        put(JShortTag.ID, (o, t, d) -> writeShort(o, t), JShortTag.class);
        put(JIntTag.ID, (o, t, d) -> writeInt(o, t), JIntTag.class);
        put(JLongTag.ID, (o, t, d) -> writeLong(o, t), JLongTag.class);
        put(JFloatTag.ID, (o, t, d) -> writeFloat(o, t), JFloatTag.class);
        put(JDoubleTag.ID, (o, t, d) -> writeDouble(o, t), JDoubleTag.class);
        put(JByteArrayTag.ID, (o, t, d) -> writeByteArray(o, t), JByteArrayTag.class);
        put(JStringTag.ID, (o, t, d) -> writeString(o, t), JStringTag.class);
        put(JListTag.ID, NBTOutputStream::writeList, JListTag.class);
        put(JCompoundTag.ID, NBTOutputStream::writeCompound, JCompoundTag.class);
        put(JIntArrayTag.ID, (o, t, d) -> writeIntArray(o, t), JIntArrayTag.class);
        put(JLongArrayTag.ID, (o, t, d) -> writeLongArray(o, t), JLongArrayTag.class);
    }

    private static void put(byte id, ExceptionTriConsumer<NBTOutputStream, JTag<?>, Integer, IOException> f, Class<?> clazz) {
        writers.put(id, f);
        classIdMapping.put(clazz, id);
    }

    public NBTOutputStream(OutputStream out) {
        super(out);
    }

    public void writeTag(NamedTag tag, int maxDepth) throws IOException {
        writeByte(tag.getTag().getID());
        if (tag.getTag().getID() != 0) {
            writeUTF(tag.getName() == null ? "" : tag.getName());
        }
        writeRawTag(tag.getTag(), maxDepth);
    }

    public void writeTag(JTag<?> tag, int maxDepth) throws IOException {
        writeByte(tag.getID());
        if (tag.getID() != 0) {
            writeUTF("");
        }
        writeRawTag(tag, maxDepth);
    }

    public void writeRawTag(JTag<?> tag, int maxDepth) throws IOException {
        ExceptionTriConsumer<NBTOutputStream, JTag<?>, Integer, IOException> f;
        if ((f = writers.get(tag.getID())) == null) {
            throw new IOException("invalid tag \"" + tag.getID() + "\"");
        }
        f.accept(this, tag, maxDepth);
    }

    static byte idFromClass(Class<?> clazz) {
        Byte id = classIdMapping.get(clazz);
        if (id == null) {
            throw new IllegalArgumentException("unknown Tag class " + clazz.getName());
        }
        return id;
    }

    private static void writeByte(NBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeByte(((JByteTag) tag).asByte());
    }

    private static void writeShort(NBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeShort(((JShortTag) tag).asShort());
    }

    private static void writeInt(NBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeInt(((JIntTag) tag).asInt());
    }

    private static void writeLong(NBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeLong(((JLongTag) tag).asLong());
    }

    private static void writeFloat(NBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeFloat(((JFloatTag) tag).asFloat());
    }

    private static void writeDouble(NBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeDouble(((JDoubleTag) tag).asDouble());
    }

    private static void writeString(NBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeUTF(((JStringTag) tag).getValue());
    }

    private static void writeByteArray(NBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeInt(((JByteArrayTag) tag).length());
        out.write(((JByteArrayTag) tag).getValue());
    }

    private static void writeIntArray(NBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeInt(((JIntArrayTag) tag).length());
        for (int i : ((JIntArrayTag) tag).getValue()) {
            out.writeInt(i);
        }
    }

    private static void writeLongArray(NBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeInt(((JLongArrayTag) tag).length());
        for (long l : ((JLongArrayTag) tag).getValue()) {
            out.writeLong(l);
        }
    }

    private static void writeList(NBTOutputStream out, JTag<?> tag, int maxDepth) throws IOException {
        out.writeByte(idFromClass(((JListTag<?>) tag).getTypeClass()));
        out.writeInt(((JListTag<?>) tag).size());
        for (JTag<?> t : ((JListTag<?>) tag)) {
            out.writeRawTag(t, out.decrementMaxDepth(maxDepth));
        }
    }

    private static void writeCompound(NBTOutputStream out, JTag<?> tag, int maxDepth) throws IOException {
        for (Map.Entry<String, JTag<?>> entry : (JCompoundTag) tag) {
            if (entry.getValue().getID() == 0) {
                throw new IOException("end tag not allowed");
            }
            out.writeByte(entry.getValue().getID());
            out.writeUTF(entry.getKey());
            out.writeRawTag(entry.getValue(), out.decrementMaxDepth(maxDepth));
        }
        out.writeByte(0);
    }
}
