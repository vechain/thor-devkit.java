package org.vechain.devkit;

// ABI v1 style paramter wrapper.
// This can be further process into JSON.
class V1DisplayWrapper {
    public int index; // index position in the tuple.
    public String name; // the name of the parameter, can be "".
    public String canonicalType; // the solidity type name.
    public Object value; // the real value.

    public V1DisplayWrapper(int index, String name, String canonicalType, Object value) {
        this.index = index;
        this.name = name;
        this.canonicalType = canonicalType;
        this.value = value;
    }
}
