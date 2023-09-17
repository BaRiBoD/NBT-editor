package me.barbod.nbt.tag;

import me.barbod.io.MaxDepthIO;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

public class JCompoundTag extends JTag<Map<String, JTag<?>>>
        implements Iterable<Map.Entry<String, JTag<?>>>, Comparable<JCompoundTag>, MaxDepthIO {
    public static final byte ID = 10;

    public JCompoundTag() {
        super(createEmptyValue());
    }

    public JCompoundTag(int initialCapacity) {
        super(new HashMap<>(initialCapacity));
    }

    @Override
    public byte getID() {
        return ID;
    }

    private static Map<String, JTag<?>> createEmptyValue() {
        return new HashMap<>(8);
    }

    public int size() {
        return getValue().size();
    }

    public JTag<?> remove(String key) {
        return getValue().remove(key);
    }

    public void clear() {
        getValue().clear();
    }

    public boolean containsKey(String key) {
        return getValue().containsKey(key);
    }

    public boolean containsValue(JTag<?> value) {
        return getValue().containsValue(value);
    }

    public Collection<JTag<?>> values() {
        return getValue().values();
    }

    public Set<String> keySet() {
        return getValue().keySet();
    }

    public Set<Map.Entry<String, JTag<?>>> entrySet() {
        return new NonNullEntrySet<>(getValue().entrySet());
    }

    @Override
    public Iterator<Map.Entry<String, JTag<?>>> iterator() {
        return entrySet().iterator();
    }

    public void forEach(BiConsumer<String, JTag<?>> action) {
        getValue().forEach(action);
    }

    public <C extends JTag<?>> C get(String key, Class<C> type) {
        JTag<?> t = getValue().get(key);
        if (t != null) {
            return type.cast(t);
        }
        return null;
    }

    public JTag<?> get(String key) {
        return getValue().get(key);
    }

    public JNumberTag<?> getNumberTag(String key) {
        return (JNumberTag<?>) getValue().get(key);
    }

    public Number getNumber(String key) {
        return getNumberTag(key).getValue();
    }

    public JByteTag getByteTag(String key) {
        return get(key, JByteTag.class);
    }

    public JShortTag getShortTag(String key) {
        return get(key, JShortTag.class);
    }

    public JIntTag getIntTag(String key) {
        return get(key, JIntTag.class);
    }

    public JLongTag getLongTag(String key) {
        return get(key, JLongTag.class);
    }

    public JFloatTag getFloatTag(String key) {
        return get(key, JFloatTag.class);
    }

    public JDoubleTag getDoubleTag(String key) {
        return get(key, JDoubleTag.class);
    }

    public JStringTag getStringTag(String key) {
        return get(key, JStringTag.class);
    }

    public JByteArrayTag getByteArrayTag(String key) {
        return get(key, JByteArrayTag.class);
    }

    public JIntArrayTag getIntArrayTag(String key) {
        return get(key, JIntArrayTag.class);
    }

    public JLongArrayTag getLongArrayTag(String key) {
        return get(key, JLongArrayTag.class);
    }

    public JListTag<?> getListTag(String key) {
        return get(key, JListTag.class);
    }

    public JCompoundTag getCompoundTag(String key) {
        return get(key, JCompoundTag.class);
    }

    public boolean getBoolean(String key) {
        JTag<?> t = get(key);
        return t instanceof JByteTag && ((JByteTag) t).asByte() > 0;
    }

    public byte getByte(String key) {
        JByteTag t = getByteTag(key);
        return t == null ? JByteTag.ZERO_VALUE : t.asByte();
    }

    public short getShort(String key) {
        JShortTag t = getShortTag(key);
        return t == null ? JShortTag.ZERO_VALUE : t.asShort();
    }

    public int getInt(String key) {
        JIntTag t = getIntTag(key);
        return t == null ? JIntTag.ZERO_VALUE : t.asInt();
    }

    public long getLong(String key) {
        JLongTag t = getLongTag(key);
        return t == null ? JLongTag.ZERO_VALUE : t.asLong();
    }

    public float getFloat(String key) {
        JFloatTag t = getFloatTag(key);
        return t == null ? JFloatTag.ZERO_VALUE : t.asFloat();
    }

    public double getDouble(String key) {
        JDoubleTag t = getDoubleTag(key);
        return t == null ? JDoubleTag.ZERO_VALUE : t.asDouble();
    }

    public String getString(String key) {
        JStringTag t = getStringTag(key);
        return t == null ? JStringTag.ZERO_VALUE : t.getValue();
    }

    public byte[] getByteArray(String key) {
        JByteArrayTag t = getByteArrayTag(key);
        return t == null ? JByteArrayTag.ZERO_VALUE : t.getValue();
    }

    public int[] getIntArray(String key) {
        JIntArrayTag t = getIntArrayTag(key);
        return t == null ? JIntArrayTag.ZERO_VALUE : t.getValue();
    }

    public long[] getLongArray(String key) {
        JLongArrayTag t = getLongArrayTag(key);
        return t == null ? JLongArrayTag.ZERO_VALUE : t.getValue();
    }

    public JTag<?> put(String key, JTag<?> tag) {
        return getValue().put(Objects.requireNonNull(key), Objects.requireNonNull(tag));
    }

    public JTag<?> putIfNotNull(String key, JTag<?> tag) {
        if (tag == null) {
            return this;
        }
        return put(key, tag);
    }

    public JTag<?> putBoolean(String key, boolean value) {
        return put(key, new JByteTag(value));
    }

    public JTag<?> putByte(String key, byte value) {
        return put(key, new JByteTag(value));
    }

    public JTag<?> putShort(String key, short value) {
        return put(key, new JShortTag(value));
    }

    public JTag<?> putInt(String key, int value) {
        return put(key, new JIntTag(value));
    }

    public JTag<?> putLong(String key, long value) {
        return put(key, new JLongTag(value));
    }

    public JTag<?> putFloat(String key, float value) {
        return put(key, new JFloatTag(value));
    }

    public JTag<?> putDouble(String key, double value) {
        return put(key, new JDoubleTag(value));
    }

    public JTag<?> putString(String key, String value) {
        return put(key, new JStringTag(value));
    }

    public JTag<?> putByteArray(String key, byte[] value) {
        return put(key, new JByteArrayTag(value));
    }

    public JTag<?> putIntArray(String key, int[] value) {
        return put(key, new JIntArrayTag(value));
    }

    public JTag<?> putLongArray(String key, long[] value) {
        return put(key, new JLongArrayTag(value));
    }

    @Override
    public String valueToString(int maxDepth) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, JTag<?>> e : getValue().entrySet()) {
            sb.append(first ? "" : ",")
                    .append(escapeString(e.getKey(), false)).append(":")
                    .append(e.getValue().toString(decrementMaxDepth(maxDepth)));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other) || size() != ((JCompoundTag) other).size()) {
            return false;
        }
        for (Map.Entry<String, JTag<?>> e : getValue().entrySet()) {
            JTag<?> v;
            if ((v = ((JCompoundTag) other).get(e.getKey())) == null || !e.getValue().equals(v)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(JCompoundTag o) {
        return Integer.compare(size(), o.getValue().size());
    }

    @Override
    public JCompoundTag clone() {
        JCompoundTag copy = new JCompoundTag((int) Math.ceil(getValue().size() / 0.75f));
        for (Map.Entry<String, JTag<?>> e : getValue().entrySet()) {
            copy.put(e.getKey(), e.getValue().clone());
        }
        return copy;
    }
}
