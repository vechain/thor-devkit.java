package org.vechain.devkit.cry;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.MnemonicUtils;

public class Mnemonic {
    private static final int[] L = new int[] { 128, 160, 192, 224, 256 };

    // Derive path for the VET:
    // m / 44' / 818' / 0' / 0 /<address_index>
    private static final int[] VET_PATH = new int[] {
        44 + Bip32ECKeyPair.HARDENED_BIT,
        818 + Bip32ECKeyPair.HARDENED_BIT,
        0 + Bip32ECKeyPair.HARDENED_BIT,
        0
    };

    /**
     * Generate a default 128 bit entropy mnemonic words.
     * 
     * @return A list of strings.
     */
    public static List<String> generate() {
        return generate(128);
    }

    /**
     * Generate mnemonic words accroding to a given entropy length.
     * 
     * The longer the length more words are created.
     * 
     * @param entropyLength How many bits the entropy shall be: 
     *                      128, 160, 192, 224, 256.
     * @return A list of strings.
     */
    public static List<String> generate(int entropyLength) {
        if (!IntStream.of(L).anyMatch(x -> x == entropyLength)) {
            throw new RuntimeException("entropyLength is wrong.");
        }
        byte[] entropy = Utils.getRandomBytes(entropyLength / 8);
        String mnemonic = MnemonicUtils.generateMnemonic(entropy);
        return Splitter.on(" ").splitToList(mnemonic);
    }

    /**
     * Validate the words.
     * 
     * @param words
     * @return
     */
    public static boolean validate(List<String> words) {
        return MnemonicUtils.validateMnemonic(Joiner.on(" ").join(words));
    }

    /**
     * Derive a seed from words. Normally you won't use this function alone.
     * 
     * @param words
     * @return
     */
    public static byte[] derive_seed(List<String> words) {
        String sentence = Joiner.on(" ").join(words);
        return MnemonicUtils.generateSeed(sentence, null);
    }

    /**
     * Derive a direct private key (in bytes) from words.
     * This is a convenient function, 
     * please use HDNode.java if you need to derive a lot of child key pairs.
     * 
     * @param words
     * @param index Just fill in 0 as the first private key.
     * @return
     */
    public static byte[] derive_private_key(List<String> words, int index) {
        // Correct our generation path 
        int[] myPath = Arrays.copyOfRange(VET_PATH, 0, VET_PATH.length + 1);
        myPath[myPath.length - 1] = index;

        // Fetch the master HD node.
        byte[] seed = derive_seed(words);
        Bip32ECKeyPair masterNode = Bip32ECKeyPair.generateKeyPair(seed);

        // Let it derive.
        Bip32ECKeyPair childNode = Bip32ECKeyPair.deriveKeyPair(masterNode, myPath);
        return childNode.getPrivateKey().toByteArray();
    }
}
