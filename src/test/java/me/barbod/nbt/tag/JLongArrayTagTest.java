package me.barbod.nbt.tag;

import me.barbod.NBTTestCase;

import java.util.Arrays;

public class JLongArrayTagTest extends NBTTestCase {
    public void testCreate() {
        JLongArrayTag t = new JLongArrayTag(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE});
        assertTrue(Arrays.equals(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE}, t.getValue()));
        t = new JLongArrayTag();
        assertTrue(Arrays.equals(JLongArrayTag.ZERO_VALUE, t.getValue()));
    }

    public void testSetValue() {
        JLongArrayTag t = new JLongArrayTag();
        t.setValue(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE});
        assertTrue(Arrays.equals(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE}, t.getValue()));
    }

    public void testStringConversion() {
        JLongArrayTag t = new JLongArrayTag(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE});
        assertTrue(Arrays.equals(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE}, t.getValue()));
        assertEquals(12, t.getID());
        assertEquals("{\"type\":\"" + t.getClass().getSimpleName() + "\",\"value\":[-9223372036854775808,0,9223372036854775807]}", t.toString());
    }

    public void testEquals() {
        JLongArrayTag t = new JLongArrayTag(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE});
        JLongArrayTag t2 = new JLongArrayTag(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE});
        assertTrue(t.equals(t2));
        JLongArrayTag t3 = new JLongArrayTag(new long[]{Long.MAX_VALUE, 0, Long.MIN_VALUE});
        assertFalse(t.equals(t3));
    }

    public void testClone() {
        JLongArrayTag t = new JLongArrayTag(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE});
        JLongArrayTag tc = t.clone();
        assertTrue(t.equals(tc));
        assertFalse(t == tc);
        assertFalse(t.getValue() == tc.getValue());
    }

    public void testSerializeDeserialize() {
        JLongArrayTag t = new JLongArrayTag(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE});
        byte[] data = serialize(t);
        assertTrue(Arrays.equals(new byte[]{12, 0, 0, 0, 0, 0, 3, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, -1, -1, -1, -1, -1, -1, -1}, data));
        JLongArrayTag tt = (JLongArrayTag) deserialize(data);
        assertTrue(t.equals(tt));
    }

    public void testCompareTo() {
        JLongArrayTag t = new JLongArrayTag(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE});
        JLongArrayTag t2 = new JLongArrayTag(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE});
        JLongArrayTag t3 = new JLongArrayTag(new long[]{Long.MAX_VALUE, 0, Long.MIN_VALUE});
        JLongArrayTag t4 = new JLongArrayTag(new long[]{0, Long.MIN_VALUE});
        assertEquals(0, t.compareTo(t2));
        assertEquals(0, t.compareTo(t3));
        assertTrue(0 < t.compareTo(t4));
        assertTrue(0 > t4.compareTo(t));
        assertThrowsRuntimeException(() -> t.compareTo(null), NullPointerException.class);
    }
}
