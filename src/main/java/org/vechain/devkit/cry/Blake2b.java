package org.vechain.devkit.cry;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.crypto.digests.Blake2bDigest;

public class Blake2b {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] blake2b(byte[]... inputs) {
        Blake2bDigest blake2bDigest = new Blake2bDigest(256);
        for (byte[] input : inputs) {
            blake2bDigest.update(input, 0, input.length);
        }
        byte[] res = new byte[32];
        blake2bDigest.doFinal(res, 0);
        return res;
    }
}
