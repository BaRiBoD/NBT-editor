package me.barbod.nbt.tag;

import me.barbod.NBTTestCase;

import java.util.Arrays;

public class JStringTagTest extends NBTTestCase {
    public void testStringConversion() {
        JStringTag t = new JStringTag("foo");
        assertEquals("foo", t.getValue());
        assertEquals(8, t.getID());
        assertEquals("{\"type\":\"" + t.getClass().getSimpleName() + "\",\"value\":\"foo\"}", t.toString());
    }

    public void testEquals() {
        JStringTag t = new JStringTag("foo");
        JStringTag t2 = new JStringTag("foo");
        assertTrue(t.equals(t2));
        JStringTag t3 = new JStringTag("something else");
        assertFalse(t.equals(t3));
    }

    public void testClone() {
        JStringTag t = new JStringTag("foo");
        JStringTag tc = t.clone();
        assertTrue(t.equals(tc));
        assertFalse(t == tc);
    }

    public void testSerializeDeserialize() {
        JStringTag t = new JStringTag("foo");
        byte[] data = serialize(t);
        assertTrue(Arrays.equals(new byte[]{8, 0, 0, 0, 3, 102, 111, 111}, data));
        JStringTag tt = (JStringTag) deserialize(data);
        assertTrue(t.equals(tt));
    }

    public void testEscape() {
        JStringTag allValue = new JStringTag("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXZY0123456789_-+");
        assertEquals("\"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXZY0123456789_-+\"", allValue.valueToString());
        JStringTag escapeValue = new JStringTag("öäü");
        assertEquals("\"öäü\"", escapeValue.valueToString());
        JStringTag escapeSpecialChars = new JStringTag("\\\n\r\t\"");
        assertEquals("\"\\\\\\n\\r\\t\\\"\"", escapeSpecialChars.valueToString());
        JStringTag escapeEmpty = new JStringTag("");
        assertEquals("\"\"", escapeEmpty.valueToString());

        assertThrowsRuntimeException(() -> new JStringTag().setValue(null), NullPointerException.class);
    }

    public void testCompareTo() {
        JStringTag t = new JStringTag("abc");
        JStringTag t2 = new JStringTag("abc");
        JStringTag t3 = new JStringTag("abd");
        assertEquals(0, t.compareTo(t2));
        assertTrue(0 > t.compareTo(t3));
        assertTrue(0 < t3.compareTo(t));
        assertThrowsRuntimeException(() -> t.compareTo(null), NullPointerException.class);
    }
}
