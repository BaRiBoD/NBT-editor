package me.barbod.io;

import java.io.OutputStream;
import java.io.IOException;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;

public interface Serializer<T> {
    void toStream(T obj, OutputStream outputStream) throws IOException;

    default void toFile(T obj, File file) throws IOException {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            toStream(obj, bufferedOutputStream);
        }
    }

    default byte[] toBytes(T obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        toStream(obj, byteArrayOutputStream);
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}
