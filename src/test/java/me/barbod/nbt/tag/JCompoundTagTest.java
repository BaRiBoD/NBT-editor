package me.barbod.nbt.tag;

import me.barbod.NBTTestCase;
import me.barbod.io.MaxDepthReachedException;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;

public class JCompoundTagTest extends NBTTestCase {
    private JCompoundTag createCompoundTag() {
        JCompoundTag ct = new JCompoundTag();
        invokeSetValue(ct, new LinkedHashMap<>());
        ct.put("b", new JByteTag(Byte.MAX_VALUE));
        ct.put("str", new JStringTag("foo"));
        ct.put("list", new JListTag<>(JByteTag.class));
        ct.getListTag("list").addByte((byte) 123);
        return ct;
    }

    public void testStringConversion() {
        JCompoundTag ct = createCompoundTag();
        assertEquals("{\"type\":\"CompoundTag\"," +
                "\"value\":{" +
                "\"b\":{\"type\":\"ByteTag\",\"value\":127}," +
                "\"str\":{\"type\":\"StringTag\",\"value\":\"foo\"}," +
                "\"list\":{\"type\":\"ListTag\"," +
                "\"value\":{\"type\":\"ByteTag\",\"list\":[123]}}}}", ct.toString());
    }

    public void testEquals() {
        JCompoundTag ct = createCompoundTag();
        JCompoundTag ct2 = new JCompoundTag();
        ct2.putByte("b", Byte.MAX_VALUE);
        ct2.putString("str", "foo");
        ct2.put("list", new JListTag<>(JByteTag.class));
        ct2.getListTag("list").addByte((byte) 123);
        assertEquals(ct, ct2);

        ct2.getListTag("list").asByteTagList().get(0).setValue((byte) 124);
        assertNotEquals(ct, ct2);

        ct2.remove("str");
        assertNotEquals(ct, ct2);

        assertThrowsNoRuntimeException(() -> ct.equals("blah"));
        assertNotEquals("blah", ct);

        assertEquals(ct, ct);
    }

    public void testHashCode() {
        JCompoundTag t = new JCompoundTag();
        for (int i = 0; i < 256; i++) {
            t.putByte("key_byte" + i, (byte) i);
            t.putShort("key_short" + i, (short) i);
            t.putInt("key_int" + i, i);
            t.putLong("key_long" + i, i);
            t.putFloat("key_float" + i, i * 1.001f);
            t.putDouble("key_double" + i, i * 1.001);
            t.putString("key_string" + i, "value" + i);

            byte[] bArray = new byte[257];
            int[] iArray = new int[257];
            long[] lArray = new long[257];
            for (byte b = -128; b < 127; b++) {
                bArray[b + 128] = b;
                iArray[b + 128] = b;
                lArray[b + 128] = b;
            }
            bArray[256] = (byte) i;
            iArray[256] = i;
            lArray[256] = i;
            t.putByteArray("key_byte_array" + i, bArray);
            t.putIntArray("key_int_array" + i, iArray);
            t.putLongArray("key_long_array" + i, lArray);

            JListTag<JStringTag> l = new JListTag<>(JStringTag.class);
            for (int j = 0; j < 256; j++) {
                l.addString("value" + j);
            }
            l.addString("value" + i);
            t.put("key_list" + i, l);

            JCompoundTag c = new JCompoundTag();
            c.putString("key_string" + i, "value" + i);
            t.put("key_child" + i, c);
        }
        JCompoundTag t2 = new JCompoundTag();
        for (int i = 0; i < 256; i++) {
            t2.putString("key_string" + i, "value" + i);
            t2.putDouble("key_double" + i, i * 1.001);
            t2.putFloat("key_float" + i, i * 1.001f);
            t2.putLong("key_long" + i, i);
            t2.putInt("key_int" + i, i);
            t2.putShort("key_short" + i, (short) i);
            t2.putByte("key_byte" + i, (byte) i);

            byte[] bArray = new byte[257];
            int[] iArray = new int[257];
            long[] lArray = new long[257];
            for (byte b = -128; b < 127; b++) {
                bArray[b + 128] = b;
                iArray[b + 128] = b;
                lArray[b + 128] = b;
            }
            bArray[256] = (byte) i;
            iArray[256] = i;
            lArray[256] = i;
            t2.putByteArray("key_byte_array" + i, bArray);
            t2.putIntArray("key_int_array" + i, iArray);
            t2.putLongArray("key_long_array" + i, lArray);

            JListTag<JStringTag> l = new JListTag<>(JStringTag.class);
            for (int j = 0; j < 256; j++) {
                l.addString("value" + j);
            }
            l.addString("value" + i);
            t2.put("key_list" + i, l);

            JCompoundTag c = new JCompoundTag();
            c.putString("key_string" + i, "value" + i);
            t2.put("key_child" + i, c);
        }

        assertEquals(t, t2);
        assertEquals(t.hashCode(), t2.hashCode());

        t.getCompoundTag("key_child1").remove("key_string1");

        assertNotEquals(t, t2);
        assertNotEquals(t.hashCode(), t2.hashCode());
    }

