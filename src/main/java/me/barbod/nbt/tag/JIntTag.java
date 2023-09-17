package me.barbod.nbt.tag;

public class JIntTag extends JNumberTag<Integer> implements Comparable<JIntTag> {
    public static final byte ID = 3;
    public static final int ZERO_VALUE = 0;

    public JIntTag(int value) {
        super(value);
    }

    public JIntTag() {
        this(ZERO_VALUE);
    }

    public void setValue(int value) {
        super.setValue(value);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && getValue().equals(((JIntTag) other).getValue());
    }

    @Override
    public int compareTo(JIntTag other) {
        return getValue().compareTo(other.getValue());
    }

    @Override
    public JIntTag clone() {
        return new JIntTag(getValue());
    }

    @Override
    public byte getID() {
        return ID;
    }
}
