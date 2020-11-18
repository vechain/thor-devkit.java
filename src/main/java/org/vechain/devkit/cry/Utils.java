package org.vechain.devkit.cry;

import java.util.Arrays;
import java.util.List;
import java.security.SecureRandom;
import com.google.common.base.Charsets;

/**
 * This class deals with int, string, byte[] conversions and stripping.
 */
public class Utils {
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
     * Doesn't have "0x" prepended.
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
     * Prepend the given input string, with prefix. prefix will repeat
     * several times.
     * 
     * eg. "abc", "0", 3 => "000abc"
     * @param input
     * @param prefix
     * @param count
     * @return
     */
    public static String prependChars(String input, String prefix, int count) {
        return prefix.repeat(count) + input;
    }

    /**
     * Prepend the given input string, with prefix,
     * util the total length requirement is met.
     * @param input
     * @param prefix
     * @param length
     * @return
     */
    public static String extendString(String input, String prefix, int length) {
        if (input.length() > length) {
            return input;
        }
        final int wanted = length - input.length();
        if (wanted % prefix.length() != 0) {
            throw new IllegalArgumentException("cannot divide!");
        }

        return prefix.repeat( wanted / prefix.length() ) + input;
    }

    /**
     * Convert a string of hex to byte sequence. eg. "FF" to 255, "0f" to 15.
     * 
     * No "0x" prefix is allowed.
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

    public static byte[] UTF8ToBytes(String input) {
        return input.getBytes(Charsets.UTF_8);
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

    // [byte[], byte[], [byte[], byte[]]]
    public static void prettyPrint(Object[] raw, int indent) {
        final int internalIndent = 2;
        System.out.println(" ".repeat(indent) + "[");
        for (Object o : raw) {
            // bytes? print it!
            if (o instanceof byte[]) {
                if (((byte[]) o).length > 0) {
                    System.out.println(" ".repeat(indent + internalIndent) + bytesToHex((byte[])o));
                } else {
                    System.out.println(" ".repeat(indent + internalIndent) + "(empty byte[])");
                }
            }
            // A list of bytes? Rabbit hole.
            if (o instanceof List<?>) { // List<byte[]>
                List<byte[]> x = (List<byte[]>) o;
                prettyPrint(x.toArray(), indent+internalIndent);
            }
            // An array of bytes? rabbit hole.
            if (o instanceof Object[]) {
                prettyPrint((Object[]) o, indent+internalIndent);
            }
        }
        System.out.println(" ".repeat(indent) + "]");
    }

    // byte[]
    public static void prettyPrint(byte[] raw, int indent) {
        if (raw.length > 0) {
            System.out.println(" ".repeat(indent) + bytesToHex(raw));
        } else {
            System.out.println(" ".repeat(indent) + "**empty byte[]**");
        }
    }
}
