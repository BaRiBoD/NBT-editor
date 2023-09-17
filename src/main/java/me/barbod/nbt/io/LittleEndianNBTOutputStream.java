package me.barbod.nbt.io;

import me.barbod.io.ExceptionTriConsumer;
import me.barbod.io.MaxDepthIO;
import me.barbod.nbt.tag.*;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LittleEndianNBTOutputStream implements DataOutput, NBTOutput, MaxDepthIO, Closeable {
    private final DataOutputStream output;

    private static Map<Byte, ExceptionTriConsumer<LittleEndianNBTOutputStream, JTag<?>, Integer, IOException>> writers = new HashMap<>();
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
        put(JListTag.ID, LittleEndianNBTOutputStream::writeList, JListTag.class);
        put(JCompoundTag.ID, LittleEndianNBTOutputStream::writeCompound, JCompoundTag.class);
        put(JIntArrayTag.ID, (o, t, d) -> writeIntArray(o, t), JIntArrayTag.class);
        put(JLongArrayTag.ID, (o, t, d) -> writeLongArray(o, t), JLongArrayTag.class);
    }

    private static void put(byte id, ExceptionTriConsumer<LittleEndianNBTOutputStream, JTag<?>, Integer, IOException> f, Class<?> clazz) {
        writers.put(id, f);
        classIdMapping.put(clazz, id);
    }

    public LittleEndianNBTOutputStream(OutputStream out) {
        output = new DataOutputStream(out);
    }

    public LittleEndianNBTOutputStream(DataOutputStream out) {
        output = out;
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
        ExceptionTriConsumer<LittleEndianNBTOutputStream, JTag<?>, Integer, IOException> f;
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

    private static void writeByte(LittleEndianNBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeByte(((JByteTag) tag).asByte());
    }

    private static void writeShort(LittleEndianNBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeShort(((JShortTag) tag).asShort());
    }

    private static void writeInt(LittleEndianNBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeInt(((JIntTag) tag).asInt());
    }

    private static void writeLong(LittleEndianNBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeLong(((JLongTag) tag).asLong());
    }

    private static void writeFloat(LittleEndianNBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeFloat(((JFloatTag) tag).asFloat());
    }

    private static void writeDouble(LittleEndianNBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeDouble(((JDoubleTag) tag).asDouble());
    }

    private static void writeString(LittleEndianNBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeUTF(((JStringTag) tag).getValue());
    }

    private static void writeByteArray(LittleEndianNBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeInt(((JByteArrayTag) tag).length());
        out.write(((JByteArrayTag) tag).getValue());
    }

    private static void writeIntArray(LittleEndianNBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeInt(((JIntArrayTag) tag).length());
        for (int i : ((JIntArrayTag) tag).getValue()) {
            out.writeInt(i);
        }
    }

    private static void writeLongArray(LittleEndianNBTOutputStream out, JTag<?> tag) throws IOException {
        out.writeInt(((JLongArrayTag) tag).length());
        for (long l : ((JLongArrayTag) tag).getValue()) {
            out.writeLong(l);
        }
    }

    private static void writeList(LittleEndianNBTOutputStream out, JTag<?> tag, int maxDepth) throws IOException {
        out.writeByte(idFromClass(((JListTag<?>) tag).getTypeClass()));
        out.writeInt(((JListTag<?>) tag).size());
        for (JTag<?> t : ((JListTag<?>) tag)) {
            out.writeRawTag(t, out.decrementMaxDepth(maxDepth));
        }
    }

    private static void writeCompound(LittleEndianNBTOutputStream out, JTag<?> tag, int maxDepth) throws IOException {
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

    @Override
    public void close() throws IOException {
        output.close();
    }

    @Override
    public void flush() throws IOException {
        output.flush();
    }

    @Override
    public void write(int b) throws IOException {
        output.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        output.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        output.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        output.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        output.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        output.writeShort(Short.reverseBytes((short) v));
    }

    @Override
    public void writeChar(int v) throws IOException {
        output.writeChar(Character.reverseBytes((char) v));
    }

    @Override
    public void writeInt(int v) throws IOException {
        output.writeInt(Integer.reverseBytes(v));
    }

    @Override
    public void writeLong(long v) throws IOException {
        output.writeLong(Long.reverseBytes(v));
    }

    @Override
    public void writeFloat(float v) throws IOException {
        output.writeInt(Integer.reverseBytes(Float.floatToIntBits(v)));
    }

    @Override
    public void writeDouble(double v) throws IOException {
        output.writeLong(Long.reverseBytes(Double.doubleToLongBits(v)));
    }

    @Override
    public void writeBytes(String s) throws IOException {
        output.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        output.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        writeShort(bytes.length);
        write(bytes);
    }
}
