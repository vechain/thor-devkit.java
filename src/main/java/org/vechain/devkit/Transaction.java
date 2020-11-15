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

import org.vechain.devkit.cry.Utils;
import org.vechain.devkit.types.Clause;

/**
 * Tx = { [clause, clause, ...] }
 */
public class Transaction {

    final List<Clause> clauses;

    public Transaction(List<Clause> clauses){
        this.clauses = clauses;
    }

    /**
     * Construct a Transaction from byte[] data.
     * @param data
     * @return
     */
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

    /**
     * Calculate the gas used by the data section.
     * 
     * @param data Thre pure bytes of the data.
     * @return
     */
    static int calcDataGas(byte[] data) {
        final int Z_GAS = 4;
        final int NZ_GAS = 68;

        int sum = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == (byte) 0) {
                sum += Z_GAS;
            } else {
                sum += NZ_GAS;
            }
        }
        return sum;
    }

    /**
     * Calculate the gas used by the data section.
     * 
     * @param hexString "0x..." the data.
     * @return
     */
    static int calcDataGas(String hexString) {
        String realHex = Utils.remove0x(hexString);
        byte[] realData = Utils.hexToBytes(realHex);
        return calcDataGas(realData);
    }

    /**
     * Calculate roughly the gas from a list of clauses.
     * 
     * @param clauses A list of clauses.
     * @return
     */
    static int calcIntrinsicGas(List<Clause> clauses) {
        final int TX_GAS = 5000;
        final int CLAUSE_GAS = 16000;
        final int CLAUSE_CONTRACT_CREATION = 48000;

        // Hey bro fill in something.
        if (clauses == null) {
            throw new IllegalArgumentException("Input is null");
        }

        // Must pay a static fee even empty!
        if (clauses.size() == 0) {
            return TX_GAS + CLAUSE_GAS;
        }

        int sum = 0;
        sum += TX_GAS;

        for (Clause c: clauses) {
            int clauseSum = 0;

            if (c.to.toBytes().length == 0) { // contract creation
                clauseSum += CLAUSE_CONTRACT_CREATION;
            } else { // or a normal clause
                clauseSum += CLAUSE_GAS;
            }

            clauseSum += calcDataGas(c.data.toBytes());
            sum += clauseSum;
        }

        return sum;
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
