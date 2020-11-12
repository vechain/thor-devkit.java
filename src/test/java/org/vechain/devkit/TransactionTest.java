package org.vechain.devkit;

import java.util.List;
import java.util.ArrayList;

import org.vechain.devkit.cry.Utils;
import org.vechain.devkit.types.Clause;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class TransactionTest {
    @Test
    public void trial() {

        // Prepare Clauses
        List<Clause> clauses = new ArrayList<Clause>();
        for (int a = 0; a < 3; a++) {
            clauses.add(new Clause(
                "0x7567d83b7b8d80addcb281a71d54fc7b3364ffed",
                "10000",
                "0x000000606060"
            ));
        }

        // Prepare a Transaction
        Transaction tx = new Transaction(clauses);
        System.out.println(tx.toString());

        // encode
        byte[] encoded = tx.toRLP();
        System.out.println(Utils.bytesToHex(encoded));

        // decode
        Transaction decoded = Transaction.fromBytes(encoded);
        System.out.println(decoded.toString());
    }
}
