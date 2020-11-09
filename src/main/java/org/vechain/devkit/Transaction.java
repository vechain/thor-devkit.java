package org.vechain.devkit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.esaulpaugh.headlong.rlp.RLPDecoder;
import com.esaulpaugh.headlong.rlp.RLPItem;

import org.vechain.devkit.types.Clause;

/**
 * Tx = { [clause, clause, ...] }
 */
public class Transaction {
    List<Clause> clauses;

    public Transaction(List<Clause> clauses){
        this.clauses = clauses;
    }

    // public static void fromBytes(byte[] data) {
    //     Iterator<RLPItem> tx = RLPDecoder.RLP_STRICT.sequenceIterator(data);

    //     byte[] clauses = tx.next().asBytes();
    //     Iterator<RLPItem> clausesIterator = RLPDecoder.RLP_STRICT.listIterator(clauses);
    //     while(clausesIterator.hasNext()){
    //         int temp = clausesIterator.next().asInt();
    //         System.out.println(temp);
    //     }
    // }
}
