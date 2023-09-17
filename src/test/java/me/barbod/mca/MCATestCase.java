package me.barbod.mca;

import me.barbod.NBTTestCase;
import me.barbod.nbt.tag.JCompoundTag;
import me.barbod.nbt.tag.JListTag;

public class MCATestCase extends NBTTestCase {
    public JCompoundTag block(String name) {
        JCompoundTag c = new JCompoundTag();
        c.putString("Name", name);
        return c;
    }

    public JCompoundTag getSection(JCompoundTag chunk, int y) {
        for (JCompoundTag section : chunk.getCompoundTag("Level").getListTag("Sections").asCompoundTagList()) {
            if (section.getByte("Y") == y) {
                return section;
            }
        }
        fail("could not find section");
        return null;
    }

    public JCompoundTag getSomeCompoundTag() {
        JCompoundTag c = new JCompoundTag();
        c.putString("Dummy", "dummy");
        return c;
    }

    public JListTag<JCompoundTag> getSomeCompoundTagList() {
        JListTag<JCompoundTag> l = new JListTag<>(JCompoundTag.class);
        l.add(getSomeCompoundTag());
        l.add(getSomeCompoundTag());
        return l;
    }

    public JListTag<JListTag<?>> getSomeListTagList() {
        JListTag<JListTag<?>> l = new JListTag<>(JListTag.class);
        l.add(getSomeCompoundTagList());
        l.add(getSomeCompoundTagList());
        return l;
    }

    public static String longToBinaryString(long n) {
        StringBuilder s = new StringBuilder(Long.toBinaryString(n));
        for (int i = s.length(); i < 64; i++) {
            s.insert(0, "0");
        }
        return s.toString();
    }

    public static String intToBinaryString(int n) {
        StringBuilder s = new StringBuilder(Integer.toBinaryString(n));
        for (int i = s.length(); i < 32; i++) {
            s.insert(0, "0");
        }
        return s.toString();
    }
}
