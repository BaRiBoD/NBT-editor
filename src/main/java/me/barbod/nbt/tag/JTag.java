package me.barbod.nbt.tag;


import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class JTag<T> implements Cloneable {
    public static final int DEFAULT_MAX_DEPTH = 512;

    private static final Pattern ESCAPE_PATTERN = Pattern.compile("[\\\\\n\t\r\"]");
    private static final Pattern NON_QUOTE_PATTERN = Pattern.compile("[a-zA-Z0-9_\\-+]+");
    private static final Map<String, String> ESCAPE_CHARACTERS =
            Map.of("\\", "\\\\\\\\", "\n", "\\\\n", "\t", "\\\\t", "\r", "\\\\r", "\"", "\\\\\"");
    private T value;

    public JTag(T value) {
        setValue(value);
    }

    protected void setValue(T value) {
        this.value = checkValue(value);
    }

    protected T getValue() {
        return value;
    }
    
    protected T checkValue(T value) {
        return Objects.requireNonNull(value);
    }

    @Override
    public boolean equals(Object other) {
        return other != null && getClass() == other.getClass();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public final String toString() {
        return toString(DEFAULT_MAX_DEPTH);
    }

    public String toString(int maxDepth) {
        return "{\"type\":\"" +getClass().getSimpleName() +"\",\"value\":" + valueToString(maxDepth) + "}";
    }

    public String valueToString() {
        return valueToString(DEFAULT_MAX_DEPTH);
    }

    protected static String escapeString(String s, boolean lenient) {
        StringBuffer stringBuffer = new StringBuffer();
        Matcher matcher = ESCAPE_PATTERN.matcher(s);
        while (matcher.find()) {
            matcher.appendReplacement(stringBuffer, ESCAPE_CHARACTERS.get(matcher.group()));
        }
        matcher.appendTail(stringBuffer);
        matcher = NON_QUOTE_PATTERN.matcher(s);
        if (!lenient || !matcher.matches()) {
            stringBuffer.insert(0, "\"").append("\"");
        }
        return stringBuffer.toString();
    }

    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    public abstract JTag<T> clone();

    public abstract byte getID();
    public abstract String valueToString(int maxDepth);
}
