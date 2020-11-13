package org.vechain.devkit.types;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.esaulpaugh.headlong.rlp.RLPDecoder;
import com.esaulpaugh.headlong.rlp.RLPEncoder;
import com.esaulpaugh.headlong.rlp.RLPItem;
import com.google.gson.Gson;

/**
 * This is part of the Transaction structure.
 * 
 * Clause = {
 *   to:    NullableFixedBlobKind(20)   // to address
 *   value: NumericKind(32)             // how much vet.
 *   data:  BlobKind()                  // the call data, if not call data fill in "0x"
 * }
 */
public class Clause {
    public final NullableFixedBlobKind to = new NullableFixedBlobKind(20);
    public final NumericKind value = new NumericKind(32);
    public final BlobKind data = new BlobKind();

    // Constructor
    public Clause(String to, String value, String data) {
        this.to.setValue(to);
        this.value.setValue(value);
        this.data.setValue(data);
    }

    // Constructor
    public Clause(byte[] to, byte[] value, byte[] data) {
        this.to.fromBytes(to);
        this.value.fromBytes(value);
        this.data.fromBytes(data);
    }

    /**
     * Deserialize via RLP from raw bytes to a Clause.
     * @param data
     * @return
     */
    public static Clause fromBytes(byte[] data) {
        Iterator<RLPItem> clause = RLPDecoder.RLP_STRICT.sequenceIterator(data);
        return new Clause(
            clause.next().asBytes(),
            clause.next().asBytes(),
            clause.next().asBytes()
        );
    }

    /**
     * Encode each object into bytes, 
     * then group them into a sequence of list.
     * @return
     */
    public Object[] toObjectArray() {
        return new Object[] {
            this.to.toBytes(),
            this.value.toBytes(),
            this.data.toBytes()
        };
    }

    /**
     * Encode the Clause to RLP represented bytes.
     * @return
     */
    public byte[] toRLP() {
        return RLPEncoder.encodeSequentially(toObjectArray());
    }

    @Override
    public String toString() {
        Map<String, String> m = new HashMap<String, String>();
        m.put("to", "0x" + this.to.toString());
        m.put("value", this.value.toString());
        m.put("data", "0x" + this.data.toString());

        Gson gson = new Gson();
        return gson.toJson(m);
    }
}
