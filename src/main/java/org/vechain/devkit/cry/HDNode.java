package org.vechain.devkit.cry;

import java.util.List;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;

/*
BIP-44 specified path notation:
m / purpose' / coin_type' / account' / change / address_index

Derive path for the VET:
m / 44' / 818' / 0' / 0 /<address_index>

So the following is the root of the "external" node chain for VET.

m / 44' / 818' / 0' / 0

m is the master key, which shall be generated from a seed.

The following is the "first" key pair on the "external" node chain.

m / 44' / 818' / 0' / 0 / 0
*/

public class HDNode {
    // Hardened bit = the mark ' on the number.
    public static final int HARDENED_BIT = 0x80000000;

    // This is a constant for VET path.
    // it simply adds 44', 818', 0', 0 to the path.
    // m / 44' / 818' / 0' / 0
    private static final int[] VET_PATH = new int[] { 44 + HARDENED_BIT, 818 + HARDENED_BIT, 0 + HARDENED_BIT, 0 };

    private DeterministicKey pair;

    public HDNode(DeterministicKey pair) {
        this.pair = pair;
    }

    /**
     * This will generate a top-most HDNode for VET wallets. All keypairs will
     * derive as its children.
     * 
     * @param seed
     */
    public static HDNode fromSeed(byte[] seed) {
        DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicKey starting = master;
        for (int i = 0; i < VET_PATH.length; i++) {
            starting = HDKeyDerivation.deriveChildKey(starting, VET_PATH[i]);
        }
        return new HDNode(starting);
    }

    /**
     * This will generate a top-most HDNode for VET wallets. All keypairs will
     * derive as its children.
     * 
     * @param words A list of mnemonic words.
     */
    public static HDNode fromMnemonic(List<String> words) {
        if (!Mnemonic.validate(words)) {
            throw new RuntimeException("The words not valid, abort.");
        }
        byte[] seed = Mnemonic.derive_seed(words);
        return fromSeed(seed);
    }

    /**
     * This will generate an HDNode.
     * 
     * But it cannot further derive HDNode with private key. Only can derive public
     * key nodes.
     * 
     * @param pub
     * @param chainCode
     */
    public static HDNode fromPublicKey(byte[] pub, byte[] chainCode) {
        return new HDNode(HDKeyDerivation.createMasterPubKeyFromBytes(pub, chainCode));
    }

    /**
     * This will generate an HDNode.
     *
     * This has the full ability to derive both public and private nodes.
     * 
     * @param priv
     * @param chainCode
     */
    public static HDNode fromPrivateKey(byte[] priv, byte[] chainCode) {
        return new HDNode(HDKeyDerivation.createMasterPrivKeyFromBytes(priv, chainCode));
    }

    public HDNode derive(int childNumber) {
        return new HDNode(HDKeyDerivation.deriveChildKey(this.pair, childNumber));
    }

    public byte[] getPublicKey() {
        return this.pair.decompress().getPubKey();
    }

    public byte[] getPrivateKey() {
        return this.pair.getPrivKeyBytes();
    }

    public byte[] getChainCode() {
        return this.pair.getChainCode();
    }

    // Returns the first 32 bits.
    // TODO, no tests available yet.
    public int getFingerPrint() {
        return this.pair.getFingerprint();
    }
}
