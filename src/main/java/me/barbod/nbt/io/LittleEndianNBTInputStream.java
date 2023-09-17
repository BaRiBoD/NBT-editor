package me.barbod.nbt.io;

import me.barbod.io.ExceptionBiFunction;
import me.barbod.io.MaxDepthIO;
import me.barbod.nbt.tag.*;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LittleEndianNBTInputStream implements DataInput, NBTInput, MaxDepthIO, Closeable {
    private final DataInputStream input;

    private static Map<Byte, ExceptionBiFunction<LittleEndianNBTInputStream, Integer, ? extends JTag<?>, IOException>> readers = new HashMap<>();
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
        put(JListTag.ID, LittleEndianNBTInputStream::readListTag, JListTag.class);
        put(JCompoundTag.ID, LittleEndianNBTInputStream::readCompound, JCompoundTag.class);
        put(JIntArrayTag.ID, (i, d) -> readIntArray(i), JIntArrayTag.class);
        put(JLongArrayTag.ID, (i, d) -> readLongArray(i), JLongArrayTag.class);
    }

    private static void put(byte id, ExceptionBiFunction<LittleEndianNBTInputStream, Integer, ? extends JTag<?>, IOException> reader, Class<?> clazz) {
        readers.put(id, reader);
        idClassMapping.put(id, clazz);
    }

    public LittleEndianNBTInputStream(InputStream in) {
        input = new DataInputStream(in);
    }

    public LittleEndianNBTInputStream(DataInputStream in) {
        input = in;
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
        ExceptionBiFunction<LittleEndianNBTInputStream, Integer, ? extends JTag<?>, IOException> f;
        if ((f = readers.get(type)) == null) {
            throw new IOException("invalid tag id \"" + type + "\"");
        }
        return f.accept(this, maxDepth);
    }

    private static JByteTag readByte(LittleEndianNBTInputStream in) throws IOException {
        return new JByteTag(in.readByte());
    }

    private static JShortTag readShort(LittleEndianNBTInputStream in) throws IOException {
        return new JShortTag(in.readShort());
    }

    private static JIntTag readInt(LittleEndianNBTInputStream in) throws IOException {
        return new JIntTag(in.readInt());
    }

    private static JLongTag readLong(LittleEndianNBTInputStream in) throws IOException {
        return new JLongTag(in.readLong());
    }

    private static JFloatTag readFloat(LittleEndianNBTInputStream in) throws IOException {
        return new JFloatTag(in.readFloat());
    }

    private static JDoubleTag readDouble(LittleEndianNBTInputStream in) throws IOException {
        return new JDoubleTag(in.readDouble());
    }

    private static JStringTag readString(LittleEndianNBTInputStream in) throws IOException {
        return new JStringTag(in.readUTF());
    }

    private static JByteArrayTag readByteArray(LittleEndianNBTInputStream in) throws IOException {
        JByteArrayTag bat = new JByteArrayTag(new byte[in.readInt()]);
        in.readFully(bat.getValue());
        return bat;
    }

    private static JIntArrayTag readIntArray(LittleEndianNBTInputStream in) throws IOException {
        int l = in.readInt();
        int[] data = new int[l];
        JIntArrayTag iat = new JIntArrayTag(data);
        for (int i = 0; i < l; i++) {
            data[i] = in.readInt();
        }
        return iat;
    }

    private static JLongArrayTag readLongArray(LittleEndianNBTInputStream in) throws IOException {
        int l = in.readInt();
        long[] data = new long[l];
        JLongArrayTag iat = new JLongArrayTag(data);
        for (int i = 0; i < l; i++) {
            data[i] = in.readLong();
        }
        return iat;
    }

    private static JListTag<?> readListTag(LittleEndianNBTInputStream in, int maxDepth) throws IOException {
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

    private static JCompoundTag readCompound(LittleEndianNBTInputStream in, int maxDepth) throws IOException {
        JCompoundTag comp = new JCompoundTag();
        for (int id = in.readByte() & 0xFF; id != 0; id = in.readByte() & 0xFF) {
            String key = in.readUTF();
            JTag<?> element = in.readTag((byte) id, in.decrementMaxDepth(maxDepth));
            comp.put(key, element);
        }
        return comp;
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        input.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        input.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return input.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return input.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return input.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return input.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return Short.reverseBytes(input.readShort());
    }

    public int readUnsignedShort() throws IOException {
        return Short.toUnsignedInt(Short.reverseBytes(input.readShort()));
    }

    @Override
    public char readChar() throws IOException {
        return Character.reverseBytes(input.readChar());
    }

    @Override
    public int readInt() throws IOException {
        return Integer.reverseBytes(input.readInt());
    }

    @Override
    public long readLong() throws IOException {
        return Long.reverseBytes(input.readLong());
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(Integer.reverseBytes(input.readInt()));
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(Long.reverseBytes(input.readLong()));
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        return input.readLine();
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    @Override
    public String readUTF() throws IOException {
        byte[] bytes = new byte[readUnsignedShort()];
        readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
