package org.vechain.devkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.esaulpaugh.headlong.rlp.RLPDecoder;
import com.esaulpaugh.headlong.rlp.RLPEncoder;
import com.esaulpaugh.headlong.rlp.RLPItem;
import com.google.gson.Gson;

import org.vechain.devkit.types.Clause;

/**
 * Tx = { [clause, clause, ...] }
 */
public class Transaction {
    final List<Clause> clauses;

    public Transaction(List<Clause> clauses){
        this.clauses = clauses;
    }

    public static Transaction fromBytes(byte[] data) {
        // byte[] -> tx
        Iterator<RLPItem> tx = RLPDecoder.RLP_STRICT.sequenceIterator(data);
        // tx -> clauses
        byte[] clauses = tx.next().asBytes();
        // clauses -> fit into Transaction object.
        List<Clause> myClauses = new ArrayList<Clause>();
        Iterator<RLPItem> clausesIterator = RLPDecoder.RLP_STRICT.sequenceIterator(clauses);
        while(clausesIterator.hasNext()){
            byte[] c = clausesIterator.next().asBytes();
            Clause clause = Clause.fromBytes(c);
            myClauses.add(clause);
        }

        return new Transaction(myClauses);
    }

    public Object[] toObjectArray() {
        List<byte[]> cls = new ArrayList<byte[]>();
        for (Clause x: this.clauses) {
            cls.add(x.toRLP());
        }
        return new Object[] {
            cls
        };
    }

    public byte[] toRLP() {
        return RLPEncoder.encodeSequentially(toObjectArray());
    }

    @Override
    public String toString() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("clauses", this.clauses);
        Gson gson = new Gson();
        return gson.toJson(m);
    }
}
