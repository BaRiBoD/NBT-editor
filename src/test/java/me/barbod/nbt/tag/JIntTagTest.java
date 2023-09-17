package me.barbod.nbt.tag;

import me.barbod.NBTTestCase;

import java.util.Arrays;

public class JIntTagTest extends NBTTestCase {
    public void testCreate() {
        JIntTag t = new JIntTag(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, t.asInt());
        t = new JIntTag();
        assertEquals(JIntTag.ZERO_VALUE, t.asInt());
    }

    public void testSetValue() {
        JIntTag t = new JIntTag();
        t.setValue(123);
        assertEquals(123, t.asInt());
    }

    public void testStringConversion() {
        JIntTag t = new JIntTag(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, t.asInt());
        assertEquals(Integer.MAX_VALUE, t.asLong());
        assertEquals(3, t.getID());
        assertEquals("{\"type\":\"" + t.getClass().getSimpleName() + "\",\"value\":" + Integer.MAX_VALUE + "}", t.toString());
    }

    public void testEquals() {
        JIntTag t = new JIntTag(Integer.MAX_VALUE);
        JIntTag t2 = new JIntTag(Integer.MAX_VALUE);
        assertTrue(t.equals(t2));
        JIntTag t3 = new JIntTag(Integer.MIN_VALUE);
        assertFalse(t.equals(t3));
    }

    public void testClone() {
        JIntTag t = new JIntTag(Integer.MAX_VALUE);
        JIntTag tc = t.clone();
        assertTrue(t.equals(tc));
        assertFalse(t == tc);
    }

    public void testSerializeDeserialize() {
        JIntTag t = new JIntTag(Integer.MAX_VALUE);
        byte[] data = serialize(t);
        assertTrue(Arrays.equals(new byte[]{3, 0, 0, 127, -1, -1, -1}, data));
        JIntTag tt = (JIntTag) deserialize(data);
        assertTrue(t.equals(tt));
    }

    public void testCompareTo() {
        assertEquals(0, new JIntTag(5).compareTo(new JIntTag(5)));
        assertTrue(0 < new JIntTag(7).compareTo(new JIntTag(5)));
        assertTrue(0 > new JIntTag(5).compareTo(new JIntTag(7)));
        assertThrowsRuntimeException(() -> new JIntTag(5).compareTo(null), NullPointerException.class);
    }
}
