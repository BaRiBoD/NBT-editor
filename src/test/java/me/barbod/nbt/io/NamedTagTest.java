package me.barbod.nbt.io;

import me.barbod.NBTTestCase;
import me.barbod.nbt.tag.JByteTag;
import me.barbod.nbt.tag.JShortTag;

public class NamedTagTest extends NBTTestCase {
    public void testCreate() {
        JByteTag t = new JByteTag();
        NamedTag n = new NamedTag("name", t);
        assertEquals("name", n.getName());
        assertTrue(n.getTag() == t);
    }

    public void testSet() {
        JByteTag t = new JByteTag();
        NamedTag n = new NamedTag("name", t);
        n.setName("blah");
        assertEquals("blah", n.getName());
        JShortTag s = new JShortTag();
        n.setTag(s);
        assertTrue(n.getTag() == s);
    }
}
