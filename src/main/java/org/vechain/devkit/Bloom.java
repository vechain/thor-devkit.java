package org.vechain.devkit;

import org.vechain.devkit.cry.Blake2b;

/**
 * Bloom filter, has some important fields.
 * 
 * k = how many different hash functions shall be used. m = total bits of the
 * filter. n = total items estimated to be put into the filter.
 */
public class Bloom {
    public static final int MAX_K = 16; // Max k allowed.
    public static final int BITS_SIZE = 2048; // Size of the filter (in bits) recommended.

    private int k;
    private byte[] storage;

    /**
     * Initialize a filter from an existing storage. If you don't know k, just call
     * estimateK() first.
     */
    public Bloom(int k, byte[] storage) {
        this.k = k;
        this.storage = storage;
    }

    /**
     * Initialize a filter. If you don't know k, just call estimateK() first.
     */
    public Bloom(int k) {
        this.k = k;
        this.storage = new byte[BITS_SIZE / 8];
    }

    public byte[] getStorage() {
        return this.storage;
    }

    /**
     * Estimate the K required for "count" items to be stored.
     * 
     * @param count The count of items to be stored in the bloom filter.
     * @return the number k.
     */
    public static int estimateK(int count) {
        int k = (int) Math.round((double) BITS_SIZE / (double) count * Math.log(2));
        return Math.max(Math.min(k, MAX_K), 1);
    }

    interface Examer {
        boolean test(int index, int bit);
    }

    private boolean distribute(byte[] element, Examer tester) {
        byte[] h = Blake2b.blake2b256(element);
        for (int x = 0; x < this.k; x++) {
            int d = (Byte.toUnsignedInt(h[x * 2 + 1]) + (Byte.toUnsignedInt(h[x * 2]) << 8)) % BITS_SIZE;
            int bit = 1 << (d % 8);
            if (!tester.test(d / 8, bit)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Add an item to the storage.
     */
    public void add(byte[] element) {
        final byte[] storage = this.storage;

        Examer a = new Examer() {
            public boolean test(int index, int bit) {
                storage[index] = (byte) (storage[index] | (byte) bit);
                return true;
            }
        };

        distribute(element, a);
    }

    /**
     * Test if the item is in the storage.
     */
    public boolean test(byte[] element) {
        final byte[] storage = this.storage;
        Examer a = new Examer() {
            public boolean test(int index, int bit) {
                return (storage[index] & (byte) bit) == bit;
            }
        };

        return distribute(element, a);
    }

}
