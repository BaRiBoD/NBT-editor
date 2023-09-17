package me.barbod.nbt.tag;

import me.barbod.NBTTestCase;

import java.util.Arrays;

public class JByteArrayTest extends NBTTestCase {
    public void testCreate() {
        JByteArrayTag t = new JByteArrayTag(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE});
        assertTrue(Arrays.equals(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE}, t.getValue()));
        t = new JByteArrayTag();
        assertTrue(Arrays.equals(JByteArrayTag.ZERO_VALUE, t.getValue()));
    }

    public void testSetValue() {
        JByteArrayTag t = new JByteArrayTag();
        t.setValue(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE});
        assertTrue(Arrays.equals(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE}, t.getValue()));
    }

    public void testStringConversion() {
        JByteArrayTag t = new JByteArrayTag(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE});
        assertTrue(Arrays.equals(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE}, t.getValue()));
        assertEquals(7, t.getID());
        assertEquals("{\"type\":\"" + t.getClass().getSimpleName() + "\",\"value\":[-128,0,127]}", t.toString());
    }

    public void testEquals() {
        JByteArrayTag t = new JByteArrayTag(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE});
        JByteArrayTag t2 = new JByteArrayTag(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE});
        assertTrue(t.equals(t2));
        JByteArrayTag t3 = new JByteArrayTag(new byte[]{Byte.MAX_VALUE, 0, Byte.MIN_VALUE});
        assertFalse(t.equals(t3));
    }

    public void testClone() {
        JByteArrayTag t = new JByteArrayTag(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE});
        JByteArrayTag tc = t.clone();
        assertTrue(t.equals(tc));
        assertFalse(t == tc);
        assertFalse(t.getValue() == tc.getValue());
    }

    public void testSerializeDeserialize() {
        JByteArrayTag t = new JByteArrayTag(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE});
        byte[] data = serialize(t);
        assertTrue(Arrays.equals(new byte[]{7, 0, 0, 0, 0, 0, 3, -128, 0, 127}, data));
        JByteArrayTag tt = (JByteArrayTag) deserialize(data);
        assertTrue(t.equals(tt));
    }

    public void testCompareTo() {
        JByteArrayTag t = new JByteArrayTag(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE});
        JByteArrayTag t2 = new JByteArrayTag(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE});
        JByteArrayTag t3 = new JByteArrayTag(new byte[]{Byte.MAX_VALUE, 0, Byte.MIN_VALUE});
        JByteArrayTag t4 = new JByteArrayTag(new byte[]{0, Byte.MIN_VALUE});
        assertEquals(0, t.compareTo(t2));
        assertEquals(0, t.compareTo(t3));
        assertTrue(0 < t.compareTo(t4));
        assertTrue(0 > t4.compareTo(t));
        assertThrowsRuntimeException(() -> t.compareTo(null), NullPointerException.class);
    }

    public void testInvalidType() {
        assertThrowsRuntimeException(NotAnArrayTag::new, UnsupportedOperationException.class);
        assertThrowsRuntimeException(() -> new NotAnArrayTag("test"), UnsupportedOperationException.class);
    }

    public class NotAnArrayTag extends JArrayTag<String> {

        public NotAnArrayTag() {
            super("");
        }

        public NotAnArrayTag(String value) {
            super(value);
        }

        @Override
        public byte getID() {
            return 0;
        }

        @Override
        public NotAnArrayTag clone() {
            return new NotAnArrayTag(getName());
        }
    }
}
