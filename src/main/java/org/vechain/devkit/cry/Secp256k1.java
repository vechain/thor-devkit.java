package org.vechain.devkit.cry;

import java.math.BigInteger;
import org.bitcoinj.core.ECKey; // TODO - seeking possible subsitution.
import org.bouncycastle.util.Arrays;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

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

    /**
     * Sign the message hash with a given private key. The signing result is a
     * signature in byte[] of 65 length.
     * 
     * @param messageHash byte[32]
     * @param privateKey  byte[32]
     * @return byte[65] = [r 32, s 32, v 1]
     */
    public static byte[] sign(byte[] messageHash, byte[] privateKey) {
        if (!isValidMessageHash(messageHash)) {
            throw new IllegalArgumentException("messageHash length wrong.");
        }
        if (!isValidPrivateKey(privateKey)) {
            throw new IllegalArgumentException("privateKey length or value wrong.");
        }

        ECKeyPair pair = ECKeyPair.create(privateKey);
        ECDSASignature sig = pair.sign(messageHash);

        int found = -1;
        for (int i = 0; i < 3; i++) {
            byte[] pub = recover(messageHash, sig, i);
            if (pub != null) {
                found = i;
                break;
            }
        }

        if (found == -1) {
            throw new RuntimeException("Cannot recover the pub key during sign.");
        }

        return new Signature(sig, found).serialize();
    }

    /**
     * Recover the uncompressed public key from the signature. The first byte is
     * "0x04" representing uncompressed format.
     * 
     * @param messageHash
     * @param signature
     * @param v
     * @return byte[65] uncompressed public key.
     */
    public static byte[] recover(byte[] messageHash, ECDSASignature signature, int v) {
        BigInteger key = Sign.recoverFromSignature(v, signature, messageHash);
        if (key != null) {
            return Arrays.concatenate(new byte[] { (byte) 4 }, Numeric.toBytesPadded(key, 64));
        } else {
            return null;
        }
    }
}