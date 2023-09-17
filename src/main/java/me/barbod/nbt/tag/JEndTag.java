package me.barbod.nbt.tag;

public final class JEndTag extends JTag<Void> {
    public static final byte ID = 0;
    public static final JEndTag INSTANCE = new JEndTag();

    private JEndTag() {
        super(null);
    }

    @Override
    public JEndTag clone() {
        return INSTANCE;
    }

    @Override
    protected Void checkValue(Void value) {
        return value;
    }

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public String valueToString(int maxDepth) {
        return "\"end\"";
    }
}
