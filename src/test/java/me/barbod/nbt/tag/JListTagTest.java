package me.barbod.nbt.tag;

import junit.framework.TestCase;
import me.barbod.NBTTestCase;
import me.barbod.io.MaxDepthReachedException;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.Assert.assertNotEquals;

public class JListTagTest extends NBTTestCase {
    public void testCreateInvalidList() {
        assertThrowsException(() -> new JListTag<>(JEndTag.class), IllegalArgumentException.class);
        assertThrowsException(() -> new JListTag<>(null), NullPointerException.class);
    }

    private JListTag<JByteTag> createListTag() {
        JListTag<JByteTag> bl = new JListTag<>(JByteTag.class);
        bl.add(new JByteTag(Byte.MIN_VALUE));
        bl.add(new JByteTag((byte) 0));
        bl.add(new JByteTag(Byte.MAX_VALUE));
        return bl;
    }

    public void testStringConversion() {
        JListTag<JByteTag> bl = createListTag();
        assertTrue(3 == bl.size());
        assertEquals(Byte.MIN_VALUE, bl.get(0).asByte());
        assertEquals(0, bl.get(1).asByte());
        assertEquals(Byte.MAX_VALUE, bl.get(2).asByte());
        assertEquals("{\"type\":\"ListTag\"," +
                "\"value\":{" +
                "\"type\":\"ByteTag\"," +
                "\"list\":[" +
                "-128," +
                "0," +
                "127]}}", bl.toString());
        JListTag<?> lu = JListTag.createUnchecked(null);
        assertEquals("{\"type\":\"ListTag\",\"value\":{\"type\":\"EndTag\",\"list\":[]}}", lu.toString());
    }

    public void testEquals() {
        JListTag<JByteTag> bl = createListTag();

        JListTag<JByteTag> bl2 = new JListTag<>(JByteTag.class);
        bl2.addByte(Byte.MIN_VALUE);
        bl2.addByte((byte) 0);
        bl2.addByte(Byte.MAX_VALUE);
        assertTrue(bl.equals(bl2));

        JListTag<JByteTag> bl3 = new JListTag<>(JByteTag.class);
        bl2.addByte(Byte.MAX_VALUE);
        bl2.addByte((byte) 0);
        bl2.addByte(Byte.MIN_VALUE);
        assertFalse(bl.equals(bl3));

        JListTag<JByteTag> bl4 = new JListTag<>(JByteTag.class);
        bl2.addByte(Byte.MIN_VALUE);
        bl2.addByte((byte) 0);
        assertFalse(bl.equals(bl4));

        assertEquals(bl, bl);

        JListTag<JIntTag> il = new JListTag<>(JIntTag.class);
        il.addInt(1);
        il.clear();
        assertEquals(il, new JListTag<>(JIntTag.class));

        JListTag<?> lu = JListTag.createUnchecked(null);
        JListTag<?> lu2 = JListTag.createUnchecked(null);
        assertTrue(lu.equals(lu2));
        lu2.asIntTagList();
        assertTrue(lu.equals(lu2));
        JListTag<JIntTag> lie = new JListTag<>(JIntTag.class);
        assertFalse(lu.equals(lie));
        lu.asIntTagList();
        assertFalse(lie.equals(lu));
    }

    public void testHashCode() {
        JListTag<JStringTag> ls = new JListTag<>(JStringTag.class);
        JListTag<JIntTag> li = new JListTag<>(JIntTag.class);
        assertNotEquals(ls.hashCode(), li.hashCode());
        JListTag<JStringTag> ls2 = new JListTag<>(JStringTag.class);
        assertEquals(ls.hashCode(), ls2.hashCode());
    }

    public void testClone() {
        JListTag<JIntTag> i = new JListTag<>(JIntTag.class);
        JListTag<JIntTag> c = i.clone();
        assertThrowsRuntimeException(() -> c.addString("wrong type in clone"), IllegalArgumentException.class);
        assertThrowsNoRuntimeException(() -> c.addInt(123));

        assertFalse(i.equals(c));
        c.clear();
        assertTrue(i.equals(c));
        assertFalse(invokeGetValue(i) == invokeGetValue(c));

        i.addInt(123);
        JListTag<JIntTag> c2 = i.clone();
        assertTrue(i.equals(c2));
        assertFalse(invokeGetValue(i) == invokeGetValue(c2));
    }

