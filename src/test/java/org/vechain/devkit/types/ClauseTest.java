package org.vechain.devkit.types;

import org.vechain.devkit.cry.Utils;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.math.BigInteger;
import java.util.ArrayList;

public class ClauseTest {
    @Test
    public void encodeDecode() {
        // Prepare
        List<Clause> clauses = new ArrayList<Clause>();
        for (int a = 0; a < 3; a++) {
            clauses.add(new Clause(
                "0x7567d83b7b8d80addcb281a71d54fc7b3364ffed",
                "10000",
                "0x000000606060"
            ));
        }

        // 
        for (Clause c: clauses) {
            // serialize.
            byte[] encoded = c.toRLP();

            // deserialize
            Clause decoded = Clause.fromBytes(encoded);
            
            assertEquals(
                decoded.to.toBytes(),
                Utils.hexToBytes("7567d83b7b8d80addcb281a71d54fc7b3364ffed")
            );

            assertEquals(
                decoded.data.toBytes(),
                Utils.hexToBytes("000000606060")
            );

            assertEquals(
                decoded.value.toBytes(), 
                new BigInteger("10000").toByteArray()
            );
        }
    }
}
