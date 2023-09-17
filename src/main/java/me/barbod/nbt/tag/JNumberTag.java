package me.barbod.nbt.tag;

public abstract class JNumberTag<T extends Number & Comparable<T>> extends JTag<T> {
    public JNumberTag(T value) {
        super(value);
    }

    public byte asByte() {
        return getValue().byteValue();
    }

    public short asShort() {
        return getValue().shortValue();
    }

    public int asInt() {
        return getValue().intValue();
    }

    public long asLong() {
        return getValue().longValue();
    }

    public float asFloat() {
        return getValue().floatValue();
    }

    public double asDouble() {
        return getValue().doubleValue();
    }

    @Override
    public String valueToString(int maxDepth) {
        return getValue().toString();
    }
}
