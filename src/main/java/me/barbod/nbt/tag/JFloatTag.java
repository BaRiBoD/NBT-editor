package me.barbod.nbt.tag;

public class JFloatTag extends JNumberTag<Float> implements Comparable<JFloatTag> {
    public static final byte ID = 5;
    public static final float ZERO_VALUE = 0.0F;

    public JFloatTag(float value) {
        super(value);
    }

    public JFloatTag() {
        this(ZERO_VALUE);
    }

    public void setValue(float value) {
        super.setValue(value);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && getValue().equals(((JFloatTag) other).getValue());
    }

    @Override
    public int compareTo(JFloatTag other) {
        return getValue().compareTo(other.getValue());
    }

    @Override
    public JFloatTag clone() {
        return new JFloatTag(getValue());
    }

    @Override
    public byte getID() {
        return ID;
    }
}
