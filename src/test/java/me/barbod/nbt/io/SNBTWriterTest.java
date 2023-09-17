package me.barbod.nbt.io;

import me.barbod.NBTTestCase;
import me.barbod.nbt.tag.*;

import java.util.LinkedHashMap;

public class SNBTWriterTest extends NBTTestCase {
    public void testWrite() {
        assertEquals("127b", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JByteTag(Byte.MAX_VALUE))));
        assertEquals("-32768s", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JShortTag(Short.MIN_VALUE))));
        assertEquals("-2147483648", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JIntTag(Integer.MIN_VALUE))));
        assertEquals("-9223372036854775808l", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JLongTag(Long.MIN_VALUE))));
        assertEquals("123.456f", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JFloatTag(123.456F))));
        assertEquals("123.456d", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JDoubleTag(123.456D))));

        assertEquals("[B;-128,0,127]", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JByteArrayTag(new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE}))));
        assertEquals("[I;-2147483648,0,2147483647]", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JIntArrayTag(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE}))));
        assertEquals("[L;-9223372036854775808,0,9223372036854775807]", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JLongArrayTag(new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE}))));

        assertEquals("abc", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JStringTag("abc"))));
        assertEquals("\"123\"", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JStringTag("123"))));
        assertEquals("\"123.456\"", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JStringTag("123.456"))));
        assertEquals("\"-123\"", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JStringTag("-123"))));
        assertEquals("\"-1.23e14\"", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JStringTag("-1.23e14"))));
        assertEquals("\"äöü\\\\\"", assertThrowsNoException(() -> SNBTUtil.toSNBT(new JStringTag("äöü\\"))));

        JListTag<JStringTag> lt = new JListTag<>(JStringTag.class);
        lt.addString("blah");
        lt.addString("blubb");
        lt.addString("123");
        assertEquals("[blah,blubb,\"123\"]", assertThrowsNoException(() -> SNBTUtil.toSNBT(lt)));

        JCompoundTag ct = new JCompoundTag();
        invokeSetValue(ct, new LinkedHashMap<>());
        ct.putString("key", "value");
        ct.putByte("byte", Byte.MAX_VALUE);
        ct.putByteArray("array", new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE});
        JListTag<JStringTag> clt = new JListTag<>(JStringTag.class);
        clt.addString("foo");
        clt.addString("bar");
        ct.put("list", clt);
        String ctExpected = "{key:value,byte:127b,array:[B;-128,0,127],list:[foo,bar]}";
        assertEquals(ctExpected, assertThrowsNoException(() -> SNBTUtil.toSNBT(ct)));
    }
}
