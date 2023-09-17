package me.barbod.nbt.tag;

public class JDoubleTag extends JNumberTag<Double> implements Comparable<JDoubleTag> {
    public static final byte ID = 6;
    public static final double ZERO_VALUE = 0.0D;

    public JDoubleTag(double value) {
        super(value);
    }

    public JDoubleTag() {
        this(ZERO_VALUE);
    }

    public void setValue(double value) {
        super.setValue(value);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && getValue().equals(((JDoubleTag) other).getValue());
    }

    @Override
    public int compareTo(JDoubleTag other) {
        return getValue().compareTo(other.getValue());
    }

    @Override
    public JDoubleTag clone() {
        return new JDoubleTag(getValue());
    }

    @Override
    public byte getID() {
        return ID;
    }
}
