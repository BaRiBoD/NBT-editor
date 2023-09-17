package me.barbod.nbt.tag;

public class JShortTag extends JNumberTag<Short> implements Comparable<JShortTag> {
    public static final byte ID = 2;
    public static final short ZERO_VALUE = 0;

    public JShortTag(short value) {
        super(value);
    }

    public JShortTag() {
        this(ZERO_VALUE);
    }

    @Override
    public byte getID() {
        return ID;
    }

    public void setValue(short value) {
        super.setValue(value);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && asShort() == ((JShortTag) other).asShort();
    }

    @Override
    public int compareTo(JShortTag other) {
        return getValue().compareTo(other.getValue());
    }

    @Override
    public JShortTag clone() {
        return new JShortTag(getValue());
    }
}
