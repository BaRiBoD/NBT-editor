package me.barbod.nbt.io;

import me.barbod.io.MaxDepthIO;
import me.barbod.nbt.tag.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class SNBTParser implements MaxDepthIO {
    private static final Pattern
            FLOAT_LITERAL_PATTERN = Pattern.compile("^[-+]?(?:\\d+\\.?|\\d*\\.\\d+)(?:e[-+]?\\d+)?f$", Pattern.CASE_INSENSITIVE),
            DOUBLE_LITERAL_PATTERN = Pattern.compile("^[-+]?(?:\\d+\\.?|\\d*\\.\\d+)(?:e[-+]?\\d+)?d$", Pattern.CASE_INSENSITIVE),
            DOUBLE_LITERAL_NO_SUFFIX_PATTERN = Pattern.compile("^[-+]?(?:\\d+\\.|\\d*\\.\\d+)(?:e[-+]?\\d+)?$", Pattern.CASE_INSENSITIVE),
            BYTE_LITERAL_PATTERN = Pattern.compile("^[-+]?\\d+b$", Pattern.CASE_INSENSITIVE),
            SHORT_LITERAL_PATTERN = Pattern.compile("^[-+]?\\d+s$", Pattern.CASE_INSENSITIVE),
            INT_LITERAL_PATTERN = Pattern.compile("^[-+]?\\d+$", Pattern.CASE_INSENSITIVE),
            LONG_LITERAL_PATTERN = Pattern.compile("^[-+]?\\d+l$", Pattern.CASE_INSENSITIVE),
            NUMBER_PATTERN = Pattern.compile("^[-+]?\\d+$");

    private StringPointer ptr;

    public SNBTParser(String string) {
        this.ptr = new StringPointer(string);
    }

    public JTag<?> parse(int maxDepth, boolean lenient) throws ParseException {
        JTag<?> tag = parseAnything(maxDepth);
        if (!lenient) {
            ptr.skipWhitespace();
            if (ptr.hasNext()) {
                throw ptr.parseException("invalid characters after end of snbt");
            }
        }
        return tag;
    }

    public JTag<?> parse(int maxDepth) throws ParseException {
        return parse(maxDepth, false);
    }

    public JTag<?> parse() throws ParseException {
        return parse(JTag.DEFAULT_MAX_DEPTH, false);
    }

    public int getReadChars() {
        return ptr.getIndex() + 1;
    }

    private JTag<?> parseAnything(int maxDepth) throws ParseException {
        ptr.skipWhitespace();
        switch (ptr.currentChar()) {
            case '{':
                return parseCompoundTag(maxDepth);
            case '[':
                if (ptr.hasCharsLeft(2) && ptr.lookAhead(1) != '"' && ptr.lookAhead(2) == ';') {
                    return parseNumArray();
                }
                return parseListTag(maxDepth);
        }
        return parseStringOrLiteral();
    }

    private JTag<?> parseStringOrLiteral() throws ParseException {
        ptr.skipWhitespace();
        if (ptr.currentChar() == '"') {
            return new JStringTag(ptr.parseQuotedString());
        }
        String s = ptr.parseSimpleString();
        if (s.isEmpty()) {
            throw new ParseException("expected non empty value");
        }
        if (FLOAT_LITERAL_PATTERN.matcher(s).matches()) {
            return new JFloatTag(Float.parseFloat(s.substring(0, s.length() - 1)));
        } else if (BYTE_LITERAL_PATTERN.matcher(s).matches()) {
            try {
                return new JByteTag(Byte.parseByte(s.substring(0, s.length() - 1)));
            } catch (NumberFormatException ex) {
                throw ptr.parseException("byte not in range: \"" + s.substring(0, s.length() - 1) + "\"");
            }
        } else if (SHORT_LITERAL_PATTERN.matcher(s).matches()) {
            try {
                return new JShortTag(Short.parseShort(s.substring(0, s.length() - 1)));
            } catch (NumberFormatException ex) {
                throw ptr.parseException("short not in range: \"" + s.substring(0, s.length() - 1) + "\"");
            }
        } else if (LONG_LITERAL_PATTERN.matcher(s).matches()) {
            try {
                return new JLongTag(Long.parseLong(s.substring(0, s.length() - 1)));
            } catch (NumberFormatException ex) {
                throw ptr.parseException("long not in range: \"" + s.substring(0, s.length() - 1) + "\"");
            }
        } else if (INT_LITERAL_PATTERN.matcher(s).matches()) {
            try {
                return new JIntTag(Integer.parseInt(s));
            } catch (NumberFormatException ex) {
                throw ptr.parseException("int not in range: \"" + s.substring(0, s.length() - 1) + "\"");
            }
        } else if (DOUBLE_LITERAL_PATTERN.matcher(s).matches()) {
            return new JDoubleTag(Double.parseDouble(s.substring(0, s.length() - 1)));
        } else if (DOUBLE_LITERAL_NO_SUFFIX_PATTERN.matcher(s).matches()) {
            return new JDoubleTag(Double.parseDouble(s));
        } else if ("true".equalsIgnoreCase(s)) {
            return new JByteTag(true);
        } else if ("false".equalsIgnoreCase(s)) {
            return new JByteTag(false);
        }
        return new JStringTag(s);
    }

    private JCompoundTag parseCompoundTag(int maxDepth) throws ParseException {
        ptr.expectChar('{');

        JCompoundTag compoundTag = new JCompoundTag();

        ptr.skipWhitespace();
        while (ptr.hasNext() && ptr.currentChar() != '}') {
            ptr.skipWhitespace();
            String key = ptr.currentChar() == '"' ? ptr.parseQuotedString() : ptr.parseSimpleString();
            if (key.isEmpty()) {
                throw new ParseException("empty keys are not allowed");
            }
            ptr.expectChar(':');

            compoundTag.put(key, parseAnything(decrementMaxDepth(maxDepth)));

            if (!ptr.nextArrayElement()) {
                break;
            }
        }
        ptr.expectChar('}');
        return compoundTag;
    }

    private JListTag<?> parseListTag(int maxDepth) throws ParseException {
        ptr.expectChar('[');
        ptr.skipWhitespace();
        JListTag<?> list = JListTag.createUnchecked(JEndTag.class);
        while (ptr.currentChar() != ']') {
            JTag<?> element = parseAnything(decrementMaxDepth(maxDepth));
            try {
                list.addUnchecked(element);
            } catch (IllegalArgumentException ex) {
                throw ptr.parseException(ex.getMessage());
            }
            if (!ptr.nextArrayElement()) {
                break;
            }
        }
        ptr.expectChar(']');
        return list;
    }

    private JArrayTag<?> parseNumArray() throws ParseException {
        ptr.expectChar('[');
        char arrayType = ptr.next();
        ptr.expectChar(';');
        ptr.skipWhitespace();
        switch (arrayType) {
            case 'B':
                return parseByteArrayTag();
            case 'I':
                return parseIntArrayTag();
            case 'L':
                return parseLongArrayTag();
        }
        throw new ParseException("invalid array type '" + arrayType + "'");
    }

    private JByteArrayTag parseByteArrayTag() throws ParseException {
        List<Byte> byteList = new ArrayList<>();
        while (ptr.currentChar() != ']') {
            String s = ptr.parseSimpleString();
            ptr.skipWhitespace();
            if (NUMBER_PATTERN.matcher(s).matches()) {
                try {
                    byteList.add(Byte.parseByte(s));
                } catch (NumberFormatException ex) {
                    throw ptr.parseException("byte not in range: \"" + s + "\"");
                }
            } else {
                throw ptr.parseException("invalid byte in ByteArrayTag: \"" + s + "\"");
            }
            if (!ptr.nextArrayElement()) {
                break;
            }
        }
        ptr.expectChar(']');
        byte[] bytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }
        return new JByteArrayTag(bytes);
    }

    private JIntArrayTag parseIntArrayTag() throws ParseException {
        List<Integer> intList = new ArrayList<>();
        while (ptr.currentChar() != ']') {
            String s = ptr.parseSimpleString();
            ptr.skipWhitespace();
            if (NUMBER_PATTERN.matcher(s).matches()) {
                try {
                    intList.add(Integer.parseInt(s));
                } catch (NumberFormatException ex) {
                    throw ptr.parseException("int not in range: \"" + s + "\"");
                }
            } else {
                throw ptr.parseException("invalid int in IntArrayTag: \"" + s + "\"");
            }
            if (!ptr.nextArrayElement()) {
                break;
            }
        }
        ptr.expectChar(']');
        return new JIntArrayTag(intList.stream().mapToInt(i -> i).toArray());
    }

    private JLongArrayTag parseLongArrayTag() throws ParseException {
        List<Long> longList = new ArrayList<>();
        while (ptr.currentChar() != ']') {
            String s = ptr.parseSimpleString();
            ptr.skipWhitespace();
            if (NUMBER_PATTERN.matcher(s).matches()) {
                try {
                    longList.add(Long.parseLong(s));
                } catch (NumberFormatException ex) {
                    throw ptr.parseException("long not in range: \"" + s + "\"");
                }
            } else {
                throw ptr.parseException("invalid long in LongArrayTag: \"" + s + "\"");
            }
            if (!ptr.nextArrayElement()) {
                break;
            }
        }
        ptr.expectChar(']');
        return new JLongArrayTag(longList.stream().mapToLong(l -> l).toArray());
    }
}
