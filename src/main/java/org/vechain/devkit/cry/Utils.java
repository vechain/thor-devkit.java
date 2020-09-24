package org.vechain.devkit.cry;

class Utils { // only package level visibility.
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
}
