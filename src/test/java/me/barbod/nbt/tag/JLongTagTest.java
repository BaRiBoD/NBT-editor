package me.barbod.nbt.tag;

import me.barbod.NBTTestCase;

import java.util.Arrays;

public class JLongTagTest extends NBTTestCase {
    public void testCreate() {
        JLongTag t = new JLongTag(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, t.asLong());
        t = new JLongTag();
        assertEquals(JLongTag.ZERO_VALUE, t.asLong());
    }

    public void testSetValue() {
        JLongTag t = new JLongTag();
        t.setValue(123);
        assertEquals(123, t.asLong());
    }

    public void testStringConversion() {
        JLongTag t = new JLongTag(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, t.asLong());
        assertEquals(4, t.getID());
        assertEquals("{\"type\":\"" + t.getClass().getSimpleName() + "\",\"value\":" + Long.MAX_VALUE + "}", t.toString());
    }

    public void testEquals() {
        JLongTag t = new JLongTag(Long.MAX_VALUE);
        JLongTag t2 = new JLongTag(Long.MAX_VALUE);
        assertTrue(t.equals(t2));
        JLongTag t3 = new JLongTag(Long.MIN_VALUE);
        assertFalse(t.equals(t3));
    }

    public void testClone() {
        JLongTag t = new JLongTag(Long.MAX_VALUE);
        JLongTag tc = t.clone();
        assertTrue(t.equals(tc));
        assertFalse(t == tc);
    }

    public void testSerializeDeserialize() {
        JLongTag t = new JLongTag(Long.MAX_VALUE);
        byte[] data = serialize(t);
        assertTrue(Arrays.equals(new byte[]{4, 0, 0, 127, -1, -1, -1, -1, -1, -1, -1}, data));
        JLongTag tt = (JLongTag) deserialize(data);
        assertTrue(t.equals(tt));
    }

    public void testCompareTo() {
        assertEquals(0, new JLongTag(5).compareTo(new JLongTag(5)));
        assertTrue(0 < new JLongTag(7).compareTo(new JLongTag(5)));
        assertTrue(0 > new JLongTag(5).compareTo(new JLongTag(7)));
        assertThrowsRuntimeException(() -> new JLongTag(5).compareTo(null), NullPointerException.class);
    }
}
