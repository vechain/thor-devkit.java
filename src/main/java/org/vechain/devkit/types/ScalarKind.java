package org.vechain.devkit.types;

public interface ScalarKind <E> {
    // Serialize the object itself into a byte sequence.
    public byte[] toBytes();

    // Convert the byte sequence back into a type E object.
    public E fromBytes(byte[] data);
}
