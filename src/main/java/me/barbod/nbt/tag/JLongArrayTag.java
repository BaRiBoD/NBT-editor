package me.barbod.nbt.tag;

import java.util.Arrays;

public class JLongArrayTag extends JArrayTag<long[]> implements Comparable<JLongArrayTag> {
    public static final byte ID = 12;
    public static final long[] ZERO_VALUE = new long[0];

    public JLongArrayTag(long[] value) {
        super(value);
    }

    public JLongArrayTag() {
        this(ZERO_VALUE);
    }

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && Arrays.equals(getValue(), ((JLongArrayTag) other).getValue());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getValue());
    }

    @Override
    public int compareTo(JLongArrayTag other) {
        return Integer.compare(length(), other.length());
    }

    @Override
    public JLongArrayTag clone() {
        return new JLongArrayTag(Arrays.copyOf(getValue(), length()));
    }
}
