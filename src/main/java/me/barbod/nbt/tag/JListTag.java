package me.barbod.nbt.tag;

import me.barbod.io.MaxDepthIO;

import java.util.*;
import java.util.function.Consumer;

public class JListTag<T extends JTag<?>> extends JTag<List<T>> implements Iterable<T>, Comparable<JListTag<T>>, MaxDepthIO {
    public static final byte ID = 9;
    private Class<?> typeClass = null;

    private JListTag(int initialCapacity) {
        super(createEmptyValue(initialCapacity));
    }

    public JListTag(Class<? super T> typeClass, int initialCapacity) {
        this(initialCapacity);
        if (typeClass == JEndTag.class)
            throw new IllegalArgumentException("cannot create ListTag with EndTag elements");
        this.typeClass = Objects.requireNonNull(typeClass);
    }

    public JListTag(Class<? super T> typeClass) throws IllegalArgumentException, NullPointerException {
        this(typeClass, 3);
    }

    private static <T> List<T> createEmptyValue(int initialCapacity) {
        return new ArrayList<>(initialCapacity);
    }

    public static JListTag<?> createUnchecked(Class<?> typeClass) {
        return createUnchecked(typeClass, 3);
    }

    public static JListTag<?> createUnchecked(Class<?> typeClass, int initialCapacity) {
        JListTag<?> list = new JListTag<>(initialCapacity);
        list.typeClass = typeClass;
        return list;
    }

    public Class<?> getTypeClass() {
        return typeClass == null ? JEndTag.class : typeClass;
    }

    public int size() {
        return getValue().size();
    }

    public void remove(int index) {
        getValue().remove(index);
    }

    public void clear() {
        getValue().clear();
    }

    public boolean contains(T t) {
        return getValue().contains(t);
    }

    public boolean containsAll(Collection<JTag<?>> tags) {
        return new HashSet<>(getValue()).containsAll(tags);
    }

    public void sort(Comparator<T> comparator) {
        getValue().sort(comparator);
    }

    public T set(int index, T t) {
        return getValue().set(index, Objects.requireNonNull(t));
    }

    public T get(int index) {
        return getValue().get(index);
    }

    public int indexOf(T t) {
        return getValue().indexOf(t);
    }

    public void add(T t) {
        add(size(), t);
    }

    public void add(int index, T t) {
        Objects.requireNonNull(t);
        if (getTypeClass() == JEndTag.class) {
            typeClass = t.getClass();
        } else if (typeClass != t.getClass()) {
            throw new ClassCastException(
                    String.format("cannot add %s to ListTag<%s>",
                            t.getClass().getSimpleName(),
                            typeClass.getSimpleName()));
        }
        getValue().add(index, t);
    }

    public void addAll(Collection<T> t) {
        for (T tt : t) {
            add(tt);
        }
    }

    public void addAll(int index, Collection<T> t) {
        int i = 0;
        for (T tt : t) {
            add(index + i, tt);
            i++;
        }
    }

    public void addBoolean(boolean value) {
        addUnchecked(new JByteTag(value));
    }

    public void addByte(byte value) {
        addUnchecked(new JByteTag(value));
    }

    public void addShort(short value) {
        addUnchecked(new JShortTag(value));
    }

    public void addInt(int value) {
        addUnchecked(new JIntTag(value));
    }

    public void addLong(long value) {
        addUnchecked(new JLongTag(value));
    }

    public void addFloat(float value) {
        addUnchecked(new JFloatTag(value));
    }

    public void addDouble(double value) {
        addUnchecked(new JDoubleTag(value));
    }

    public void addString(String value) {
        addUnchecked(new JStringTag(value));
    }

    public void addByteArray(byte[] value) {
        addUnchecked(new JByteArrayTag(value));
    }

    public void addIntArray(int[] value) {
        addUnchecked(new JIntArrayTag(value));
    }

    public void addLongArray(long[] value) {
        addUnchecked(new JLongArrayTag(value));
    }


    @SuppressWarnings("unchecked")
    public <L extends JTag<?>> JListTag<L> asTypedList(Class<L> type) {
        checkTypeClass(type);
        return (JListTag<L>) this;
    }

    public JListTag<JByteTag> asByteTagList() {
        return asTypedList(JByteTag.class);
    }

    public JListTag<JShortTag> asShortTagList() {
        return asTypedList(JShortTag.class);
    }

    public JListTag<JIntTag> asIntTagList() {
        return asTypedList(JIntTag.class);
    }

    public JListTag<JLongTag> asLongTagList() {
        return asTypedList(JLongTag.class);
    }

    public JListTag<JFloatTag> asFloatTagList() {
        return asTypedList(JFloatTag.class);
    }

    public JListTag<JDoubleTag> asDoubleTagList() {
        return asTypedList(JDoubleTag.class);
    }

    public JListTag<JStringTag> asStringTagList() {
        return asTypedList(JStringTag.class);
    }

    public JListTag<JByteArrayTag> asByteArrayTagList() {
        return asTypedList(JByteArrayTag.class);
    }

    public JListTag<JIntArrayTag> asIntArrayTagList() {
        return asTypedList(JIntArrayTag.class);
    }

    public JListTag<JLongArrayTag> asLongArrayTagList() {
        return asTypedList(JLongArrayTag.class);
    }

    @SuppressWarnings("unchecked")
    public JListTag<JListTag<?>> asListTagList() {
        checkTypeClass(JListTag.class);
        typeClass = JListTag.class;
        return (JListTag<JListTag<?>>) this;
    }

    public JListTag<JCompoundTag> asCompoundTagList() {
        return asTypedList(JCompoundTag.class);
    }

    @SuppressWarnings("unchecked")
    public void addUnchecked(JTag<?> tag) {
        if (getTypeClass() != JEndTag.class && typeClass != tag.getClass()) {
            throw new IllegalArgumentException(String.format(
                    "cannot add %s to ListTag<%s>",
                    tag.getClass().getSimpleName(), typeClass.getSimpleName()));
        }
        add(size(), (T) tag);
    }

    private void checkTypeClass(Class<?> clazz) {
        if (getTypeClass() != JEndTag.class && typeClass != clazz) {
            throw new ClassCastException(String.format(
                    "cannot cast ListTag<%s> to ListTag<%s>",
                    typeClass.getSimpleName(), clazz.getSimpleName()));
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other) || size() != ((JListTag<?>) other).size() || getTypeClass() != ((JListTag<?>) other)
                .getTypeClass()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!get(i).equals(((JListTag<?>) other).get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTypeClass().hashCode(), getValue().hashCode());
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        getValue().forEach(action);
    }

    @Override
    public int compareTo(JListTag<T> o) {
        return Integer.compare(size(), o.getValue().size());
    }

    @Override
    public Iterator<T> iterator() {
        return getValue().iterator();
    }

    @Override
    @SuppressWarnings("unchecked")
    public JListTag<T> clone() {
        JListTag<T> copy = new JListTag<>(this.size());
        copy.typeClass = typeClass;
        for (T t : getValue()) {
            copy.add((T) t.clone());
        }
        return copy;
    }

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public String valueToString(int maxDepth) {
        StringBuilder stringBuilder = new StringBuilder("{\"type\":\"").append(getTypeClass().getSimpleName()).append("\",\"list\":[");
        for (int i = 0; i < size(); i++) {
            stringBuilder.append(i > 0 ? "," : "").append(get(i).valueToString(decrementMaxDepth(maxDepth)));
        }
        stringBuilder.append("]}");
        return stringBuilder.toString();
    }
}
