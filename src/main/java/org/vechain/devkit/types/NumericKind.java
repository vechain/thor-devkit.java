package org.vechain.devkit.types;

import java.math.BigInteger;

// int:     32-bit/4-byte
// long:    64-bit/8-byte
// BigInteger:  Longer than above.
// Longest number in Solidity: 256-bit/32-byte

/**
 * Positive or Zero number kind of integers.
 */
public class NumericKind implements ScalarKind <BigInteger> {
    public static final int MAX = 256; // MAX bit length is 256-bits.
    public static final BigInteger ZERO = new BigInteger("0"); // Smallest is 0.

    private int byteLength = 0;
    private BigInteger big = null;

    // byteLength: How long the number shall be.
    public NumericKind(int byteLength){
        if (byteLength <=0 || byteLength * 8 > MAX) {
            throw new IllegalArgumentException("Shall be 32 or less.");
        }
        this.byteLength = byteLength;
        this.big = ZERO;
    }

    private void setBig(BigInteger big) {
        this.big = big;
    }

    private static void check(BigInteger big, int byteLength) {
        if (big == null) {
            throw new IllegalArgumentException();
        }

        // Less than 0.
        if (ZERO.compareTo(big) > 0) {
            throw new IllegalArgumentException("Shall be bigger than 0");
        }

        // Breach the ceiling.
        if (big.bitLength() > MAX) {
            throw new IllegalArgumentException("Shall be 256-bit/32-byte or less.");
        }

        // Breach the limit.
        if (big.bitLength() > byteLength * 8) {
            throw new IllegalArgumentException(String.format("Longer than expected: %d", byteLength));
        }
    }

    // Convenient function.
    public NumericKind create(String number) {
        BigInteger b = null;
        if (number.startsWith("0x")) {
            b = new BigInteger(number.substring(2), 16);
        } else {
            b = new BigInteger(number, 10);
        }
        check(b, this.byteLength);
        this.setBig(b);
        return this;
    }

    // Convenient function.
    public NumericKind create(long number) {
        BigInteger b = BigInteger.valueOf(number);
        check(b, this.byteLength);
        this.setBig(b);
        return this;
    }

    @Override
    public byte[] toBytes() {
        if (this.big.compareTo(ZERO) == 0) {
            return new byte[]{};
        }
        return this.big.toByteArray();
    }

    @Override
    public BigInteger fromBytes(byte[] data) {
        if (data.length > 0 && data[0] == 0) {
            throw new IllegalArgumentException("Trim the leading zeros, please.");
        }
        // We only deal with zero-positive numbers, hence 1.
        BigInteger b = new BigInteger(1, data);
        check(b, this.byteLength);
        return b;
    }
}