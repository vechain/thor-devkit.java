package org.vechain.devkit.cry;

public class Address {
    
    /**
     * Remove the "0x" from a string.
     * @param input The input string.
     * @return The stripped string.
     */
    public static String remove0x (String input) {
        return input.replaceFirst("0(x|X)", "");
    }
}