    public void testClone() {
        JCompoundTag ct = createCompoundTag();
        JCompoundTag cl = ct.clone();
        assertEquals(ct, cl);
        assertNotSame(ct, cl);
        assertNotSame(ct.get("list"), cl.get("list"));
        assertNotSame(invokeGetValue(ct), invokeGetValue(cl));
    }

    public void testClear() {
        JCompoundTag clear = new JCompoundTag();
        clear.putString("test", "blah");
        assertEquals(1, clear.size());
        clear.clear();
        assertEquals(0, clear.size());
    }

    public void testSerializeDeserialize() {
        JCompoundTag ct = createCompoundTag();
        byte[] data = serialize(ct);
        assertArrayEquals(
                new byte[] { 10, 0, 0, 1, 0, 1, 98, 127, 8, 0, 3, 115, 116, 114, 0, 3, 102, 111, 111, 9, 0, 4, 108, 105, 115, 116,
                        1, 0, 0, 0, 1, 123, 0
                }, data);
        JCompoundTag tt = (JCompoundTag) deserialize(data);
        assertEquals(ct, tt);
    }

    public void testCasting() {
        JCompoundTag cc = new JCompoundTag();
        assertNull(cc.get("b", JByteTag.class));
        assertNull(cc.get("b"));
        cc.putByte("b", Byte.MAX_VALUE);
        assertEquals(new JByteTag(Byte.MAX_VALUE), cc.getByteTag("b"));
        assertNull(cc.getByteTag("bb"));
        assertEquals(Byte.MAX_VALUE, cc.get("b", JByteTag.class).asByte());
        assertThrowsRuntimeException(() -> cc.getShort("b"), ClassCastException.class);
        assertEquals(0, cc.getByte("bb"));
        assertTrue(cc.getBoolean("b"));
        cc.putByte("b2", (byte) 0);
        assertFalse(cc.getBoolean("b2"));
        cc.putBoolean("b3", false);
        assertEquals(0, cc.getByte("b3"));
        cc.putBoolean("b4", true);
        assertEquals(1, cc.getByte("b4"));

        cc.putShort("s", Short.MAX_VALUE);
        assertEquals(new JShortTag(Short.MAX_VALUE), cc.getShortTag("s"));
        assertNull(cc.getShortTag("ss"));
        assertEquals(Short.MAX_VALUE, cc.get("s", JShortTag.class).asShort());
        assertThrowsRuntimeException(() -> cc.getInt("s"), ClassCastException.class);
        assertEquals(0, cc.getShort("ss"));

        cc.putInt("i", Integer.MAX_VALUE);
        assertEquals(new JIntTag(Integer.MAX_VALUE), cc.getIntTag("i"));
        assertNull(cc.getIntTag("ii"));
        assertEquals(Integer.MAX_VALUE, cc.get("i", JIntTag.class).asInt());
        assertThrowsRuntimeException(() -> cc.getLong("i"), ClassCastException.class);
        assertEquals(0, cc.getInt("ii"));

        cc.putLong("l", Long.MAX_VALUE);
        assertEquals(new JLongTag(Long.MAX_VALUE), cc.getLongTag("l"));
        assertNull(cc.getLongTag("ll"));
        assertEquals(Long.MAX_VALUE, cc.get("l", JLongTag.class).asLong());
        assertThrowsRuntimeException(() -> cc.getFloat("l"), ClassCastException.class);
        assertEquals(0, cc.getLong("ll"));

        cc.putFloat("f", Float.MAX_VALUE);
        assertEquals(new JFloatTag(Float.MAX_VALUE), cc.getFloatTag("f"));
        assertNull(cc.getFloatTag("ff"));
        assertEquals(Float.MAX_VALUE, cc.get("f", JFloatTag.class).asFloat());
        assertThrowsRuntimeException(() -> cc.getDouble("f"), ClassCastException.class);
        assertEquals(0.0F, cc.getFloat("ff"));

        cc.putDouble("d", Double.MAX_VALUE);
        assertEquals(new JDoubleTag(Double.MAX_VALUE), cc.getDoubleTag("d"));
        assertNull(cc.getDoubleTag("dd"));
        assertEquals(Double.MAX_VALUE, cc.get("d", JDoubleTag.class).asDouble());
        assertThrowsRuntimeException(() -> cc.getString("d"), ClassCastException.class);
        assertEquals(0.0D, cc.getDouble("dd"));

        cc.putString("st", "foo");
        assertEquals(new JStringTag("foo"), cc.getStringTag("st"));
        assertNull(cc.getStringTag("stst"));
        assertEquals("foo", cc.get("st", JStringTag.class).getValue());
        assertThrowsRuntimeException(() -> cc.getByteArray("st"), ClassCastException.class);
        assertEquals("", cc.getString("stst"));

        cc.putByteArray("ba", new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE});
        assertEquals(new JByteArrayTag(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE}), cc.getByteArrayTag("ba"));
        assertNull(cc.getByteArrayTag("baba"));
        assertArrayEquals(new byte[] { Byte.MIN_VALUE, 0, Byte.MAX_VALUE }, cc.get("ba", JByteArrayTag.class).getValue());
        assertThrowsRuntimeException(() -> cc.getIntArray("ba"), ClassCastException.class);
        assertArrayEquals(new byte[0], cc.getByteArray("baba"));

        cc.putIntArray("ia", new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        assertEquals(new JIntArrayTag(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE}), cc.getIntArrayTag("ia"));
        assertNull(cc.getIntArrayTag("iaia"));
        assertArrayEquals(new int[] { Integer.MIN_VALUE, 0, Integer.MAX_VALUE }, cc.get("ia", JIntArrayTag.class).getValue());
        assertThrowsRuntimeException(() -> cc.getLongArray("ia"), ClassCastException.class);
        assertArrayEquals(new int[0], cc.getIntArray("iaia"));

        cc.putLongArray("la", new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE});
        assertEquals(new JLongArrayTag(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE}), cc.getLongArrayTag("la"));
        assertNull(cc.getLongArrayTag("lala"));
        assertArrayEquals(new long[] { Long.MIN_VALUE, 0, Long.MAX_VALUE }, cc.get("la", JLongArrayTag.class).getValue());
        assertThrowsRuntimeException(() -> cc.getListTag("la"), ClassCastException.class);
        assertArrayEquals(new long[0], cc.getLongArray("lala"));

        cc.put("li", new JListTag<>(JIntTag.class));
        assertEquals(new JListTag<>(JIntTag.class), cc.getListTag("li"));
        assertNull(cc.getListTag("lili"));
        assertThrowsRuntimeException(() -> cc.getCompoundTag("li"), ClassCastException.class);

        cc.put("co", new JCompoundTag());
        assertEquals(new JCompoundTag(), cc.getCompoundTag("co"));
        assertNull(cc.getCompoundTag("coco"));
        assertThrowsRuntimeException(() -> cc.getByte("co"), ClassCastException.class);
    }

    public void testCompareTo() {
        JCompoundTag ci = new JCompoundTag();
        ci.putInt("one", 1);
        ci.putInt("two", 2);
        JCompoundTag co = new JCompoundTag();
        co.putInt("three", 3);
        co.putInt("four", 4);
        assertEquals(0, ci.compareTo(co));
        co.putInt("five", 5);
        assertEquals(-1, ci.compareTo(co));
        co.remove("five");
        co.remove("four");
        assertEquals(1, ci.compareTo(co));
        assertThrowsRuntimeException(() -> ci.compareTo(null), NullPointerException.class);
    }

    public void testMaxDepth() {
        JCompoundTag root = new JCompoundTag();
        JCompoundTag rec = root;
        for (int i = 0; i < JTag.DEFAULT_MAX_DEPTH + 1; i++) {
            JCompoundTag c = new JCompoundTag();
            rec.put("c" + i, c);
            rec = c;
        }
        assertThrowsRuntimeException(() -> serialize(root), MaxDepthReachedException.class);
        assertThrowsRuntimeException(() -> deserializeFromFile("max_depth_reached.dat"), MaxDepthReachedException.class);
        assertThrowsNoRuntimeException(() -> root.toString(JTag.DEFAULT_MAX_DEPTH + 1));
        assertThrowsRuntimeException(root::toString, MaxDepthReachedException.class);
        assertThrowsRuntimeException(() -> root.valueToString(-1), IllegalArgumentException.class);
    }

    public void testRecursion() {
        JCompoundTag recursive = new JCompoundTag();
        recursive.put("recursive", recursive);
        assertThrowsRuntimeException(() -> serialize(recursive), MaxDepthReachedException.class);
        assertThrowsRuntimeException(recursive::toString, MaxDepthReachedException.class);
    }

    public void testEntrySet() {
        JCompoundTag e = new JCompoundTag();
        e.putInt("int", 123);
        for (Map.Entry<String, JTag<?>> en : e.entrySet()) {
            assertThrowsRuntimeException(() -> en.setValue(null), NullPointerException.class);
            assertThrowsNoRuntimeException(() -> en.setValue(new JIntTag(321)));
        }
        assertEquals(1, e.size());
        assertEquals(321, e.getInt("int"));
    }

    public void testContains() {
        JCompoundTag ct = createCompoundTag();
        assertEquals(3, ct.size());
        assertTrue(ct.containsKey("b"));
        assertTrue(ct.containsKey("str"));
        assertTrue(ct.containsKey("list"));
        assertFalse(ct.containsKey("invalid"));
        assertTrue(ct.containsValue(new JStringTag("foo")));
        JListTag<JByteTag> l = new JListTag<>(JByteTag.class);
        l.addByte((byte) 123);
        assertTrue(ct.containsValue(l));
        assertTrue(ct.containsValue(new JByteTag(Byte.MAX_VALUE)));
        assertFalse(ct.containsValue(new JByteTag(Byte.MIN_VALUE)));
        assertFalse(ct.containsKey("blah"));
    }

    public void testIterator() {
        JCompoundTag ct = createCompoundTag();
        for (JTag<?> t : ct.values()) {
            assertNotNull(t);
        }
        ct.values().remove(new JStringTag("foo"));
        assertFalse(ct.containsKey("str"));
        assertThrowsRuntimeException(() -> ct.values().add(new JStringTag("foo")), UnsupportedOperationException.class);
        ct.putString("str", "foo");
        for (String k : ct.keySet()) {
            assertNotNull(k);
            assertTrue(ct.containsKey(k));
        }
        ct.keySet().remove("str");
        assertFalse(ct.containsKey("str"));
        assertThrowsRuntimeException(() -> ct.keySet().add("str"), UnsupportedOperationException.class);
        ct.putString("str", "foo");
        for (Map.Entry<String, JTag<?>> e : ct.entrySet()) {
            assertNotNull(e.getKey());
            assertNotNull(e.getValue());
            assertThrowsRuntimeException(() -> e.setValue(null), NullPointerException.class);
            if (e.getKey().equals("str")) {
                assertThrowsNoRuntimeException(() -> e.setValue(new JStringTag("bar")));
            }
        }
        assertTrue(ct.containsKey("str"));
        assertEquals("bar", ct.getString("str"));
        for (Map.Entry<String, JTag<?>> e : ct) {
            assertNotNull(e.getKey());
            assertNotNull(e.getValue());
            assertThrowsRuntimeException(() -> e.setValue(null), NullPointerException.class);
            if (e.getKey().equals("str")) {
                assertThrowsNoRuntimeException(() -> e.setValue(new JStringTag("foo")));
            }
        }
        assertTrue(ct.containsKey("str"));
        assertEquals("foo", ct.getString("str"));
        ct.forEach((k, v) -> {
            assertNotNull(k);
            assertNotNull(v);
        });
        assertEquals(3, ct.size());
    }

    public void testPutIfNotNull() {
        JCompoundTag ct = new JCompoundTag();
        assertEquals(0, ct.size());
        ct.putIfNotNull("foo", new JStringTag("bar"));
        ct.putIfNotNull("bar", null);
        assertEquals(1, ct.size());
        assertEquals("bar", ct.getString("foo"));
    }
}
