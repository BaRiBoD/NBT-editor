package me.barbod.nbt.io;

import me.barbod.nbt.tag.JTag;

import java.io.IOException;

public interface NBTInput {
    NamedTag readTag(int maxDepth) throws IOException;
    JTag<?> readRawTag(int maxDepth) throws IOException;
}
