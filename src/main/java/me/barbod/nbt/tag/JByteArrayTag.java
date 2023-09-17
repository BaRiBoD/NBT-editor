package me.barbod.nbt.tag;

import java.util.Arrays;

public class JByteArrayTag extends JArrayTag<byte[]> implements Comparable<JByteArrayTag> {
    public static final byte ID = 7;
    public static final byte[] ZERO_VALUE = new byte[0];

    public JByteArrayTag(byte[] value) {
        super(value);
    }

    public JByteArrayTag() {
        this(ZERO_VALUE);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && Arrays.equals(getValue(), ((JByteArrayTag) other).getValue());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getValue());
    }

    @Override
    public int compareTo(JByteArrayTag other) {
        return Integer.compare(length(), other.length());
    }

    @Override
    public JByteArrayTag clone() {
        return new JByteArrayTag(Arrays.copyOf(getValue(), length()));
    }

    @Override
    public byte getID() {
        return ID;
    }
}
