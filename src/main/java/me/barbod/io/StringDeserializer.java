package me.barbod.io;

import java.io.Reader;
import java.io.IOException;
import java.io.StringReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileReader;

public interface StringDeserializer<T> extends Deserializer<T> {
    T fromReader(Reader reader) throws IOException;

    default T fromString(String str) throws IOException {
        return fromReader(new StringReader(str));
    }

    @Override
    default T fromStream(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream)) {
            return fromReader(reader);
        }
    }

    @Override
    default T fromFile(File file) throws IOException {
        try (Reader reader = new FileReader(file)) {
            return fromReader(reader);
        }
    }

    @Override
    default T fromBytes(byte[] data) throws IOException {
        return fromReader(new StringReader(new String(data)));
    }
}
