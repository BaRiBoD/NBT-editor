package me.barbod.nbt.tag;

import me.barbod.NBTTestCase;

import java.util.Arrays;

public class JByteTagTest extends NBTTestCase {
    public void testCreate() {
        JByteTag t = new JByteTag(Byte.MAX_VALUE);
        assertEquals(Byte.MAX_VALUE, t.asByte());
        t = new JByteTag();
        assertEquals(JByteTag.ZERO_VALUE, t.asByte());
    }

    public void testSetValue() {
        JByteTag t = new JByteTag();
        t.setValue((byte) 123);
        assertEquals(123, t.asByte());
    }

    public void testStringConversion() {
        JByteTag t = new JByteTag(Byte.MAX_VALUE);
        assertEquals(Byte.MAX_VALUE, t.asByte());
        assertEquals(Byte.MAX_VALUE, t.asShort());
        assertEquals(Byte.MAX_VALUE, t.asInt());
        assertEquals(Byte.MAX_VALUE, t.asLong());
        assertEquals(1, t.getID());
        assertEquals("{\"type\":\"" + t.getClass().getSimpleName() + "\",\"value\":" + Byte.MAX_VALUE + "}", t.toString());
    }

    public void testEquals() {
        JByteTag t = new JByteTag(Byte.MAX_VALUE);
        JByteTag t2 = new JByteTag(Byte.MAX_VALUE);
        assertTrue(t.equals(t2));
        JByteTag t3 = new JByteTag(Byte.MIN_VALUE);
        assertFalse(t.equals(t3));
    }

    public void testClone() {
        JByteTag t = new JByteTag(Byte.MAX_VALUE);
        JByteTag tc = t.clone();
        assertTrue(t.equals(tc));
        assertFalse(t == tc);
    }

    public void testSerializeDeserialize() {
        JByteTag t = new JByteTag(Byte.MAX_VALUE);
        byte[] data = serialize(t);
        assertTrue(Arrays.equals(new byte[]{1, 0, 0, 127}, data));
        JByteTag tt = (JByteTag) deserialize(data);
        assertTrue(t.equals(tt));
    }

    public void testCompareTo() {
        assertEquals(0, new JByteTag((byte) 5).compareTo(new JByteTag((byte) 5)));
        assertTrue(0 < new JByteTag((byte) 7).compareTo(new JByteTag((byte) 5)));
        assertTrue(0 > new JByteTag((byte) 5).compareTo(new JByteTag((byte) 7)));
        assertThrowsRuntimeException(() -> new JByteTag((byte) 5).compareTo(null), NullPointerException.class);
    }

    public void testBoolean() {
        assertFalse(new JByteTag((byte) 0).asBoolean());
        assertFalse(new JByteTag(Byte.MIN_VALUE).asBoolean());
        assertTrue(new JByteTag((byte) 1).asBoolean());
        assertTrue(new JByteTag(Byte.MAX_VALUE).asBoolean());
        assertEquals(1, new JByteTag(true).asByte());
        assertEquals(0, new JByteTag(false).asByte());
    }
}
