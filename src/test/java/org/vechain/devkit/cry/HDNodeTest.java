package org.vechain.devkit.cry;

import org.testng.annotations.*;
import static org.testng.Assert.*;

import java.util.List;

import com.google.common.base.Splitter;

public class HDNodeTest {
    @Test
    public void hdnodeTest() {
        // Prepare.
        String sentence = "ignore empty bird silly journey junior ripple have guard waste between tenant";
        String private_key_hex = "27196338e7d0b5e7bf1be1c0327c53a244a18ef0b102976980e341500f492425";
        String seed_hex = "28bc19620b4fbb1f8892b9607f6e406fcd8226a0d6dc167ff677d122a1a64ef936101a644e6b447fd495677f68215d8522c893100d9010668614a68b3c7bb49f";
        List<String> myWords = Splitter.on(" ").splitToList(sentence);

        String[] addresses = new String[] {
            "0x339fb3c438606519e2c75bbf531fb43a0f449a70",
            "0x5677099d06bc72f9da1113afa5e022feec424c8e",
            "0x86231b5cdcbfe751b9ddcd4bd981fc0a48afe921",
            "0xd6f184944335f26ea59dbb603e38e2d434220fcd",
            "0x2ac1a0aecd5c80fb5524348130ab7cf92670470a"
        };

        // Generate from words.
        HDNode topMostNode = HDNode.fromMnemonic(myWords);
        assertEquals(Utils.hexToBytes(private_key_hex), topMostNode.derive(0).getPrivateKey());
        // Generate from seed.
        HDNode topMostNode2 = HDNode.fromSeed(Utils.hexToBytes(seed_hex));
        assertEquals(Utils.hexToBytes(private_key_hex), topMostNode2.derive(0).getPrivateKey());

        // Derive children test.
        for (int i = 0; i < addresses.length; i++) {
            HDNode child = topMostNode.derive(i);
            assertEquals(
                addresses[i],
                Address.publicKeyToAddressString(child.getPublicKey())
            );
        }

        byte[] priv = topMostNode.getPrivateKey();
        byte[] pub = topMostNode.getPublicKey();
        byte[] cc = topMostNode.getChainCode();

        // Generate from a private key node test.
        HDNode privateNode = HDNode.fromPrivateKey(priv, cc);

        // Derive children test.
        for (int i = 0; i < addresses.length; i++) {
            HDNode child = privateNode.derive(i);
            assertEquals(
                addresses[i],
                Address.publicKeyToAddressString(child.getPublicKey())
            );
        }

        // Generate from a public key node test.
        HDNode publicNode = HDNode.fromPublicKey(pub, cc);

        // Derive children test.
        for (int i = 0; i < addresses.length; i++) {
            HDNode child = publicNode.derive(i);
            assertEquals(
                addresses[i],
                Address.publicKeyToAddressString(child.getPublicKey())
            );
        }
    }
}
