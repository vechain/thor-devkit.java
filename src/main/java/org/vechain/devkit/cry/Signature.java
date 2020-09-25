package org.vechain.devkit.cry;

import org.spongycastle.util.Arrays;
import org.web3j.crypto.ECDSASignature;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import com.google.common.primitives.Bytes;

class Signature {
    private ECDSASignature sig;
    private int v;

    Signature(ECDSASignature sig, int v) {
        this.sig = sig;
        this.v = v;
    }

    Signature(byte[] sigBytes) {
        if (sigBytes.length != 65) {
            throw new RuntimeException("signature bytes shall be 65 length.");
        }
        // byte[] r = 32, s = 32, v = 1
        BigInteger r = new BigInteger(1, Arrays.copyOfRange(sigBytes, 0, 32));
        BigInteger s = new BigInteger(1, Arrays.copyOfRange(sigBytes, 32, 64));

        this.sig = new ECDSASignature(r, s);
        this.v = (int) sigBytes[64];
    }

    /**
     * Serialize the signature to byte[].
     * 
     * @return
     */
    public byte[] serialize() {
        byte[] r = Numeric.toBytesPadded(this.sig.r, 32);
        byte[] s = Numeric.toBytesPadded(this.sig.s, 32);
        byte[] v = new byte[] { (byte) this.v };
        return Bytes.concat(r, s, v);
    }

    /**
     * Return the ECDSASignature part of the structure.
     * 
     * @return
     */
    public ECDSASignature getECDSASignature() {
        return this.sig;
    }

    /**
     * Return the v part of the structure.
     * 
     * @return
     */
    public int getV() {
        return this.v;
    }
}