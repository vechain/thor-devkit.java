package org.vechain.devkit;

import org.vechain.devkit.cry.Utils;
import org.vechain.devkit.types.Clause;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class TransactionTest {
    @Test
    public void unsignedTx() {

        Clause[] clauses = new Clause[]{
            new Clause(
                "0x7567d83b7b8d80addcb281a71d54fc7b3364ffed",
                "10000",
                "0x000000606060"
            ),
            new Clause(
                "0x7567d83b7b8d80addcb281a71d54fc7b3364ffed",
                "20000",
                "0x000000606060"
            )
        };

        Transaction tx = new Transaction(
            "1",
            "0x00000000aabbccdd",
            "32",
            clauses,
            "128",
            "21000",
            null,
            "12345678",
            null
        );

        // Uncomment below line for debugging.
        // Utils.prettyPrint(tx.getUnsignedTxBody().toArray(), 0);

        assertEquals(
            tx.encode(),
            Utils.hexToBytes("f8540184aabbccdd20f840df947567d83b7b8d80addcb281a71d54fc7b3364ffed82271086000000606060df947567d83b7b8d80addcb281a71d54fc7b3364ffed824e208600000060606081808252088083bc614ec0")
        );

        assertEquals(
            tx.getSigningHash(null),
            Utils.hexToBytes("2a1c25ce0d66f45276a5f308b99bf410e2fc7d5b6ea37a49f2ab9f1da9446478")
        );

        assertEquals(tx.getId(), null);
        assertEquals(tx.getIntrinsicGas(), 37432);
        assertEquals(tx.getSignature(), null);
        assertEquals(tx.getOriginAsPublicKey(), null);
        assertEquals(tx.getOriginAsAddressBytes(), null);
        assertEquals(tx.getOriginAsAddressString(), null);
    }
}
