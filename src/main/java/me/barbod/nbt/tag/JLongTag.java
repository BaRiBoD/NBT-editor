package me.barbod.nbt.tag;

public class JLongTag extends JNumberTag<Long> implements Comparable<JLongTag> {
    public static final byte ID = 4;
    public static final long ZERO_VALUE = 0L;

    public JLongTag(long value) {
        super(value);
    }

    public JLongTag() {
        this(ZERO_VALUE);
    }

    @Override
    public byte getID() {
        return ID;
    }

    public void setValue(long value) {
        super.setValue(value);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && asLong() == ((JLongTag) other).asLong();
    }

    @Override
    public int compareTo(JLongTag other) {
        return getValue().compareTo(other.getValue());
    }

    @Override
    public JLongTag clone() {
        return new JLongTag(getValue());
    }
}
