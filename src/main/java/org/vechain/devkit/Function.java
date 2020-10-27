package org.vechain.devkit;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.esaulpaugh.headlong.abi.ABIType;
import com.esaulpaugh.headlong.abi.Tuple;
import com.esaulpaugh.headlong.abi.TupleType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.vechain.devkit.cry.Utils;

/**
 * ABI Function related operations. 1) Initialize a function instance according
 * to the definition. 2) Encode the parameters into bytes. 3) Decode the call
 * return value into Map or JSON.
 */
public class Function {

    private com.esaulpaugh.headlong.abi.Function f;

    /**
     * Create a function instance from a JSON definition.
     * 
     * @param jsonString
     */
    public Function(String jsonString) {
        this.f = com.esaulpaugh.headlong.abi.Function.fromJson(jsonString);
    }

    /** Get the selector in bytes. */
    public byte[] selector() {
        return this.f.selector();
    }

    /**
     * Encode the parameters into valid byte buffer.
     * 
     * @param args
     * @return
     */
    private ByteBuffer encode(Object... args) {
        return this.f.encodeCallWithArgs(args);
    }

    /**
     * Encode the parameters into valid bytes.
     * 
     * @param args
     * @return
     */
    public byte[] encodeToBytes(Object... args) {
        ByteBuffer bb = this.encode(args);
        bb.rewind(); // Set buffer "position" to index 0;
        int total = bb.remaining(); // Get elements counter in the buffer.
        byte[] r = new byte[total];
        bb.get(r);
        return r;
    }

    /**
     * Encode the parameters into valid hex format, with or without 0x prefix.
     * 
     * @param prefix0x
     * @param args
     * @return
     */
    public String encodeToHex(boolean prefix0x, Object... args) {
        byte[] b = this.encodeToBytes(args);
        String r = Utils.bytesToHex(b);
        if (prefix0x) {
            return Utils.prepend0x(r);
        } else {
            return Utils.remove0x(r);
        }
    }

    /**
     * Decode the return value into a wrapped "Tuple".
     * 
     * See com.esaulpaugh.headlong.abi.Tuple for details.
     * 
     * "Tuple" supports random access of size() and get(i).
     * @param data
     * @return
     */
    public Tuple decodeReturn(byte[] data) {
        return this.f.decodeReturn(data);
    }

    /**
     * Decode return data into a list of V1DisplayWrapper objects.
     * 
     * "human" option will enable the output:
     * 1) "address" convert to "0x..." rather than BigInteger.
     * 2) "bytes32"/"bytes[]" convert o "0x..." rather than byte[].
     * 3) "unint256"/"int256" to "123456" rather than BigInteger.
     * @param data
     * @param human
     * @return
     */
    public List<V1DisplayWrapper> decodeReturnV1(byte[] data, boolean human) {
        Tuple params = this.f.decodeReturn(data);
        TupleType guide = this.f.getOutputTypes();
        List<V1DisplayWrapper> c = new ArrayList<V1DisplayWrapper>();
        int i = 0;
        for (ABIType<?> e : guide) {
            final int index = i;
            final String name = e.getName();
            final String canonicalType = e.getCanonicalType();
            Object value = params.get(i);

            if (human) {
                // Covert BigIntegers.
                if (value.getClass() == BigInteger.class) {
                    // Convert address to String type of "0x..." for display.
                    if (canonicalType == "address") {
                        value = "0x" + Utils.extendString(((BigInteger) value).toString(16), "0", 40);
                    } else {
                        // Convert other BigIntegers into String to prevent overflow.
                        // eg. int72 till int256, uint64 till uint256.
                        value = ((BigInteger) value).toString(10);
                    }
                }
                // Convert byte array into String type of "0x..."
                if (value.getClass() == byte[].class) {
                    // System.out.println("value class: " + value.getClass());
                    value = "0x" + Utils.bytesToHex((byte[])value);
                }
            }

            final V1DisplayWrapper dw = new V1DisplayWrapper(index, name, canonicalType, value);
            c.add(dw);
            i++;
        }
        return c;
    }

    /**
     * Convenient function. Decode the output according to the Json String.
     * 
     * @param data
     * @param pretty Indentation or not.
     * @param human Convert output human readable or not.
     * @return The Json String.
     */
    public String decodeReturnV1Json(byte[] data, boolean pretty, boolean human) {
        Collection<V1DisplayWrapper> c = decodeReturnV1(data, human);
        Gson gson = pretty ? new GsonBuilder().setPrettyPrinting().create() : new Gson();
        return gson.toJson(c);
    }

    // public void decodeReturnToJsonV2(byte[] data) {
    //     // So this, actually stores the real data itself.
    //     Tuple value = this.f.decodeReturn(data);
    //     System.out.println("Return Data (Tuple):");
    //     for (int i = 0; i < value.size(); i++) {
    //         System.out.printf("[%d]%n", i);
    //         System.out.printf("Tuple? %s%n", value.get(i) instanceof Tuple);
    //         System.out.printf("Tuple[]? %s%n", value.get(i) instanceof Tuple[]);
    //         System.out.println(value.get(i));
    //         System.out.printf("Class? %s%n", value.get(i).getClass());
    //         Gson gson = new Gson();
    //         Object o = value.get(i) instanceof java.math.BigInteger ? ((BigInteger) value.get(i)).toString(16) : value.get(i);
    //         System.out.println(gson.toJson(o));
    //         System.out.println();
    //     }

    //     System.out.println("Output Types (TupleType):");
    //     TupleType guide = this.f.getOutputTypes();
    //     for (ABIType<?> e : guide) {
    //         System.out.printf("Canonical Type? %s%n", e.getCanonicalType());
    //         System.out.printf("Name? %s%n", e.getName());
    //         System.out.printf("Dynamic? %s%n", e.isDynamic());
    //         if (e instanceof UnitType) {
    //             System.out.println("UnitType Type.");
    //         } else {
    //             System.out.println("Not UnitType Type.");
    //         }
    //         if (e instanceof TupleType) {
    //             System.out.println("TupleType.");
    //         } else {
    //             System.out.println("Not TupleType.");
    //         }
    //         if (e instanceof ArrayType) {
    //             System.out.println("ArrayType Type.");
    //             ArrayType x = (ArrayType) e;
    //             System.out.println("array element type: " + x.getElementType());
    //         } else {
    //             System.out.println("Not ArrayType Type.");
    //         }
    //         System.out.println();
    //     }
    // }

    @Override
    public String toString() {
        return this.f.toString();
    }
}
