package me.barbod.nbt.tag;

import me.barbod.NBTTestCase;

import java.util.Arrays;

public class JShortTagTest extends NBTTestCase {
    public void testCreate() {
        JShortTag t = new JShortTag(Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, t.asShort());
        t = new JShortTag();
        assertEquals(JShortTag.ZERO_VALUE, t.asShort());
    }

    public void testSetValue() {
        JShortTag t = new JShortTag();
        t.setValue((short) 123);
        assertEquals(123, t.asShort());
    }

    public void testStringConversion() {
        JShortTag t = new JShortTag(Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, t.asShort());
        assertEquals(Short.MAX_VALUE, t.asInt());
        assertEquals(Short.MAX_VALUE, t.asLong());
        assertEquals(2, t.getID());
        assertEquals("{\"type\":\"" + t.getClass().getSimpleName() + "\",\"value\":" + Short.MAX_VALUE + "}", t.toString());
    }

    public void testEquals() {
        JShortTag t = new JShortTag(Short.MAX_VALUE);
        JShortTag t2 = new JShortTag(Short.MAX_VALUE);
        assertTrue(t.equals(t2));
        JShortTag t3 = new JShortTag(Short.MIN_VALUE);
        assertFalse(t.equals(t3));
    }

    public void testClone() {
        JShortTag t = new JShortTag(Short.MAX_VALUE);
        JShortTag tc = t.clone();
        assertTrue(t.equals(tc));
        assertFalse(t == tc);
    }

    public void testSerializeDeserialize() {
        JShortTag t = new JShortTag(Short.MAX_VALUE);
        byte[] data = serialize(t);
        assertTrue(Arrays.equals(new byte[]{2, 0, 0, 127, -1}, data));
        JShortTag tt = (JShortTag) deserialize(data);
        assertTrue(t.equals(tt));
    }

    public void testCompareTo() {
        assertEquals(0, new JShortTag((short) 5).compareTo(new JShortTag((short) 5)));
        assertTrue(0 < new JShortTag((short) 7).compareTo(new JShortTag((short) 5)));
        assertTrue(0 > new JShortTag((short) 5).compareTo(new JShortTag((short) 7)));
        assertThrowsRuntimeException(() -> new JShortTag((short) 5).compareTo(null), NullPointerException.class);
    }
}
