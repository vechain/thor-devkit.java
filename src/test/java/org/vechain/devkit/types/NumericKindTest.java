package org.vechain.devkit.types;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.math.BigInteger;

import org.vechain.devkit.cry.Utils;

public class NumericKindTest {
    @Test
    public void encode() {
        NumericKind kind = new NumericKind(8); // Max 8 bytes.
        kind.setValue("0x0");
        assertEquals(
            Utils.bytesToHex(kind.toBytes()),
            ""
        );

        kind.setValue("0x123");
        assertEquals(
            Utils.bytesToHex(kind.toBytes()),
            "0123"
        );
        kind.setValue("0");
        assertEquals(
            Utils.bytesToHex(kind.toBytes()),
            ""
        );
        kind.setValue(0);
        assertEquals(
            Utils.bytesToHex(kind.toBytes()),
            ""
        );
        kind.setValue("100");
        assertEquals(
            Utils.bytesToHex(kind.toBytes()),
            "64"
        );
        kind.setValue(0x123);
        assertEquals(
            Utils.bytesToHex(kind.toBytes()),
            "0123"
        );
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void nullTest() {
        NumericKind kind = new NumericKind(8); // Max 8 bytes.
        kind.toBytes();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void belowZero() {
        NumericKind kind = new NumericKind(8); // Max 8 bytes.
        kind.setValue(-1); // below zero.
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void notHex() {
        NumericKind kind = new NumericKind(8); // Max 8 bytes.
        kind.setValue("0x"); // 0x is not valid.
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void wrongHex() {
        NumericKind kind = new NumericKind(8); // Max 8 bytes.
        kind.setValue("0x123z"); // z is not hex.
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void tooLong() {
        NumericKind kind = new NumericKind(8); // Max 8 bytes.
        kind.setValue("0x12345678123456780");
        kind.toBytes(); // longer than 8.
    }

    @Test
    public void decode() {
        NumericKind kind = new NumericKind(8); // Max 8 bytes.
        BigInteger b = kind.fromBytes(new byte[]{});
        assertEquals(b.compareTo(new BigInteger("0")), 0); // byte[] == 0

        BigInteger b2 = kind.fromBytes(new byte[]{1, 2, 3});
        assertEquals(b2.compareTo(new BigInteger("010203", 16)), 0);

        BigInteger b3 = kind.fromBytes(new byte[]{1,2,3,4,5,6,7,8});
        assertEquals(b3.compareTo(new BigInteger("0102030405060708", 16)), 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void tooLongDecode() {
        NumericKind kind = new NumericKind(8); // Max 8 bytes.
        kind.fromBytes(new byte[]{1,2,3,4,5,6,7,8,9}); // 9 bytes.
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void leadingZeroDecode() {
        NumericKind kind = new NumericKind(8); // Max 8 bytes.
        kind.fromBytes(new byte[]{0, 1, 2}); // Leading 0.
    }
}
