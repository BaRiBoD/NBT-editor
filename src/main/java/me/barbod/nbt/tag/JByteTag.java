package me.barbod.nbt.tag;

public class JByteTag extends JNumberTag<Byte> implements Comparable<JByteTag> {
    public static final byte ID = 1;
    public static final byte ZERO_VALUE = 0;

    public JByteTag(Byte value) {
        super(value);
    }

    public JByteTag(boolean value) {
        this((byte) (value ? 1 : 0));
    }

    public JByteTag() {
        this(ZERO_VALUE);
    }

    public void setValue(byte value) {
        super.setValue(value);
    }

    public boolean asBoolean() {
        return getValue() > 0;
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && asByte() == ((JByteTag) other).asByte();
    }

    @Override
    public int compareTo(JByteTag other) {
        return getValue().compareTo(other.getValue());
    }

    @Override
    public JByteTag clone() {
        return new JByteTag(getValue());
    }

    @Override
    public byte getID() {
        return ID;
    }
}
