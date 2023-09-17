package me.barbod.nbt.io;

import me.barbod.NBTTestCase;
import me.barbod.nbt.tag.*;

import java.util.Arrays;

public class SNBTParserTest extends NBTTestCase {
    public void testParse() {
        JTag<?> t = assertThrowsNoException(() -> new SNBTParser("{abc: def, blah: 4b, blubb: \"string\", \"foo\": 2s}").parse());
        assertEquals(JCompoundTag.class, t.getClass());
        JCompoundTag c = (JCompoundTag) t;
        assertEquals(4, c.size());
        assertEquals("def", c.getString("abc"));
        assertEquals((byte) 4, c.getByte("blah"));
        assertEquals("string", c.getString("blubb"));
        assertEquals((short) 2, c.getShort("foo"));
        assertFalse(c.containsKey("invalid"));


        JTag<?> tb = assertThrowsNoException(() -> new SNBTParser("16b").parse());
        assertEquals(JByteTag.class, tb.getClass());
        assertEquals((byte) 16, ((JByteTag) tb).asByte());

        tb = assertThrowsNoException(() -> new SNBTParser("16B").parse());
        assertEquals(JByteTag.class, tb.getClass());
        assertEquals((byte) 16, ((JByteTag) tb).asByte());

        assertThrowsException((() -> new SNBTParser("-129b").parse()), ParseException.class);

        JTag<?> ts = assertThrowsNoException(() -> new SNBTParser("17s").parse());
        assertEquals(JShortTag.class, ts.getClass());
        assertEquals((short) 17, ((JShortTag) ts).asShort());

        ts = assertThrowsNoException(() -> new SNBTParser("17S").parse());
        assertEquals(JShortTag.class, ts.getClass());
        assertEquals((short) 17, ((JShortTag) ts).asShort());

        assertThrowsException((() -> new SNBTParser("-32769s").parse()), ParseException.class);

        JTag<?> ti = assertThrowsNoException(() -> new SNBTParser("18").parse());
        assertEquals(JIntTag.class, ti.getClass());
        assertEquals(18, ((JIntTag) ti).asInt());

        assertThrowsException((() -> new SNBTParser("-2147483649").parse()), ParseException.class);

        JTag<?> tl = assertThrowsNoException(() -> new SNBTParser("19l").parse());
        assertEquals(JLongTag.class, tl.getClass());
        assertEquals(19L, ((JLongTag) tl).asLong());

        tl = assertThrowsNoException(() -> new SNBTParser("19L").parse());
        assertEquals(JLongTag.class, tl.getClass());
        assertEquals(19L, ((JLongTag) tl).asLong());

        assertThrowsException((() -> new SNBTParser("-9223372036854775809l").parse()), ParseException.class);

        JTag<?> tf = assertThrowsNoException(() -> new SNBTParser("20.3f").parse());
        assertEquals(JFloatTag.class, tf.getClass());
        assertEquals(20.3f, ((JFloatTag) tf).asFloat());

        tf = assertThrowsNoException(() -> new SNBTParser("20.3F").parse());
        assertEquals(JFloatTag.class, tf.getClass());
        assertEquals(20.3f, ((JFloatTag) tf).asFloat());

        JTag<?> td = assertThrowsNoException(() -> new SNBTParser("21.3d").parse());
        assertEquals(JDoubleTag.class, td.getClass());
        assertEquals(21.3d, ((JDoubleTag) td).asDouble());

        td = assertThrowsNoException(() -> new SNBTParser("21.3D").parse());
        assertEquals(JDoubleTag.class, td.getClass());
        assertEquals(21.3d, ((JDoubleTag) td).asDouble());

        td = assertThrowsNoException(() -> new SNBTParser("21.3").parse());
        assertEquals(JDoubleTag.class, td.getClass());
        assertEquals(21.3d, ((JDoubleTag) td).asDouble());

        JTag<?> tbo = assertThrowsNoException(() -> new SNBTParser("true").parse());
        assertEquals(JByteTag.class, tbo.getClass());
        assertEquals((byte) 1, ((JByteTag) tbo).asByte());

        tbo = assertThrowsNoException(() -> new SNBTParser("false").parse());
        assertEquals(JByteTag.class, tbo.getClass());
        assertEquals((byte) 0, ((JByteTag) tbo).asByte());


        JTag<?> ba = assertThrowsNoException(() -> new SNBTParser("[B; -128,0, 127]").parse());
        assertEquals(JByteArrayTag.class, ba.getClass());
        assertEquals(3, ((JByteArrayTag) ba).length());
        assertTrue(Arrays.equals(new byte[]{-128, 0, 127}, ((JByteArrayTag) ba).getValue()));

        JTag<?> ia = assertThrowsNoException(() -> new SNBTParser("[I; -2147483648, 0,2147483647]").parse());
        assertEquals(JIntArrayTag.class, ia.getClass());
        assertEquals(3, ((JIntArrayTag) ia).length());
        assertTrue(Arrays.equals(new int[]{-2147483648, 0, 2147483647}, ((JIntArrayTag) ia).getValue()));

        JTag<?> la = assertThrowsNoException(() -> new SNBTParser("[L; -9223372036854775808, 0, 9223372036854775807 ]").parse());
        assertEquals(JLongArrayTag.class, la.getClass());
        assertEquals(3, ((JLongArrayTag) la).length());
        assertTrue(Arrays.equals(new long[]{-9223372036854775808L, 0, 9223372036854775807L}, ((JLongArrayTag) la).getValue()));


        assertThrowsException((() -> new SNBTParser("[B; -129]").parse()), ParseException.class);
        assertThrowsException((() -> new SNBTParser("[I; -2147483649]").parse()), ParseException.class);
        assertThrowsException((() -> new SNBTParser("[L; -9223372036854775809]").parse()), ParseException.class);
        assertThrowsException((() -> new SNBTParser("[B; 123b]").parse()), ParseException.class);
        assertThrowsException((() -> new SNBTParser("[I; 123i]").parse()), ParseException.class);
        assertThrowsException((() -> new SNBTParser("[L; 123l]").parse()), ParseException.class);
        assertThrowsException((() -> new SNBTParser("[K; -129]").parse()), ParseException.class);


        assertThrowsException(() -> new SNBTParser("{20:10} {blah:blubb}").parse(), ParseException.class);


        JTag<?> st = assertThrowsNoException(() -> new SNBTParser("abc").parse());
        assertEquals(JStringTag.class, st.getClass());
        assertEquals("abc", ((JStringTag) st).getValue());

        st = assertThrowsNoException(() -> new SNBTParser("\"abc\"").parse());
        assertEquals(JStringTag.class, st.getClass());
        assertEquals("abc", ((JStringTag) st).getValue());

        st = assertThrowsNoException(() -> new SNBTParser("123a").parse());
        assertEquals(JStringTag.class, st.getClass());
        assertEquals("123a", ((JStringTag) st).getValue());

        JTag<?> lt = assertThrowsNoException(() -> new SNBTParser("[abc, \"def\", \"123\" ]").parse());
        assertEquals(JListTag.class, lt.getClass());
        assertEquals(JStringTag.class, ((JListTag<?>) lt).getTypeClass());
        assertEquals(3, ((JListTag<?>) lt).size());
        assertEquals("abc", ((JListTag<?>) lt).asStringTagList().get(0).getValue());
        assertEquals("def", ((JListTag<?>) lt).asStringTagList().get(1).getValue());
        assertEquals("123", ((JListTag<?>) lt).asStringTagList().get(2).getValue());

        assertThrowsException(() -> new SNBTParser("[123, 456").parse(), ParseException.class);
        assertThrowsException(() -> new SNBTParser("[123, 456d]").parse(), ParseException.class);

        JTag<?> ct = assertThrowsNoException(() -> new SNBTParser("{abc: def,\"key\": 123d, blah: [L;123, 456], blubb: [123, 456]}").parse());
        assertEquals(JCompoundTag.class, ct.getClass());
        assertEquals(4, ((JCompoundTag) ct).size());
        assertEquals("def", assertThrowsNoException(() -> ((JCompoundTag) ct).getString("abc")));
        assertEquals(123D, assertThrowsNoException(() -> ((JCompoundTag) ct).getDouble("key")));
        assertTrue(Arrays.equals(new long[]{123, 456}, assertThrowsNoException(() -> ((JCompoundTag) ct).getLongArray("blah"))));
        assertEquals(2, assertThrowsNoException(() -> ((JCompoundTag) ct).getListTag("blubb")).size());
        assertEquals(JIntTag.class, ((JCompoundTag) ct).getListTag("blubb").getTypeClass());

        assertThrowsException(() -> new SNBTParser("{abc: def").parse(), ParseException.class);
        assertThrowsException(() -> new SNBTParser("{\"\":empty}").parse(), ParseException.class);
        assertThrowsException(() -> new SNBTParser("{empty:}").parse(), ParseException.class);
    }
}
