package org.vechain.devkit.types;

import org.vechain.devkit.cry.Utils;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.ArrayList;

public class ClauseTest {
    @Test
    public void simpleTest() {
        List<Clause> clauses = new ArrayList<Clause>();
        for (int a = 1; a < 5; a++) {
            clauses.add(new Clause(Integer.toString(a).repeat(a), a));
        }

        // [0, 1, 2, ...]
        for (Clause c: clauses) {
            // serialize.
            byte[] encoded = c.toRLP();
            System.out.println(Utils.bytesToHex(encoded));
            // deserialize
            Clause decoded = Clause.fromBytes(encoded);
            System.out.println(decoded.name);
            System.out.println(decoded.value);
        }
    }
}
