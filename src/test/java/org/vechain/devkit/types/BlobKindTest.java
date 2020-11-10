package org.vechain.devkit.types;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import org.vechain.devkit.cry.Utils;

public class BlobKindTest {
    @Test
    public void encode() {
        BlobKind bk = new BlobKind();
        bk.setValue("0x1234567890");
        assertEquals(Utils.bytesToHex(bk.toBytes()), "1234567890");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void notEvenEncode() {
        BlobKind bk = new BlobKind();
        bk.setValue("0x1");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void wrongDataEncode() {
        BlobKind bk = new BlobKind();
        bk.setValue("0xxy");
    }

    @Test
    public void decode() {
        BlobKind bk = new BlobKind();
        String result = bk.fromBytes(new byte[]{1,2,3,4,5});
        assertEquals(result, "0x0102030405");
    }
}
