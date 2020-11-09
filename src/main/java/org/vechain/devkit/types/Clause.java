package org.vechain.devkit.types;

import java.util.Iterator;

import com.esaulpaugh.headlong.rlp.RLPDecoder;
import com.esaulpaugh.headlong.rlp.RLPEncoder;
import com.esaulpaugh.headlong.rlp.RLPItem;
import com.esaulpaugh.headlong.util.Integers;
import com.esaulpaugh.headlong.util.Strings;

/**
 * Clause = {int data}
 */
public class Clause {
    public String name;
    public int value;

    public Clause(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Construct a clause from raw bytes.
     * @param data
     * @return
     */
    public static Clause fromBytes(byte[] data) {
        Iterator<RLPItem> clause = RLPDecoder.RLP_STRICT.sequenceIterator(data);
        return new Clause(
            clause.next().asString(Strings.UTF_8),
            clause.next().asInt()
        );
    }

    /**
     * Put each object into bytes, then group them into a list.
     * @return
     */
    public Object[] toObjectArray() {
        return new Object[] {
            Strings.decode(this.name, Strings.UTF_8),
            Integers.toBytes(this.value)
        };
    }

    /**
     * Encode a group of elements in to findal rlp represented bytes.
     * @return
     */
    public byte[] toRLP() {
        return RLPEncoder.encodeSequentially(toObjectArray());
    }
}
