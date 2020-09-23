package org.vechain.devkit.cry;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.crypto.digests.KeccakDigest;

public class Keccak {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] keccak256(byte[]... inputs) {
        KeccakDigest keccak = new KeccakDigest(256);
        for (byte[] input : inputs) {
            keccak.update(input, 0, input.length);
        }
        byte[] res = new byte[32];
        keccak.doFinal(res, 0);
        return res;
    }
}