    public void testSerializeDeserialize() {
        JListTag<JByteTag> bl = createListTag();
        byte[] data = serialize(bl);
        assertTrue(Arrays.equals(new byte[]{9, 0, 0, 1, 0, 0, 0, 3, -128, 0, 127}, data));
        JListTag<?> tt = (JListTag<?>) deserialize(data);
        assertNotNull(tt);
        JListTag<JByteTag> ttt = tt.asByteTagList();
        assertTrue(bl.equals(ttt));
    }

    public void testSerializeDeserializeEmptyList() {
        JListTag<JIntTag> empty = new JListTag<>(JIntTag.class);
        byte[] data = serialize(empty);
        assertTrue(Arrays.equals(new byte[]{9, 0, 0, 3, 0, 0, 0, 0}, data));
        JListTag<?> et = (JListTag<?>) deserialize(data);
        assertNotNull(et);
        assertThrowsRuntimeException(et::asByteTagList, ClassCastException.class);
    }

    public void testCasting() {
        JListTag<JByteTag> b = new JListTag<>(JByteTag.class);
        assertThrowsRuntimeException(() -> b.addShort((short) 123), IllegalArgumentException.class);
        assertThrowsNoRuntimeException(() -> b.addByte((byte) 123));
        assertThrowsNoRuntimeException(b::asByteTagList);
        assertThrowsRuntimeException(b::asShortTagList, ClassCastException.class);
        assertThrowsNoRuntimeException(() -> b.asTypedList(JByteTag.class));
        assertThrowsRuntimeException(() -> b.asTypedList(JShortTag.class), ClassCastException.class);
        b.remove(0);

        assertEquals(JByteTag.class, b.getTypeClass());
        assertThrowsRuntimeException(() -> b.addShort((short) 1), IllegalArgumentException.class);
        assertEquals(JByteTag.class, b.getTypeClass());
        b.clear();
        assertEquals(JByteTag.class, b.getTypeClass());

        JListTag<?> l = JListTag.createUnchecked(null);
        assertThrowsNoRuntimeException(l::asByteTagList);
        l.addByte(Byte.MAX_VALUE);
        assertThrowsNoRuntimeException(l::asByteTagList);
        assertThrowsRuntimeException(l::asShortTagList, ClassCastException.class);

        l = JListTag.createUnchecked(null);
        l.addShort(Short.MAX_VALUE);
        assertThrowsNoRuntimeException(l::asShortTagList);
        assertThrowsRuntimeException(l::asIntTagList, ClassCastException.class);

        l = JListTag.createUnchecked(null);
        l.addInt(Integer.MAX_VALUE);
        assertThrowsNoRuntimeException(l::asIntTagList);
        assertThrowsRuntimeException(l::asLongTagList, ClassCastException.class);

        l = JListTag.createUnchecked(null);
        l.addLong(Long.MAX_VALUE);
        assertThrowsNoRuntimeException(l::asLongTagList);
        assertThrowsRuntimeException(l::asFloatTagList, ClassCastException.class);

        l = JListTag.createUnchecked(null);
        l.addFloat(Float.MAX_VALUE);
        assertThrowsNoRuntimeException(l::asFloatTagList);
        assertThrowsRuntimeException(l::asDoubleTagList, ClassCastException.class);

        l = JListTag.createUnchecked(null);
        l.addDouble(Double.MAX_VALUE);
        assertThrowsNoRuntimeException(l::asDoubleTagList);
        assertThrowsRuntimeException(l::asStringTagList, ClassCastException.class);

        l = JListTag.createUnchecked(null);
        l.addString("foo");
        assertThrowsNoRuntimeException(l::asStringTagList);
        assertThrowsRuntimeException(l::asByteArrayTagList, ClassCastException.class);

        l = JListTag.createUnchecked(null);
        l.addByteArray(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE});
        assertThrowsNoRuntimeException(l::asByteArrayTagList);
        assertThrowsRuntimeException(l::asIntArrayTagList, ClassCastException.class);

