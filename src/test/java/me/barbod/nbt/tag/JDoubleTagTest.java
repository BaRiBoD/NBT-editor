package me.barbod.nbt.tag;

import me.barbod.NBTTestCase;

import java.util.Arrays;

import static org.junit.Assert.assertNotEquals;

public class JDoubleTagTest extends NBTTestCase {
    public void testCreate() {
        JDoubleTag t = new JDoubleTag(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, t.asDouble());
        t = new JDoubleTag();
        assertEquals(JDoubleTag.ZERO_VALUE, t.asDouble());
    }

    public void testSetValue() {
        JDoubleTag t = new JDoubleTag();
        t.setValue(123.4);
        assertEquals(123.4, t.asDouble());
    }

    public void testStringConversion() {
        JDoubleTag t = new JDoubleTag(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, t.asDouble());
        assertEquals(6, t.getID());
        assertEquals("{\"type\":\"" + t.getClass().getSimpleName() + "\",\"value\":" + Double.MAX_VALUE + "}", t.toString());
    }

    public void testEquals() {
        JDoubleTag t = new JDoubleTag(Double.MAX_VALUE);
        assertEquals(t, new JDoubleTag(Double.MAX_VALUE));
        assertNotEquals(t, new JDoubleTag(Double.MIN_VALUE));
        assertEquals(new JDoubleTag(Double.NaN), new JDoubleTag(Double.NaN));
    }

    public void testClone() {
        JDoubleTag t = new JDoubleTag(Double.MAX_VALUE);
        JDoubleTag tc = t.clone();
        assertTrue(t.equals(tc));
        assertFalse(t == tc);
    }

    public void testSerializeDeserialize() {
        JDoubleTag t = new JDoubleTag(Double.MAX_VALUE);
        byte[] data = serialize(t);
        assertTrue(Arrays.equals(new byte[]{6, 0, 0, 127, -17, -1, -1, -1, -1, -1, -1}, data));
        JDoubleTag tt = (JDoubleTag) deserialize(data);
        assertTrue(t.equals(tt));
    }

    public void testCompareTo() {
        assertEquals(0, new JDoubleTag(5).compareTo(new JDoubleTag(5)));
        assertTrue(0 < new JDoubleTag(7).compareTo(new JDoubleTag(5)));
        assertTrue(0 > new JDoubleTag(5).compareTo(new JDoubleTag(7)));
        assertThrowsRuntimeException(() -> new JDoubleTag(5).compareTo(null), NullPointerException.class);
    }
}
