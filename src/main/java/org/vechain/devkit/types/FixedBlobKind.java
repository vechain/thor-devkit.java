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
        // Null check
        if (hexString == null) {
            throw new IllegalArgumentException("Can't be null.");
        }
        // Strip the "0x".
        String realHex = Utils.remove0x(hexString);

        // Length check.
        if (realHex.length() != byteLength * 2) { // 1 byte = 2 hex chars.
            throw new IllegalArgumentException(
                String.format("Need %d chars, got %d chars input.", byteLength * 2, realHex.length())
            );
        }
        // Set it.
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
