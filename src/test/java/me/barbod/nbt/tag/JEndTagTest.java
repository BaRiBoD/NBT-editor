package me.barbod.nbt.tag;

import me.barbod.NBTTestCase;

public class JEndTagTest extends NBTTestCase {
    public void testStringConversion() {
        JEndTag e = JEndTag.INSTANCE;
        assertEquals(0, e.getID());
        assertNull(e.getValue());
        assertEquals("{\"type\":\"" + e.getClass().getSimpleName() + "\",\"value\":\"end\"}", e.toString());
    }

    public void testClone() {
        assertTrue(JEndTag.INSTANCE == JEndTag.INSTANCE.clone());
    }
}
