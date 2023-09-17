package me.barbod.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;

public interface Deserializer<T> {
    T fromStream(InputStream inputStream) throws IOException;

    default T fromFile(File file) throws IOException {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
            return fromStream(bufferedInputStream);
        }
    }

    default T fromBytes(byte[] data) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        return fromStream(byteArrayInputStream);
    }

    default T fromResource(Class<?> clazz, String path) throws IOException {
        try (InputStream inputStream = clazz.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null)
                throw new IOException("resource \"" + path + "\" not found");
            return fromStream(inputStream);
        }
    }

    default T fromURL(URL url) throws IOException {
        try (InputStream inputStream = url.openStream()) {
            return fromStream(inputStream);
        }
    }
}
