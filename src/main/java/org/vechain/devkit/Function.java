package org.vechain.devkit;

import java.nio.ByteBuffer;

import com.esaulpaugh.headlong.abi.ABIType;
import com.esaulpaugh.headlong.abi.ArrayType;
import com.esaulpaugh.headlong.abi.Tuple;
import com.esaulpaugh.headlong.abi.TupleType;
import com.esaulpaugh.headlong.abi.UnitType;

import org.vechain.devkit.cry.Utils;

/**
 * ABI Function related operations.
 * 1) Initialize a function instance according to the definition.
 * 2) Encode the parameters into bytes.
 * 3) Decode the call return value into Map or JSON.
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
    public byte[] selector(){
        return this.f.selector();
    }

    /**
     * Encode the parameters into valid byte buffer.
     * @param args
     * @return
     */
    private ByteBuffer encode(Object... args) {
        return this.f.encodeCallWithArgs(args);
    }

    /**
     * Encode the parameters into valid bytes.
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

    public void decodeReturnToJson(byte[] data) {
        Tuple value = this.f.decodeReturn(data);
        System.out.println("Tuple:");
        for (int i = 0; i < value.size(); i ++) {
            System.out.printf("[%d]%n", i);
            System.out.println(value.get(i));
            System.out.println();
        }

        System.out.println("TupleType:");
        TupleType guide = this.f.getOutputTypes();
        for (ABIType<?> e: guide) {
            System.out.println(e.getCanonicalType());
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
