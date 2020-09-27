package org.vechain.devkit.cry;

import org.testng.annotations.*;
import static org.testng.Assert.*;

import java.util.List;

import com.google.common.base.Splitter;

public class MnemonicTest {
    @Test
    public void mnemonicTest() {
        List<String> words = Mnemonic.generate(128);
        assertEquals(12, words.size());

        List<String> words2 = Mnemonic.generate();
        assertEquals(12, words2.size());

        // Truth test
        String sentence = "ignore empty bird silly journey junior ripple have guard waste between tenant";
        List<String> myWords = Splitter.on(" ").splitToList(sentence);
        assertTrue(Mnemonic.validate(myWords));

        // False test
        assertFalse(Mnemonic.validate(Splitter.on(" ").splitToList("hello world")));

        // seed test
        byte[] seed = Mnemonic.derive_seed(myWords);
        String seedExpected = "28bc19620b4fbb1f8892b9607f6e406fcd8226a0d6dc167ff677d122a1a64ef936101a644e6b447fd495677f68215d8522c893100d9010668614a68b3c7bb49f";
        assertEquals(seed, Utils.hexToBytes(seedExpected));

        // private key test
        String private_key_hex = "27196338e7d0b5e7bf1be1c0327c53a244a18ef0b102976980e341500f492425";
        assertEquals(Mnemonic.derive_private_key(myWords, 0), Utils.hexToBytes(private_key_hex));

    }
}
