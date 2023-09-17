package me.barbod.nbt.tag;

import me.barbod.NBTTestCase;

import java.util.Arrays;

import static org.junit.Assert.assertNotEquals;

public class JFloatTagTest extends NBTTestCase {
    public void testCreate() {
        JFloatTag t = new JFloatTag(Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, t.asFloat());
        t = new JFloatTag();
        assertEquals(JFloatTag.ZERO_VALUE, t.asFloat());
    }

    public void testSetValue() {
        JFloatTag t = new JFloatTag();
        t.setValue(123.4f);
        assertEquals(123.4f, t.asFloat());
    }

    public void testStringConversion() {
        JFloatTag t = new JFloatTag(Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, t.asFloat());
        assertEquals(5, t.getID());
        assertEquals("{\"type\":\"" + t.getClass().getSimpleName() + "\",\"value\":" + Float.MAX_VALUE + "}", t.toString());
    }

    public void testEquals() {
        JFloatTag t = new JFloatTag(Float.MAX_VALUE);
        assertEquals(t, new JFloatTag(Float.MAX_VALUE));
        assertNotEquals(t, new JFloatTag(Float.MIN_VALUE));
        assertEquals(new JFloatTag(Float.NaN), new JFloatTag(Float.NaN));
    }

    public void testClone() {
        JFloatTag t = new JFloatTag(Float.MAX_VALUE);
        JFloatTag tc = t.clone();
        assertTrue(t.equals(tc));
        assertFalse(t == tc);
    }

    public void testSerializeDeserialize() {
        JFloatTag t = new JFloatTag(Float.MAX_VALUE);
        byte[] data = serialize(t);
        assertTrue(Arrays.equals(new byte[]{5, 0, 0, 127, 127, -1, -1}, data));
        JFloatTag tt = (JFloatTag) deserialize(data);
        assertTrue(t.equals(tt));
    }

    public void testCompareTo() {
        assertEquals(0, new JFloatTag(5).compareTo(new JFloatTag(5)));
        assertTrue(0 < new JFloatTag(7).compareTo(new JFloatTag(5)));
        assertTrue(0 > new JFloatTag(5).compareTo(new JFloatTag(7)));
        assertThrowsRuntimeException(() -> new JFloatTag(5).compareTo(null), NullPointerException.class);
    }
}
