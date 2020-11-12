package org.vechain.devkit.types;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import org.vechain.devkit.cry.Utils;

public class CompactFixedBlobKindTest {
    @Test
    public void encode() {
        CompactFixedBlobKind kind = new CompactFixedBlobKind(4);
        // leading zeros
        kind.setValue("0x00112233");
        assertEquals(
            kind.toBytes(),
            Utils.hexToBytes("112233")
        );
        // zero in the middle
        kind.setValue("0x11002233");
        assertEquals(
            kind.toBytes(),
            Utils.hexToBytes("11002233")
        );
    }

    @Test
    public void decode() {
        CompactFixedBlobKind kind = new CompactFixedBlobKind(4);

        // Should prepend leading zeros
        assertEquals(
            kind.fromBytes(new byte[]{1}),
            "0x00000001"
        );

        // Should prepend leading zeros, but middle zeros shall remain.
        assertEquals(
            kind.fromBytes(Utils.hexToBytes("110022")),
            "0x00110022"
        );
    }

    @Test
    public void allZero() {
        CompactFixedBlobKind kind = new CompactFixedBlobKind(4);
        kind.setValue("0x00000000");

        assertEquals(
            kind.toBytes(),
            new byte[]{});
    }
}
