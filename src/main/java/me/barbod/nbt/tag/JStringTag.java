package me.barbod.nbt.tag;

public class JStringTag extends JTag<String> implements Comparable<JStringTag> {
    public static final byte ID = 8;
    public static final String ZERO_VALUE = "";

    public JStringTag(String value) {
        super(value);
    }

    public JStringTag() {
        this(ZERO_VALUE);
    }

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public String getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
    }

    @Override
    public String valueToString(int maxDepth) {
        return escapeString(getValue(), false);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && getValue().equals(((JStringTag) other).getValue());
    }

    @Override
    public int compareTo(JStringTag o) {
        return getValue().compareTo(o.getValue());
    }

    @Override
    public JStringTag clone() {
        return new JStringTag(getValue());
    }
}
