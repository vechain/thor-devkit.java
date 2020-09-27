package org.vechain.devkit.cry;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Keystore {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Encrypt a private key with a password, generate a JSON string.
     * 
     * @param privateKey byte[32]
     * @param password   String, it will later be used as .getBytes(UTF_8)
     * @param standard   boolean, if use standard(true) or lightweight(false).
     * @return String, a JSON style keystore. You can save it into a file.
     */
    public static String encrypt(byte[] privateKey, String password, boolean standard) {
        ECKeyPair pair = ECKeyPair.create(privateKey);
        try {
            WalletFile wf;
            if (standard) {
                wf = Wallet.createStandard(password, pair);
            } else {
                wf = Wallet.createLight(password, pair);
            }
            return objectMapper.writeValueAsString(wf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decrypt a JSON-style keystore back into a private key.
     * 
     * @param jsonString The keystore in JSON string.
     * @param password   String, will later be used as .getBytes(UTF_8)
     * @return byte[32], the private key itself.
     */
    public static byte[] decrypt(String jsonString, String password) {
        try {
            Credentials c = WalletUtils.loadJsonCredentials(password, jsonString);
            return c.getEcKeyPair().getPrivateKey().toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
