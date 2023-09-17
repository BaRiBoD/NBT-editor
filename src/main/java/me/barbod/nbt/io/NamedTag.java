package me.barbod.nbt.io;

import me.barbod.nbt.tag.JTag;

public class NamedTag {
    private String name;
    private JTag<?> tag;

    public NamedTag(String name, JTag<?> tag) {
        this.name = name;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JTag<?> getTag() {
        return tag;
    }

    public void setTag(JTag<?> tag) {
        this.tag = tag;
    }
}
