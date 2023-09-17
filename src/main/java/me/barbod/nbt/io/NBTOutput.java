package me.barbod.nbt.io;

import me.barbod.nbt.tag.JTag;

import java.io.IOException;

public interface NBTOutput {
    void writeTag(NamedTag tag, int maxDepth) throws IOException;
    void writeTag(JTag<?> tag, int maxDepth) throws IOException;
    void flush() throws IOException;
}
