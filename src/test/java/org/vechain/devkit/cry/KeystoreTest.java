package org.vechain.devkit.cry;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

public class KeystoreTest {
    @Test
    public void decryptTest() {
        String ks = "" + "{" + "    \"version\": 3," + "    \"id\": \"f437ebb1-5b0d-4780-ae9e-8640178ffd77\","
                + "    \"address\": \"dc6fa3ec1f3fde763f4d59230ed303f854968d26\"," + "    \"crypto\":" + "    {"
                + "        \"kdf\": \"scrypt\"," + "        \"kdfparams\": {" + "            \"dklen\": 32,"
                + "            \"salt\": \"b57682e5468934be81217ad5b14ca74dab2b42c2476864592c9f3b370c09460a\","
                + "            \"n\": 262144," + "            \"r\": 8," + "            \"p\": 1" + "        },"
                + "        \"cipher\": \"aes-128-ctr\","
                + "        \"ciphertext\": \"88cb876f9c0355a89cad88ee7a17a2179700bc4306eaf78fa67320efbb4c7e31\","
                + "        \"cipherparams\": {" + "            \"iv\": \"de5c0c09c882b3f679876b22b6c5af21\""
                + "        }," + "        \"mac\": \"8426e8a1e151b28f694849cb31f64cbc9ae3e278d02716cf5b61d7ddd3f6e728\""
                + "    }" + "}";

        String password = "123456";
        String private_key_hex = "1599403f7b6c17bb09f16e7f8ebe697af3626db5b41e0f9427a49151c6216920";

        byte[] priv = Keystore.decrypt(ks, password);
        assertEquals(private_key_hex, Utils.bytesToHex(priv));
    }

    @Test
    public void encrytTest() {
        String private_key_hex = "1599403f7b6c17bb09f16e7f8ebe697af3626db5b41e0f9427a49151c6216920";
        String password = "123456";

        // Convert private key to keystore.
        String ks = Keystore.encrypt(Utils.hexToBytes(private_key_hex), password, true);
        // Convert keystore to private key.
        byte[] priv = Keystore.decrypt(ks, password);
        // Private key shall remain the same.
        assertEquals(private_key_hex, Utils.bytesToHex(priv));
    }
}
