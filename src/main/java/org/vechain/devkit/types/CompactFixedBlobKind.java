package org.vechain.devkit.types;

import java.util.Arrays;
import com.google.common.primitives.Bytes;

/**
 * Just like fixed blob kind, except it will try to reduce space.
 * 
 * When encoded, the resulting byte[] will remove the leading 0.
 */
public class CompactFixedBlobKind extends FixedBlobKind {

    public CompactFixedBlobKind(int byteLength) {
        super(byteLength);
    }

    @Override
    public void setValue(String hexString) {
        super.setValue(hexString);
    }

    /**
     * Serialize the blob, but remove the leading zeros,
     * from the result byte[].
     */
    @Override
    public byte[] toBytes() {
        byte[] m = super.toBytes();
        int first_non_zero_index = -1;
        for (int i = 0; i < m.length; i++) {
            if (m[i] != 0) {
                first_non_zero_index = i;
                break;
            }
        }

        byte[] n = null;
        if (first_non_zero_index != -1) {
            n = Arrays.copyOfRange(m, first_non_zero_index, m.length);
        }

        if (n != null) {
            return n;
        } else {
            return new byte[]{};
        }
    }

    @Override
    public String fromBytes(byte[] data) {
        // Null check
        if (data == null) {
            throw new IllegalArgumentException("Can't be null.");
        }
        // check length
        if (data.length > this.byteLength) {
            throw new IllegalArgumentException("Input too long.");
        }
        if (data.length == 0) {
            throw new IllegalArgumentException("Input length 0, forbidden.");
        }
        // check leading zeros
        if (data[0] == 0) {
            throw new IllegalArgumentException("Remove leading zeros, please.");
        }

        int missing_zeros = this.byteLength - data.length;
        if (missing_zeros > 0) { // indeed missing some zeros!
            byte[] temp = new byte[missing_zeros];
            byte[] newData = Bytes.concat(temp, data);
            return super.fromBytes(newData);
        } else {
            return super.fromBytes(data);
        }
    }
}
