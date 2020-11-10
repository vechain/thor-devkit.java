package org.vechain.devkit.types;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import org.vechain.devkit.cry.Utils;

public class FixedBlobKindTest {
    @Test
    public void encode() {
        FixedBlobKind fbk = new FixedBlobKind(4); // 4 bytes fixed.
        fbk.setValue("0x12345678");
        assertEquals(Utils.bytesToHex(fbk.toBytes()), "12345678");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void wrongLengthEncode() {
        FixedBlobKind fbk = new FixedBlobKind(4); // 4 bytes fixed.
        fbk.setValue("0x1122334455");
    }

    @Test
    public void decode() {
        FixedBlobKind fbk = new FixedBlobKind(4); // 4 bytes fixed.
        String result = fbk.fromBytes(new byte[] {1,2,3,4});
        assertEquals(result, "0x01020304");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void wrongLengthDecode() {
        FixedBlobKind fbk = new FixedBlobKind(4); // 4 bytes fixed.
        fbk.fromBytes(new byte[]{1, 2});
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void wrongLengthDecode2() {
        FixedBlobKind fbk = new FixedBlobKind(4); // 4 bytes fixed.
        fbk.fromBytes(new byte[]{});
    }
}
