package org.vechain.devkit;

import org.vechain.devkit.cry.Address;
import org.vechain.devkit.cry.Secp256k1;
import org.vechain.devkit.cry.Utils;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.TreeMap;

public class CertificateTest {
    final static byte[] priv = Utils.hexToBytes("7582be841ca040aa940fff6c05773129e135623e41acce3e0b8ba520dc1ae26a");
    final static byte[] addr = Address.publicKeyToAddressBytes(Secp256k1.derivePublicKey(priv, false));
    final static String sig = "0x390870e4a99a6a80c3903e0bc13fdcaf15ae46d27b6365e3e07275990e3e74955ad43dba79682b9d0de3a47e96149539b07dde6b51c49a1c7eb6254036b913b000";
    @Test
    public void encode() {
        Map<String, String> m = new TreeMap<String, String>();
        m.put("type", "text");
        m.put("content", "fyi");

        Certificate c = new Certificate(
            "identification",
            m,
            "localhost",
            1545035330,
            "0x" + Utils.bytesToHex(addr),
            null
        );

        System.out.println(c.toJsonString());
    }

    @Test
    public void verify() {
        Map<String, String> m = new TreeMap<String, String>();
        m.put("type", "text");
        m.put("content", "fyi");

        Certificate c = new Certificate(
            "identification",
            m,
            "localhost",
            1545035330,
            "0x" + Utils.bytesToHex(addr),
            sig
        );

        Certificate.verify(c);
        assertEquals(
            c.toMap().get("signature") != null,
            true
        );
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void verifyWrong() {
        Map<String, String> m = new TreeMap<String, String>();
        m.put("type", "text");
        m.put("content", "fyi");

        Certificate c = new Certificate(
            "identification",
            m,
            "localhost",
            1545035330,
            "0x" + Utils.bytesToHex(addr),
            sig.replace("0", "1") // wrong sig.
        );

        Certificate.verify(c);
    }
}
