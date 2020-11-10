package org.vechain.devkit.types;

import org.vechain.devkit.cry.Utils;

/**
 * See BlobKind, except this is of fixed width.
 */
public class FixedBlobKind extends BlobKind {
    int byteLength;

    public FixedBlobKind(int byteLength) {
        this.byteLength = byteLength;
    }

    @Override
    public void setValue(String hexString) {
        String realHex = Utils.remove0x(hexString);
        if (realHex.length() != byteLength * 2) { // 1 byte = 2 hex chars.
            throw new IllegalArgumentException(
                String.format("Need %d chars, got %d chars input.", byteLength * 2, realHex.length())
            );
        }
        super.setValue(hexString);
    }

    @Override
    public String fromBytes(byte[] data) {
        // Validation.
        if (data.length != byteLength) {
            throw new IllegalArgumentException(
                String.format("Need %d bytes, got %d bytes.", byteLength, data.length)
            );
        }

        return super.fromBytes(data);
    }
}
