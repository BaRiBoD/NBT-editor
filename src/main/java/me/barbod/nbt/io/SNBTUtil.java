package me.barbod.nbt.io;

import me.barbod.nbt.tag.JTag;

import java.io.IOException;

public class SNBTUtil {
    public static String toSNBT(JTag<?> tag) throws IOException {
        return new SNBTSerializer().toString(tag);
    }

    public static JTag<?> fromSNBT(String string) throws IOException {
        return new SNBTDeserializer().fromString(string);
    }

    public static JTag<?> fromSNBT(String string, boolean lenient) throws IOException {
        return new SNBTParser(string).parse(JTag.DEFAULT_MAX_DEPTH, lenient);
    }
}
