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

    @Test
    public void signAndRecover() {
        byte[] pub = Utils.hexToBytes(
                "04b90e9bb2617387eba4502c730de65a33878ef384a46f1096d86f2da19043304afa67d0ad09cf2bea0c6f2d1767a9e62a7a7ecc41facf18f2fa505d92243a658f");
        byte[] priv = Utils.hexToBytes("7582be841ca040aa940fff6c05773129e135623e41acce3e0b8ba520dc1ae26a");
        byte[] msgHash = Keccak.keccak256(Utils.AsciiToBytes("hello world"));
        byte[] sigBytes = Secp256k1.sign(msgHash, priv);

        assertEquals(sigBytes, Utils.hexToBytes(
                "f8fe82c74f9e1f5bf443f8a7f8eb968140f554968fdcab0a6ffe904e451c8b9244be44bccb1feb34dd20d9d8943f8c131227e55861736907b02d32c06b934d7200"));

        assertEquals(
                Secp256k1.recover(msgHash, new Signature(sigBytes).getECDSASignature(), new Signature(sigBytes).getV()),
                pub);
    }
}