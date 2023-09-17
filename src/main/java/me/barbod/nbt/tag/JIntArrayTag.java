package me.barbod.nbt.tag;

import java.util.Arrays;

public class JIntArrayTag extends JArrayTag<int[]> implements Comparable<JIntArrayTag> {
    public static final byte ID = 11;
    public static final int[] ZERO_VALUE = new int[0];

    public JIntArrayTag(int[] value) {
        super(value);
    }

    public JIntArrayTag() {
        this(ZERO_VALUE);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && Arrays.equals(getValue(), ((JIntArrayTag) other).getValue());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getValue());
    }

    @Override
    public int compareTo(JIntArrayTag other) {
        return Integer.compare(length(), other.length());
    }

    @Override
    public JIntArrayTag clone() {
        return new JIntArrayTag(getValue());
    }

    @Override
    public byte getID() {
        return ID;
    }
}
