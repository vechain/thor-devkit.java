package org.vechain.devkit.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Reserved = {
 *   features: int --> later encoded to a single byte[].
 *   unused: List<byte[]>
 * }
 */
final public class Reserved {
    final static NumericKind featuresKind = new NumericKind(4);
    private int features;
    private List<byte[]> unused;
    
    public Reserved (int features, List<byte[]> unused) {
        this.features = features;
        if (unused == null) {
            this.unused = new ArrayList<byte[]>();
        } else {
            this.unused = unused;
        }
    }

    public int getFeatures() {
        return this.features;
    }

    public List<byte[]> getUnused() {
        return this.unused;
    }

    // If not reserved, create a "not" reserved instance.
    public static Reserved getNullReserved() {
        return new Reserved(0, null);
    }

    public boolean isNullReserved() {
        if (this.unused.size() == 0 && this.features == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Pack the Reserved into a List<byte[]>
     * @return
     */
    public List<byte[]> pack() {
        List<byte[]> mList = new ArrayList<byte[]>();
        featuresKind.setValue(this.features);
        mList.add(featuresKind.toBytes());
        mList.addAll(this.unused); // Concat the list at the tail.

        // While some elements in the m_list is "new byte[]{}",
        // Then just right strip those from the list.
        // Hence reverse order travesal.
        int rightFirstNotEmptyIdx = -10;
        for (int i = mList.size() - 1; i >= 0 ; i--) {
            final byte[] x = mList.get(i);
            if (x == null || x.length == 0) {
                continue;
            } else {
                rightFirstNotEmptyIdx = i;
                break;
            }
        }

        if (rightFirstNotEmptyIdx == -10) {
            return new ArrayList<byte[]>();
        } else {
            List<byte[]> rList = new ArrayList<byte[]>();
            for (int i = 0; i <= rightFirstNotEmptyIdx; i++) {
                rList.add(mList.get(i));
            }
            return rList;
        }
    }

    /**
     * Unpack the Reserved from a group of byte[].
     * @param data
     * @return Instance of Reserved or null.
     */
    public static Reserved unpack(List<byte[]> data) {
        List<byte[]> r = new ArrayList<byte[]>();
        if (data != null) {
            r = data;
        }
        // empty? return null.
        if (r.size() == 0) {
            return null;
        }
        // not empty? start decoding.
        // Check: Right most isn't something of "new byte[]{}".
        byte[] last = r.get(r.size() - 1);
        if (last.length == 0) {
            throw new IllegalArgumentException("Right trim the input please.");
        }
        // Decode the features slot.
        int features = featuresKind.fromBytes(r.get(0)).intValue();
        // Decode the unused. (simply copy the rest of the items)
        if (r.size() > 1) {
            return new Reserved(features, r.subList(1, r.size()));
        } else { // r.size() == 1, so no unused followed.
            return new Reserved(features, null);
        }
    }
}