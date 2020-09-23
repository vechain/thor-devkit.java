package org.vechain.devkit.cry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

public class Address {
    
    /**
     * Remove the "0x" from a string.
     * @param input The input string.
     * @return The stripped string.
     */
    public static String remove0x (String input) {
        return input.replaceFirst("^0(x|X)", "");
    }

    /**
     * Check if the public key is 65 bytes, and starts with 4.
     * @param input
     * @return
     */
    public static boolean isUncompressedPublicKey(byte[] input) {
        if (input.length != 65) {
            throw new IllegalArgumentException("Requires 65 bytes!");
        }

        if (input[0] != 4) {
            throw new IllegalArgumentException("Should starts with first byte as 4.");
        }

        return true;
    }

    public static byte[] publicKeyToAddressBytes(byte[] input) {
        isUncompressedPublicKey(input);
        // Get rid of 0x04 (first byte)
        byte[] slice = Arrays.copyOfRange(input, 0, input.length);
        // Hash the slice and get 32 bytes of result.
        byte[] h = Keccak.keccak256(slice);
        // Get the last 20 bytes from the 32 bytes.
        return Arrays.copyOfRange(h, 12, h.length);
    }

    public static boolean isAddress(String input) {
        return input.matches("(?i)^0x[0-9a-f]{40}$");
    }

    public static String toChecksumAddress(String input) {
        if (!isAddress(input)) {
            throw new IllegalArgumentException("address is not valid.");
        }

        String body = remove0x(input);
        body = body.toLowerCase();

        byte[] h = Keccak.keccak256(body.getBytes(Charsets.US_ASCII));
        String hash = Utils.bytesToHex(h);

        List<String> parts = new ArrayList<String>();
        parts.add("0x");

        for (int i = 0; i < body.length(); i++) { // loop over body.
            if (Integer.parseInt(hash.substring(i, i+1), 16) >= 8) {
                parts.add(body.substring(i, i+1).toUpperCase());
            } else {
                parts.add(body.substring(i, i+1));
            }
        }

        Joiner j = Joiner.on("");
        return j.join(parts);
    }
}
