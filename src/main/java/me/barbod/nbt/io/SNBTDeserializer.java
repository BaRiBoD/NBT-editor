package me.barbod.nbt.io;

import me.barbod.io.StringDeserializer;
import me.barbod.nbt.tag.JTag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.stream.Collectors;

public class SNBTDeserializer implements StringDeserializer<JTag<?>> {
    @Override
    public JTag<?> fromReader(Reader reader) throws IOException {
        return fromReader(reader, JTag.DEFAULT_MAX_DEPTH);
    }

    public JTag<?> fromReader(Reader reader, int maxDepth) throws IOException {
        BufferedReader bufferedReader;
        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }
        return new SNBTParser(bufferedReader.lines().collect(Collectors.joining())).parse(maxDepth);
    }
}
