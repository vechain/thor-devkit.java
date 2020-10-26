package org.vechain.devkit;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import com.esaulpaugh.headlong.abi.ABIType;
import com.esaulpaugh.headlong.abi.ArrayType;
import com.esaulpaugh.headlong.abi.Tuple;
import com.esaulpaugh.headlong.abi.TupleType;
import com.esaulpaugh.headlong.abi.UnitType;
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

    class DisplayWrapper {
        private int index;
        private String name;
        private String canonicalType;
        private Object value;

        public DisplayWrapper(
            int index,
            String name,
            String canonicalType,
            Object value)
        {
            this.index = index;
            this.name = name;
            this.canonicalType = canonicalType;
            this.value = value;
        }
    }

    /**
     * Decode the output according to the Json String.
     * 
     * "human" option will enable the output:
     * 1) "address" convert to "0x..." rather than BigInteger.
     * 2) "bytes32"/"bytes[]" convert o "0x..." rather than byte[].
     * 
     * @param data
     * @param pretty Indentation or not.
     * @param human Convert output human readable or not.
     * @return
     */
    public String decodeReturnToJsonV1(byte[] data, boolean pretty, boolean human) {
        Tuple params = this.f.decodeReturn(data);
        TupleType guide = this.f.getOutputTypes();
        Collection<DisplayWrapper> c = new ArrayList<DisplayWrapper>();
        int i = 0;
        for (ABIType<?> e : guide) {
            final int index = i;
            final String name = e.getName();
            final String canonicalType = e.getCanonicalType();
            Object value = params.get(i);

            if (human) {
                if (canonicalType == "address") {
                    value = "0x" + ((BigInteger) value).toString(16);
                }
                if (canonicalType.startsWith("bytes")) {
                    value = "0x" + Utils.bytesToHex((byte[])value);
                }
            }

            final DisplayWrapper dw = new DisplayWrapper(index, name, canonicalType, value);
            c.add(dw);
            i++;
        }

        Gson gson = pretty ? new GsonBuilder().setPrettyPrinting().create() : new Gson();
        // TODO remove the line below in production.
        System.out.println(gson.toJson(c));
        return gson.toJson(c);
    }

    private boolean isUnitType(ABIType<?> e) {
        return e instanceof UnitType;
    }

    private boolean isArrayType(ABIType<?> e) {
        return e instanceof ArrayType;
    }

    private boolean isTupleType(ABIType<?> e) {
        return e instanceof TupleType;
    }

    public void decodeReturnToJsonV2(byte[] data) {
        // First let's resolve the ABI v1 style problem.

        // So this, actually stores the real data itself.
        Tuple value = this.f.decodeReturn(data);
        System.out.println("Return Data (Tuple):");
        for (int i = 0; i < value.size(); i++) {
            System.out.printf("[%d]%n", i);
            System.out.printf("Tuple? %s%n", value.get(i) instanceof Tuple);
            System.out.printf("Tuple[]? %s%n", value.get(i) instanceof Tuple[]);
            System.out.println(value.get(i));
            System.out.printf("Class? %s%n", value.get(i).getClass());
            Gson gson = new Gson();
            Object o = value.get(i) instanceof java.math.BigInteger ? ((BigInteger) value.get(i)).toString(16) : value.get(i);
            System.out.println(gson.toJson(o));
            System.out.println();
        }

        System.out.println("Output Types (TupleType):");
        TupleType guide = this.f.getOutputTypes();
        for (ABIType<?> e : guide) {
            System.out.printf("Canonical Type? %s%n", e.getCanonicalType());
            System.out.printf("Name? %s%n", e.getName());
            System.out.printf("Dynamic? %s%n", e.isDynamic());
            if (e instanceof UnitType) {
                System.out.println("UnitType Type.");
            } else {
                System.out.println("Not UnitType Type.");
            }
            if (e instanceof TupleType) {
                System.out.println("TupleType.");
            } else {
                System.out.println("Not TupleType.");
            }
            if (e instanceof ArrayType) {
                System.out.println("ArrayType Type.");
                ArrayType x = (ArrayType) e;
                System.out.println("array element type: " + x.getElementType());
            } else {
                System.out.println("Not ArrayType Type.");
            }
            System.out.println();
        }
    }

    @Override
    public String toString() {
        return this.f.toString();
    }
}
