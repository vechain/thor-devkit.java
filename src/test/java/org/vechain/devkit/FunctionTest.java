package org.vechain.devkit;

import java.math.BigInteger;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import org.vechain.devkit.cry.Utils;

public class FunctionTest {
    static String f1 =
    "{" +
    "    \"constant\": false," +
    "    \"inputs\": [" +
    "        {" +
    "            \"name\": \"a1\"," +
    "            \"type\": \"uint256\"" +
    "        }," +
    "        {" +
    "            \"name\": \"a2\"," +
    "            \"type\": \"string\"" +
    "        }" +
    "    ]," +
    "    \"name\": \"f1\"," +
    "    \"outputs\": [" +
    "        {" +
    "            \"name\": \"r1\"," +
    "            \"type\": \"address\"" +
    "        }," +
    "        {" +
    "            \"name\": \"r2\"," +
    "            \"type\": \"bytes\"" +
    "        }" +
    "    ]," +
    "    \"payable\": False," +
    "    \"stateMutability\": \"nonpayable\"," +
    "    \"type\": \"function\"" +
    "}";
    @Test
    public void encodeTest() {
        Function f = new Function(f1);
        assertEquals(
            f.encodeToHex(true, BigInteger.valueOf(1), "foo"),
            "0x27fcbb2f000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000003666f6f0000000000000000000000000000000000000000000000000000000000"
        );
    }

    public void selectorText() {
        Function f = new Function(f1);
        assertEquals(
             Utils.bytesToHex(f.selector()),
             "27fcbb2f"
        );
    }
}
