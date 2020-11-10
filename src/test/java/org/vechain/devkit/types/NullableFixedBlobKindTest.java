package org.vechain.devkit.types;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import org.vechain.devkit.cry.Utils;

public class NullableFixedBlobKindTest {
    @Test
    public void encode() {
        NullableFixedBlobKind nfbk = new NullableFixedBlobKind(4);
        nfbk.setValue(null);
        assertEquals(nfbk.toBytes().length, 0);

        nfbk.setValue("0x12345678");
        assertEquals(nfbk.toBytes(), Utils.hexToBytes("12345678"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void wrongLength() {
        NullableFixedBlobKind nfbk = new NullableFixedBlobKind(4);
        nfbk.setValue("0x1234567890"); // too long.
    }

    @Test
    public void decode() {
        NullableFixedBlobKind nfbk = new NullableFixedBlobKind(4);
        assertEquals(nfbk.fromBytes(new byte[]{}), null); // is null.
        assertEquals(nfbk.fromBytes(new byte[]{1,2,3,4}), "0x01020304"); 
    }
}
