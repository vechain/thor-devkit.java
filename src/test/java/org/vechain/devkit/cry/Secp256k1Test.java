package org.vechain.devkit.cry;

import org.testng.annotations.*;
import static org.testng.Assert.*;

public class Secp256k1Test {
    @Test
    public void isValidPrivateKey() {
        byte[] tooBig = Utils.hexToBytes("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364142");
        assertFalse(Secp256k1.isValidPrivateKey(tooBig));

        byte[] lessbits = Utils.hexToBytes("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd03641");
        assertFalse(Secp256k1.isValidPrivateKey(lessbits));

        byte[] zero = Utils.hexToBytes("00");
        assertFalse(Secp256k1.isValidPrivateKey(zero));

        byte[] priv = Secp256k1.newPrivateKey();
        assertTrue(Secp256k1.isValidPrivateKey(priv));
    }

    @Test
    public void privateKey() {
        byte[] priv = Secp256k1.newPrivateKey();
        assertEquals(priv.length, 32);
    }

    @Test
    public void publicKey() {
        byte[] priv = Secp256k1.newPrivateKey();
        byte[] pub = Secp256k1.derivePublicKey(priv, false);
        assertEquals(pub.length, 65);
    }
}