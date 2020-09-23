package org.vechain.devkit.cry;

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
}
