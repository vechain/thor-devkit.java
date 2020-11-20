package org.vechain.devkit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.esaulpaugh.headlong.abi.ABIType;
import com.esaulpaugh.headlong.abi.BigDecimalType;
import com.esaulpaugh.headlong.abi.BigIntegerType;
import com.esaulpaugh.headlong.abi.BooleanType;
import com.esaulpaugh.headlong.abi.ByteType;
import com.esaulpaugh.headlong.abi.IntType;
import com.esaulpaugh.headlong.abi.LongType;
import com.esaulpaugh.headlong.abi.Tuple;
import com.esaulpaugh.headlong.abi.TupleType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.vechain.devkit.cry.Keccak;
import org.vechain.devkit.cry.Utils;
import org.vechain.devkit.types.V1ParamWrapper;


public class Event {

    static final List<Class> basicTypes = Arrays.asList(new Class[] {
        BigDecimalType.class,
        BigIntegerType.class,
        BooleanType.class,
        ByteType.class,
        IntType.class,
        LongType.class
    });

    private com.esaulpaugh.headlong.abi.Event event;

    public Event(String jsonString) {
        this.event = com.esaulpaugh.headlong.abi.Event.fromJson(jsonString);
    }

    /**
     * This signature is almost the same as the Function selector.
     * @return The signature bytes.
     */
    public byte[] calcEventSignature() {
        String s = this.event.getCanonicalSignature();
        byte[] sig = Keccak.keccak256(
            Utils.UTF8ToBytes(s)
        );
        return sig;
    }

    /**
     * Decode the data part according to the abi specs.
     * 
     * Non-indexed params are decoded normally according
     * to the abi specs.
     * 
     * @param data
     * @return
     */
    public Tuple decodeData(byte[] data) {
        TupleType guide = this.event.getNonIndexedParams();
        return guide.decode(data);
    }

    /**
     * Decode the data section of event. -> ABI v1.
     * @param data
     * @param human
     * @return
     */
    public List<V1ParamWrapper> decodeDataV1(byte[] data, boolean human) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("data is empty or null!");
        }
        TupleType guide = this.event.getNonIndexedParams();
        Tuple params = this.decodeData(data);
        return Function.decodeParams(params, guide, human);
    }

    /**
     * Decode the data section of an event. -> ABI v1.
     * @param data
     * @param pretty
     * @param human
     * @return
     */
    public String decodeDataV1Json(byte[] data, boolean pretty, boolean human) {
        Collection<V1ParamWrapper> c = decodeDataV1(data, human);
        Gson gson = pretty ? new GsonBuilder().setPrettyPrinting().create() : new Gson();
        return gson.toJson(c);
    }

    /**
     * Decode the topics section of an event.
     * 
     * For simple values, a topic entry is the abi(variable).
     * 
     * For complex values, a topic entry is the keccak() of the 
     * intermediary of the original value.
     * 
     * For non-anonymous Events, topics[0] is the event signature.
     * topics[1] topics[2] topics[3] are the indexed parameters.
     * 
     * For anonymous Events, event signature isn't included. So indexed
     * parameters starts with topics[0].
     * 
     * @param topics
     */
    public List<V1ParamWrapper> decodeTopics(List<byte[]> topics, boolean human) {
        // Check if non-anonymous event signature matches topics[0]
        if (!this.event.isAnonymous()){
            if (!Arrays.equals(topics.get(0), this.calcEventSignature())) {
                throw new IllegalArgumentException("topics.get(0) for non-anonymous doesnt match.");
            }
        }

        final int start = this.event.isAnonymous() ? 0 : 1;
        // This is a view of the original list.
        List<byte[]> topicsSlice = topics.subList(start, topics.size());

        // A List of "type" information. (iterator)
        TupleType guide = this.event.getIndexedParams();

        // Check length
        if (guide.size() != topicsSlice.size()) {
            throw new RuntimeException("params length doesn't match.");
        }

        List<V1ParamWrapper> c = new ArrayList<V1ParamWrapper>();
        int i = 0;
        for (ABIType<?> e: guide) {
            Object value = decodeSingle(e, topicsSlice.get(i), human);
            c.add(new V1ParamWrapper(i, e.getName(), e.getCanonicalType(), value));
            i++;
        }

        return c;
    }

    /**
     * Decode topics to Json style.
     * @param topics
     * @param pretty
     * @param human
     * @return
     */
    public String decodeTopicsJson(List<byte[]> topics, boolean pretty, boolean human) {
        Collection<V1ParamWrapper> c = decodeTopics(topics, human);
        Gson gson = pretty ? new GsonBuilder().setPrettyPrinting().create() : new Gson();
        return gson.toJson(c);
    }

    /**
     * Decode the single param, if complex data,
     * then just return the original byte[] data.
     * 
     * The object can be one of the BigInteger, int, long etc.
     * 
     * @param e
     * @param data
     * @return
     */
    public Object decodeSingle(ABIType<?> e, byte[] data, boolean human) {
        if (!basicTypes.contains(e.getClass())) {
            return data;
        }

        try {
            Object value = e.decode(data);
            if (human) {
                // Covert BigIntegers.
                if (value.getClass() == BigInteger.class) {
                    // Convert address to String type of "0x..." for display.
                    if (e.getCanonicalType() == "address") {
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

            return value;

        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    @Override
    public String toString() {
        return this.event.toString();
    }
}
