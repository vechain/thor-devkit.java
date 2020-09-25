package org.vechain.devkit.cry;

import org.testng.annotations.*;
import static org.testng.Assert.*;

public class IntegrationTest {
    @Test
    public void KeyPairTest() { // private key -> public key -> address conversion.
        byte[] priv = Utils.hexToBytes("a4e41355b54229af1832bdd03061c7c83b011aab26060b421a05019144e2f52d");
        byte[] pub = Secp256k1.derivePublicKey(priv, false);
        assertTrue(Address.isUncompressedPublicKey(pub));

        byte[] addressBytes = Address.publicKeyToAddressBytes(pub);
        assertEquals(Utils.bytesToHex(addressBytes), "6e8b475c7786A1023a28C45dB93B74BbD7faF354".toLowerCase());
    }
}
