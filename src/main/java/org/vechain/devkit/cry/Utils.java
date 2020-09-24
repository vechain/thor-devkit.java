package org.vechain.devkit.cry;

import java.util.Arrays;
import com.google.common.base.Charsets;

class Utils { // only package level visibility.
    /**
     * Remove the "0x" from a string.
     * @param input The input string.
     * @return The stripped string.
     */
    public static String remove0x (String input) {
        return input.replaceFirst("^0(x|X)", "");
    }

    /**
     * Remove the 0x04 bytes at the begin of a byte[].
     * @param input
     * @return
     */
    public static byte[] remove0x04 (byte[] input) {
        if (input[0] == 4) {
            return Arrays.copyOfRange(input, 1, input.length);
        } else {
            return input;
        }
    }

    /**
     * Convert byte[] to its corresponding hex String.
     * @param input byte[]
     * @return String the hex representation.
     */
    public static String bytesToHex(byte[] input) {
        StringBuffer ret = new StringBuffer();
        for (byte a : input) {
            ret.append(String.format("%02x", a));
        }
        return ret.toString();
    }

    /**
     * Convert a string of hex to byte sequence.
     * eg. "FF" to 255, "0f" to 15.
     * @param input The input string, must be of even length.
     * @return The byte sequence or raise error.
     */
    public static byte[] hexToBytes(String input) {
        if (input.length() % 2 == 1) {
            throw new IllegalArgumentException("Hex string length must be even.");
        }
        byte[] result = new byte[input.length() / 2];
        for (int i = 0; i < input.length(); i += 2) {
            String sub = input.substring(i, i+2);
            int number = Integer.parseInt(sub, 16);
            result[i/2] = (byte)number;
        }
        return result;
    }

    /**
     * Convert a string of ASCII characters to byte sequence.
     * eg. "123" to ['49','50','51'], "hello" to ['104', '101', '108', '108', '111']
     * @param input a string of ascii characters.
     * @return the byte[] sequence.
     */
    public static byte[] AsciiToBytes(String input) {
        return input.getBytes(Charsets.US_ASCII);
    }
}
