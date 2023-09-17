package me.barbod.nbt.io;

import me.barbod.io.StringSerializer;
import me.barbod.nbt.tag.JTag;

import java.io.IOException;
import java.io.Writer;

public class SNBTSerializer implements StringSerializer<JTag<?>> {
    @Override
    public void toWriter(JTag<?> tag, Writer writer) throws IOException {
        SNBTWriter.write(tag, writer);
    }

    public void toWriter(JTag<?> tag, Writer writer, int maxDepth) throws IOException {
        SNBTWriter.write(tag, writer, maxDepth);
    }
}