        l = JListTag.createUnchecked(null);
        l.addIntArray(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        assertThrowsNoRuntimeException(l::asIntArrayTagList);
        assertThrowsRuntimeException(l::asLongArrayTagList, ClassCastException.class);

        l = JListTag.createUnchecked(null);
        l.addLongArray(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE});
        assertThrowsNoRuntimeException(l::asLongArrayTagList);
        assertThrowsRuntimeException(l::asListTagList, ClassCastException.class);

        JListTag<JListTag<?>> lis = new JListTag<>(JListTag.class);
        lis.add(new JListTag<>(JIntTag.class));
        assertThrowsNoRuntimeException(lis::asListTagList);
        assertThrowsRuntimeException(lis::asCompoundTagList, ClassCastException.class);

        JListTag<JCompoundTag> lco = new JListTag<>(JCompoundTag.class);
        lco.add(new JCompoundTag());
        assertThrowsNoRuntimeException(lco::asCompoundTagList);
        assertThrowsRuntimeException(lco::asByteTagList, ClassCastException.class);

        JListTag<?> lg = JListTag.createUnchecked(null);
        JListTag<JByteTag> lb = assertThrowsNoRuntimeException(lg::asByteTagList);
        assertEquals(lb, lg);
        assertThrowsNoException(lg::asShortTagList);
    }

    public void testCompareTo() {
        JListTag<JIntTag> li = new JListTag<>(JIntTag.class);
        li.addInt(1);
        li.addInt(2);
        JListTag<JIntTag> lo = new JListTag<>(JIntTag.class);
        lo.addInt(3);
        lo.addInt(4);
        assertEquals(0, li.compareTo(lo));
        lo.addInt(5);
        assertEquals(-1, li.compareTo(lo));
        lo.remove(2);
        lo.remove(1);
        assertEquals(1, li.compareTo(lo));
        assertThrowsRuntimeException(() -> li.compareTo(null), NullPointerException.class);
    }

    public void testMaxDepth() {
        JListTag<JListTag<?>> root = new JListTag<>(JListTag.class);
        JListTag<JListTag<?>> rec = root;
        for (int i = 0; i < JTag.DEFAULT_MAX_DEPTH + 1; i++) {
            JListTag<JListTag<?>> l = new JListTag<>(JListTag.class);
            rec.add(l);
            rec = l;
        }
        assertThrowsRuntimeException(() -> serialize(root), MaxDepthReachedException.class);
        assertThrowsRuntimeException(() -> deserializeFromFile("max_depth_reached.dat"), MaxDepthReachedException.class);
        assertThrowsRuntimeException(root::toString, MaxDepthReachedException.class);
        assertThrowsRuntimeException(() -> root.valueToString(-1), IllegalArgumentException.class);
    }

    public void testRecursion() {
        JListTag<JListTag<?>> recursive = new JListTag<>(JListTag.class);
        recursive.add(recursive);
        assertThrowsRuntimeException(() -> serialize(recursive), MaxDepthReachedException.class);
        assertThrowsRuntimeException(recursive::toString, MaxDepthReachedException.class);
    }

    public void testContains() {
        JListTag<JIntTag> l = new JListTag<>(JIntTag.class);
        l.addInt(1);
        l.addInt(2);
        assertTrue(l.contains(new JIntTag(1)));
        assertFalse(l.contains(new JIntTag(3)));
        assertTrue(l.containsAll(Arrays.asList(new JIntTag(1), new JIntTag(2))));
        assertFalse(l.containsAll(Arrays.asList(new JIntTag(1), new JIntTag(3))));
    }

    public void testSort() {
        JListTag<JIntTag> l = new JListTag<>(JIntTag.class);
        l.addInt(2);
        l.addInt(1);
        l.sort(Comparator.comparingInt(JNumberTag::asInt));
        assertEquals(1, l.get(0).asInt());
        assertEquals(2, l.get(1).asInt());
    }

    public void testIterator() {
        JListTag<JIntTag> l = new JListTag<>(JIntTag.class);
        l.addInt(1);
        l.addInt(2);
        for (JIntTag i : l) {
            assertNotNull(i);
        }
        l.forEach(TestCase::assertNotNull);
    }

    public void testSet() {
        JListTag<JByteTag> l = createListTag();
        l.set(1, new JByteTag((byte) 5));
        assertEquals(3, l.size());
        assertEquals(5, l.get(1).asByte());
        assertThrowsRuntimeException(() -> l.set(0, null), NullPointerException.class);
    }

    public void testAddAll() {
        JListTag<JByteTag> l = createListTag();
        l.addAll(Arrays.asList(new JByteTag((byte) 5), new JByteTag((byte) 7)));
        assertEquals(5, l.size());
        assertEquals(5, l.get(3).asByte());
        assertEquals(7, l.get(4).asByte());
        l.addAll(1, Arrays.asList(new JByteTag((byte) 9), new JByteTag((byte) 11)));
        assertEquals(7, l.size());
        assertEquals(9, l.get(1).asByte());
        assertEquals(11, l.get(2).asByte());
    }

    public void testIndexOf() {
        JListTag<JByteTag> l = createListTag();
        assertEquals(0, l.indexOf(new JByteTag(Byte.MIN_VALUE)));
        assertEquals(1, l.indexOf(new JByteTag((byte) 0)));
        assertEquals(2, l.indexOf(new JByteTag(Byte.MAX_VALUE)));
        l.addByte((byte) 0);
        assertEquals(1, l.indexOf(new JByteTag((byte) 0)));
    }

    public void testAdd() {
        JListTag<JByteTag> l = new JListTag<>(JByteTag.class);
        l.addBoolean(true);
        assertThrowsRuntimeException(() -> l.addShort((short) 5), IllegalArgumentException.class);
        assertEquals(1, l.size());
        assertEquals(1, l.get(0).asByte());
        l.addByte(Byte.MAX_VALUE);
        assertEquals(2, l.size());
        assertEquals(Byte.MAX_VALUE, l.get(1).asByte());
        JListTag<JShortTag> s = new JListTag<>(JShortTag.class);
        s.addShort(Short.MAX_VALUE);
        assertEquals(1, s.size());
        assertEquals(Short.MAX_VALUE, s.get(0).asShort());
        JListTag<JIntTag> i = new JListTag<>(JIntTag.class);
        i.addInt(Integer.MAX_VALUE);
        assertEquals(1, i.size());
        assertEquals(Integer.MAX_VALUE, i.get(0).asInt());
        JListTag<JLongTag> lo = new JListTag<>(JLongTag.class);
        lo.addLong(Long.MAX_VALUE);
        assertEquals(1, lo.size());
        assertEquals(Long.MAX_VALUE, lo.get(0).asLong());
        JListTag<JFloatTag> f = new JListTag<>(JFloatTag.class);
        f.addFloat(Float.MAX_VALUE);
        assertEquals(1, f.size());
        assertEquals(Float.MAX_VALUE, f.get(0).asFloat());
        JListTag<JDoubleTag> d = new JListTag<>(JDoubleTag.class);
        d.addDouble(Double.MAX_VALUE);
        assertEquals(1, d.size());
        assertEquals(Double.MAX_VALUE, d.get(0).asDouble());
        JListTag<JStringTag> st = new JListTag<>(JStringTag.class);
        st.addString("foo");
        assertEquals(1, st.size());
        assertEquals("foo", st.get(0).getValue());
        JListTag<JByteArrayTag> ba = new JListTag<>(JByteArrayTag.class);
        ba.addByteArray(new byte[] {Byte.MIN_VALUE, 0, Byte.MAX_VALUE});
        assertEquals(1, ba.size());
        assertTrue(Arrays.equals(new byte[] {Byte.MIN_VALUE, 0, Byte.MAX_VALUE}, ba.get(0).getValue()));
        JListTag<JIntArrayTag> ia = new JListTag<>(JIntArrayTag.class);
        ia.addIntArray(new int[] {Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        assertEquals(1, ia.size());
        assertTrue(Arrays.equals(new int[] {Integer.MIN_VALUE, 0, Integer.MAX_VALUE}, ia.get(0).getValue()));
        JListTag<JLongArrayTag> la = new JListTag<>(JLongArrayTag.class);
        la.addLongArray(new long[] {Long.MIN_VALUE, 0, Long.MAX_VALUE});
        assertEquals(1, la.size());
        assertTrue(Arrays.equals(new long[] {Long.MIN_VALUE, 0, Long.MAX_VALUE}, la.get(0).getValue()));
    }
}
