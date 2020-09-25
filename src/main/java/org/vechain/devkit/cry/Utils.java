package org.vechain.devkit.cry;

import java.util.Arrays;
import java.security.SecureRandom;
import com.google.common.base.Charsets;

/**
 * This class deals with int, string, byte[] conversions and stripping.
 */
class Utils {
    /**
     * Remove the "0x" at the begin of a string.
     * 
     * @param input The input string.
     * @return The stripped string.
     */
    public static String remove0x(String input) {
        return input.replaceFirst("^0(x|X)", "");
    }

    /**
     * Append "0x" at the begin of a string.
     * 
     * @param input The input string.
     * @return The prepended string.
     */
    public static String prepend0x(String input) {
        if (input.startsWith("0x") || input.startsWith("0X")) {
            return input;
        } else {
            return "0x" + input;
        }
    }

    /**
     * Remove the 0x04 bytes at the begin of a byte[].
     * 
     * @param input the byte sequence.
     * @return the byte sequence.
     */
    public static byte[] remove0x04(byte[] input) {
        if (input[0] == 4) {
            return Arrays.copyOfRange(input, 1, input.length);
        } else {
            return input;
        }
    }

    /**
     * Convert byte[] to its corresponding hex String.
     * 
     * @param input a sequence of bytes
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
     * Convert a string of hex to byte sequence. eg. "FF" to 255, "0f" to 15.
     * 
     * @param input The input string, must be of even length.
     * @return The byte sequence or raise error.
     */
    public static byte[] hexToBytes(String input) {
        if (input.length() % 2 == 1) {
            throw new IllegalArgumentException("Hex string length must be even.");
        }
        byte[] result = new byte[input.length() / 2];
        for (int i = 0; i < input.length(); i += 2) {
            String sub = input.substring(i, i + 2);
            int number = Integer.parseInt(sub, 16);
            result[i / 2] = (byte) number;
        }
        return result;
    }

    /**
     * Convert a string of ASCII characters to byte sequence. eg. "123" to
     * ['49','50','51'], "hello" to ['104', '101', '108', '108', '111']
     * 
     * @param input a string of ascii characters.
     * @return the byte[] sequence.
     */
    public static byte[] AsciiToBytes(String input) {
        return input.getBytes(Charsets.US_ASCII);
    }

    /**
     * Get random byte[] of a given length.
     * 
     * @param length The length of desired length of bytes.
     * @return byte[]
     */
    public static byte[] getRandomBytes(int length) {
        SecureRandom sr = new SecureRandom();
        byte[] result = new byte[length];
        sr.nextBytes(result);
        return result;
    }
}
