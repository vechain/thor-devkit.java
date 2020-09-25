package org.vechain.devkit.cry;

import java.math.BigInteger;

import org.bitcoinj.core.ECKey;

public class Secp256k1 {
    // MAX is the maximum number used as private key.
    private static final byte[] MAX = Utils
            .hexToBytes("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141");

    /**
     * Test if the private key is valid.
     * 
     * @param privateKey byte[] of a private key.
     * @return true/false
     */
    public static boolean isValidPrivateKey(byte[] privateKey) {
        BigInteger max = new BigInteger(1, MAX);
        BigInteger zero = new BigInteger("0");
        BigInteger priv = new BigInteger(1, privateKey);

        if (privateKey.length != 32) {
            return false;
        }

        if (priv.compareTo(zero) == 0) {
            return false;
        }

        if (priv.compareTo(max) == 1) {
            return false;
        }

        return true;
    }

    /**
     * If the message hash is of valid length or format.
     * 
     * @param messageHash byte[] of message hash.
     * @return true/false
     */
    public static boolean isValidMessageHash(byte[] messageHash) {
        return messageHash.length == 32;
    }

    /**
     * Generate a new private key.
     * 
     * @return The byte[] represents the private key.
     */
    public static byte[] newPrivateKey() {
        ECKey pair = new ECKey();
        return pair.getPrivKeyBytes();
    }

    /**
     * Generate a valid public key from the given private key.
     * 
     * @param privateKey
     * @param compressed The output public key is compressed or not.
     * @return
     */
    public static byte[] derivePublicKey(byte[] privateKey, boolean compressed) {
        ECKey pair = ECKey.fromPrivate(privateKey, compressed);
        return pair.getPubKey();
    }

    public static byte[] sign(byte[] messageHash, byte[] privateKey) {
        return new byte[65];
    }
}