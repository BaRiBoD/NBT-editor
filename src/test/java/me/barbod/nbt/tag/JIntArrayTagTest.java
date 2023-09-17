package me.barbod.nbt.tag;

import me.barbod.NBTTestCase;

import java.util.Arrays;

public class JIntArrayTagTest extends NBTTestCase {
    public void testCreate() {
        JIntArrayTag t = new JIntArrayTag(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        assertTrue(Arrays.equals(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE}, t.getValue()));
        t = new JIntArrayTag();
        assertTrue(Arrays.equals(JIntArrayTag.ZERO_VALUE, t.getValue()));
    }

    public void testSetValue() {
        JIntArrayTag t = new JIntArrayTag();
        t.setValue(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        assertTrue(Arrays.equals(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE}, t.getValue()));
    }

    public void testStringConversion() {
        JIntArrayTag t = new JIntArrayTag(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        assertTrue(Arrays.equals(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE}, t.getValue()));
        assertEquals(11, t.getID());
        assertEquals("{\"type\":\"" + t.getClass().getSimpleName() + "\",\"value\":[-2147483648,0,2147483647]}", t.toString());
    }

    public void testEquals() {
        JIntArrayTag t = new JIntArrayTag(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        JIntArrayTag t2 = new JIntArrayTag(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        assertTrue(t.equals(t2));
        JIntArrayTag t3 = new JIntArrayTag(new int[]{Integer.MAX_VALUE, 0, Integer.MIN_VALUE});
        assertFalse(t.equals(t3));
    }

    public void testClone() {
        JIntArrayTag t = new JIntArrayTag(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        JIntArrayTag tc = t.clone();
        assertTrue(t.equals(tc));
        assertFalse(t == tc);
        assertFalse(t.getValue() == tc.getValue());
    }

    public void testSerializeDeserialize() {
        JIntArrayTag t = new JIntArrayTag(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        byte[] data = serialize(t);
        assertTrue(Arrays.equals(new byte[]{11, 0, 0, 0, 0, 0, 3, -128, 0, 0, 0, 0, 0, 0, 0, 127, -1, -1, -1}, data));
        JIntArrayTag tt = (JIntArrayTag) deserialize(data);
        assertTrue(t.equals(tt));
    }

    public void testCompareTo() {
        JIntArrayTag t = new JIntArrayTag(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        JIntArrayTag t2 = new JIntArrayTag(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        JIntArrayTag t3 = new JIntArrayTag(new int[]{Integer.MAX_VALUE, 0, Integer.MIN_VALUE});
        JIntArrayTag t4 = new JIntArrayTag(new int[]{0, Integer.MIN_VALUE});
        assertEquals(0, t.compareTo(t2));
        assertEquals(0, t.compareTo(t3));
        assertTrue(0 < t.compareTo(t4));
        assertTrue(0 > t4.compareTo(t));
        assertThrowsRuntimeException(() -> t.compareTo(null), NullPointerException.class);
    }
}
