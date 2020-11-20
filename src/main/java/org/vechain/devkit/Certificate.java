package org.vechain.devkit;

import java.util.Map;
import java.util.TreeMap;

import com.google.gson.Gson;

import org.vechain.devkit.cry.Address;
import org.vechain.devkit.cry.Blake2b;
import org.vechain.devkit.cry.Secp256k1;
import org.vechain.devkit.cry.Utils;

/**
 * User signed certificate.
 * See: https://github.com/vechain/VIPs/blob/master/vips/VIP-192.md
 */ 
public class Certificate {
    final static Gson gson = new Gson();

    private String purpose;
    private Map<String,String> payload;
    private String domain;
    private long timestamp;
    private String signer;
    private String signature; // A secp256k1 signature, bytes, but turned into a '0x' + bytes.hex() format, by default Null.

    // Empty constructor.
    Certificate(){}

    // Non-empty constructor.
    public Certificate(
        String purpose,
        Map<String, String> payload, // { "type": str, "content": str}
        String domain,
        long timestamp,
        String signer,
        String signature
    ) {
        this.purpose = purpose;
        this.payload = payload;
        checkPayload(payload);
        this.payload = new TreeMap<String, String>(payload); // Keep the key order.
        this.domain = domain;
        this.timestamp = timestamp;
        this.signer = toLower(signer);
        this.signature = toLower(signature);
    }

    /** Verify that the payload is in good shape. */
    private static void checkPayload(Map<String, String> payload) {
        if (payload == null ) {
            throw new IllegalArgumentException("payload cannot be empty.");
        }
        if (payload.get("type") == null) {
            throw new IllegalArgumentException("payload.type shall be string.");
        }
        if (payload.get("content") == null) {
            throw new IllegalArgumentException("payload.content shall be string.");
        }
    }

    /** Safely convert a string to lower case. */
    private String toLower(String str) {
        if (str != null) {
            return str.toLowerCase();
        } else {
            return str;
        }
    }

    /** Create a new Map. */
    Map<String, Object> toMap() {
        TreeMap<String, Object> tm = new TreeMap<>();
        tm.put("purpose", this.purpose);
        tm.put("payload", this.payload);
        tm.put("domain", this.domain);
        tm.put("timestamp", this.timestamp);
        tm.put("signer", this.signer);
        tm.put("signature", this.signature);
        return tm;
    }

    /** Create a new Certificate instance. */
    static Certificate fromMap(Map<String, Object> m) {
        Map<String, String> payload = (Map<String, String>) m.get("payload");
        return new Certificate(
            (String) m.get("purpose"),
            payload,
            (String) m.get("domain"),
            (long) m.get("timestamp"),
            (String) m.get("signer"),
            (String) m.get("signature")
        );
    }

    /**
     * Encode a certificate into json string.
     */
    public String toJsonString() {
        return gson.toJson(this.toMap());
    }

    /**
     * Create a Certificate instance from json string.
     * @param jsonString
     * @return
     */
    public static Certificate fromJsonString(String jsonString) {
        Map<String, Object> temp = gson.fromJson(jsonString, Map.class);
        return fromMap(temp);
    }

    /** Check if signature is in good shape. */
    private static boolean isSignature(String input) {
        return input.matches("(?i)^0x[0-9a-f]+$");
    }

    /**
     * Verify a cert (mainly on signature matching.)
     * @param c
     * @return
     */
    public static void verify(Certificate c) {
        if (c.signature == null) {
            throw new IllegalArgumentException("Cert needs a signature.");
        }
        if (c.signature.length() % 2 != 0) {
            throw new IllegalArgumentException("Signature shall be even length.");
        }
        if (!isSignature(c.signature)) {
            throw new IllegalArgumentException("Signature cannot pass the style check.");
        }

        // Compares if the signer matches with the signature.
        Map<String, Object> temp = c.toMap();
        temp.remove("signature");
        Certificate newCert = Certificate.fromMap(temp);
        String j = newCert.toJsonString();
        byte[] signingHash = Blake2b.blake2b256(Utils.UTF8ToBytes(j));
        // Try to recover the public key.
        byte[] pubKey = Secp256k1.recover(signingHash, Utils.hexToBytes(c.signature.substring(2)));
        byte[] addrBytes = Address.publicKeyToAddressBytes(pubKey);

        String addr = ("0x" + Utils.bytesToHex(addrBytes)).toLowerCase();
        if (addr.compareTo(c.signer.toLowerCase()) != 0){
            throw new IllegalArgumentException("signature does not match with the signer.");
        }
    }
}
