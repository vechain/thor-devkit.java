package org.vechain.devkit.types;

import org.vechain.devkit.cry.Utils;

/**
 * This is a pre-defined type for "0x...." like hex strings,
    which shouldn't be interpreted as a number, usually an identifier.

    like: address, block_ref, data to smart contract.
 */
public class BlobKind implements ScalarKind <String> {

    byte[] data = null; // Internal representation of this kind.

    public BlobKind() {}

    /**
     * hexString = "0x..."
     * or without the "0x" prefix is also okay.
     */
    public void setValue(String hexString) {
        this.data = Utils.hexToBytes(Utils.remove0x(hexString));
    }

    @Override
    public byte[] toBytes() {
        return this.data;
    }

    /**
     * @return "0x..."
     */
    @Override
    public String fromBytes(byte[] data) {
        this.data = data;
        return Utils.prepend0x(Utils.bytesToHex(data));
    }

    /**
     * Directly return the hex string without "0x" prefix.
     */
    @Override
    public String toString() {
        return Utils.bytesToHex(this.data);
    }
}
