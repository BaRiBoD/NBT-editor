package me.barbod.nbt.tag;

import java.lang.reflect.Array;

public abstract class JArrayTag<T> extends JTag<T> {
    public JArrayTag(T value) {
        super(value);
        if (!value.getClass().isArray())
            throw new UnsupportedOperationException("type of array tag must be an array");
    }

    public int length() {
        return Array.getLength(getValue());
    }

    @Override
    public T getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
    }

    @Override
    public String valueToString(int maxDepth) {
        return arrayToString("", "");
    }

    protected String arrayToString(String prefix, String suffix) {
        StringBuilder stringBuilder = new StringBuilder("[").append(prefix).append("".equals(prefix) ? "" : ";");
        for (int i = 0; i < length(); i++) {
            stringBuilder.append(i == 0 ? "" : ",").append(Array.get(getValue(), i)).append(suffix);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
