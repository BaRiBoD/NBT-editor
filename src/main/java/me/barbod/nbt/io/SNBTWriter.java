package me.barbod.nbt.io;

import me.barbod.io.MaxDepthIO;
import me.barbod.nbt.tag.*;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.regex.Pattern;

public final class SNBTWriter implements MaxDepthIO {
    private static final Pattern NON_QUOTE_PATTERN = Pattern.compile("[a-zA-Z_.+\\-]+");

    private Writer writer;

    private SNBTWriter(Writer writer) {
        this.writer = writer;
    }

    public static void write(JTag<?> tag, Writer writer, int maxDepth) throws IOException {
        new SNBTWriter(writer).writeAnything(tag, maxDepth);
    }

    public static void write(JTag<?> tag, Writer writer) throws IOException {
        write(tag, writer, JTag.DEFAULT_MAX_DEPTH);
    }

    private void writeAnything(JTag<?> tag, int maxDepth) throws IOException {
        switch (tag.getID()) {
            case JEndTag.ID:
                break;
            case JByteTag.ID:
                writer.append(Byte.toString(((JByteTag) tag).asByte())).write('b');
                break;
            case JShortTag.ID:
                writer.append(Short.toString(((JShortTag) tag).asShort())).write('s');
                break;
            case JIntTag.ID:
                writer.write(Integer.toString(((JIntTag) tag).asInt()));
                break;
            case JLongTag.ID:
                writer.append(Long.toString(((JLongTag) tag).asLong())).write('l');
                break;
            case JFloatTag.ID:
                writer.append(Float.toString(((JFloatTag) tag).asFloat())).write('f');
                break;
            case JDoubleTag.ID:
                writer.append(Double.toString(((JDoubleTag) tag).asDouble())).write('d');
                break;
            case JByteArrayTag.ID:
                writeArray(((JByteArrayTag) tag).getValue(), ((JByteArrayTag) tag).length(), "B");
                break;
            case JStringTag.ID:
                writer.write(escapeString(((JStringTag) tag).getValue()));
                break;
            case JListTag.ID:
                writer.write('[');
                for (int i = 0; i < ((JListTag<?>) tag).size(); i++) {
                    writer.write(i == 0 ? "" : ",");
                    writeAnything(((JListTag<?>) tag).get(i), decrementMaxDepth(maxDepth));
                }
                writer.write(']');
                break;
            case JCompoundTag.ID:
                writer.write('{');
                boolean first = true;
                for (Map.Entry<String, JTag<?>> entry : (JCompoundTag) tag) {
                    writer.write(first ? "" : ",");
                    writer.append(escapeString(entry.getKey())).write(':');
                    writeAnything(entry.getValue(), decrementMaxDepth(maxDepth));
                    first = false;
                }
                writer.write('}');
                break;
            case JIntArrayTag.ID:
                writeArray(((JIntArrayTag) tag).getValue(), ((JIntArrayTag) tag).length(), "I");
                break;
            case JLongArrayTag.ID:
                writeArray(((JLongArrayTag) tag).getValue(), ((JLongArrayTag) tag).length(), "L");
                break;
            default:
                throw new IOException("unknown tag with id \"" + tag.getID() + "\"");
        }
    }

    private void writeArray(Object array, int length, String prefix) throws IOException {
        writer.append('[').append(prefix).write(';');
        for (int i = 0; i < length; i++) {
            writer.append(i == 0 ? "" : ",").write(Array.get(array, i).toString());
        }
        writer.write(']');
    }

    public static String escapeString(String s) {
        if (!NON_QUOTE_PATTERN.matcher(s).matches()) {
            StringBuilder sb = new StringBuilder();
            sb.append('"');
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '\\' || c == '"') {
                    sb.append('\\');
                }
                sb.append(c);
            }
            sb.append('"');
            return sb.toString();
        }
        return s;
    }
}
