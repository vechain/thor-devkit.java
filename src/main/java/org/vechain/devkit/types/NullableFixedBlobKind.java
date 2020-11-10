package org.vechain.devkit.types;

/**
 * See FixedBlobKind.java, except this can be null.
 * 
 * If filled null, it is transformed as the following rules:
 * To bytes:
 * null => byte[]{};
 * 
 * To object:
 * byte[]{} => null;
 */
public class NullableFixedBlobKind extends FixedBlobKind {

    public NullableFixedBlobKind(int byteLength) {
        super(byteLength);
    }

    @Override
    public void setValue(String hexString) {
        if (hexString != null) {
            super.setValue(hexString);
        }
    }

    @Override
    public byte[] toBytes() {
        if (this.data == null) {
            return new byte[]{};
        } else {
            return super.toBytes();
        }
    }

    @Override
    public String fromBytes(byte[] data) {
        if (data.length == 0) {
            this.data = null;
            return null;
        } else {
            return super.fromBytes(data);
        }
    }
}
