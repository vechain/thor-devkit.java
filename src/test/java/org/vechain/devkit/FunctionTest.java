package org.vechain.devkit;

import java.math.BigInteger;
import java.util.List;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import org.vechain.devkit.cry.Utils;

public class FunctionTest {
    final static String f1 =
    "{"
    + "    \"constant\": false,"
    + "    \"inputs\": ["
    + "        {"
    + "            \"name\": \"a1\","
    + "            \"type\": \"uint256\""
    + "        },"
    + "        {"
    + "            \"name\": \"a2\","
    + "            \"type\": \"string\""
    + "        }"
    + "    ],"
    + "    \"name\": \"f1\","
    + "    \"outputs\": ["
    + "        {"
    + "            \"name\": \"r1\","
    + "            \"type\": \"address\""
    + "        },"
    + "        {"
    + "            \"name\": \"r2\","
    + "            \"type\": \"bytes\""
    + "        }"
    + "    ],"
    + "    \"payable\": false,"
    + "    \"stateMutability\": \"nonpayable\","
    + "    \"type\": \"function\""
    + "}";

    final static String f2 = 
    "{"
    + "    \"inputs\": [],"
    + "    \"name\": \"nodes\","
    + "    \"payable\": false,"
    + "    \"outputs\": ["
    + "        {"
    + "            \"components\": ["
    + "                {"
    + "                    \"internalType\": \"address\","
    + "                    \"name\": \"master\","
    + "                    \"type\": \"address\""
    + "                },"
    + "                {"
    + "                    \"internalType\": \"address\","
    + "                    \"name\": \"endorsor\","
    + "                    \"type\": \"address\""
    + "                },"
    + "                {"
    + "                    \"internalType\": \"bytes32\","
    + "                    \"name\": \"identity\","
    + "                    \"type\": \"bytes32\""
    + "                },"
    + "                {"
    + "                    \"internalType\": \"bool\","
    + "                    \"name\": \"active\","
    + "                    \"type\": \"bool\""
    + "                }"
    + "            ],"
    + "            \"internalType\": \"struct AuthorityUtils.Candidate[]\","
    + "            \"name\": \"list\","
    + "            \"type\": \"tuple[]\""
    + "        }"
    + "    ],"
    + "    \"stateMutability\": \"nonpayable\","
    + "    \"type\": \"function\""
    + "}";

    // Solidity
    // function getStr() public pure returns (string memory) {
    //    return "Hello World!";
    // }
    final static String f3 = 
    "{"
    + "    \"inputs\": [],"
    + "    \"name\": \"getStr\","
    + "    \"outputs\": ["
    + "        {"
    + "            \"internalType\": \"string\","
    + "            \"name\": \"\","
    + "            \"type\": \"string\""
    + "        }"
    + "    ],"
    + "    \"stateMutability\": \"pure\","
    + "    \"type\": \"function\""
    + "}";

    // Solidity
    // function getBool() public pure returns (bool) {
    //     return true;
    // }
    final static String f4 = 
    "{"
    + "    \"inputs\": [],"
    + "    \"name\": \"getBool\","
    + "    \"outputs\": ["
    + "        {"
    + "            \"internalType\": \"bool\","
    + "            \"name\": \"\","
    + "            \"type\": \"bool\""
    + "        }"
    + "    ],"
    + "    \"stateMutability\": \"pure\","
    + "    \"type\": \"function\""
    + "}";

    // Solidity
    // function getBigNumbers() public pure returns (uint256 a, int256 b) {
    //     return (123456, -123456);
    // }
    final static String f5 =
    "{"
    + "    \"inputs\": [],"
    + "    \"name\": \"getBigNumbers\","
    + "    \"outputs\": ["
    + "        {"
    + "            \"internalType\": \"uint256\","
    + "            \"name\": \"a\","
    + "            \"type\": \"uint256\""
    + "        },"
    + "        {"
    + "            \"internalType\": \"int256\","
    + "            \"name\": \"b\","
    + "            \"type\": \"int256\""
    + "        }"
    + "    ],"
    + "    \"stateMutability\": \"pure\","
    + "    \"type\": \"function\""
    + "}";

    @Test
    public void encodeTest() {
        Function f = new Function(f1);
        assertEquals(
            f.encodeToHex(true, BigInteger.valueOf(1), "foo"),
            "0x27fcbb2f000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000003666f6f0000000000000000000000000000000000000000000000000000000000"
        );
    }

    @Test
    public void selectorText() {
        Function f = new Function(f1);
        assertEquals(
             Utils.bytesToHex(f.selector()),
             "27fcbb2f"
        );
    }

    @Test
    public void decodeReturnV1() {
        final byte[] data = Utils.hexToBytes("000000000000000000000000abc000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000003666f6f0000000000000000000000000000000000000000000000000000000000");
        Function f = new Function(f1);

        // Uncomment following line for debug.
        // System.out.println(f.decodeReturnV1Json(data, true, true));

        List<V1ParamWrapper> result = f.decodeReturnV1(data, true);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).name, "r1");
        assertEquals(result.get(0).value, "0xabc0000000000000000000000000000000000001");
        assertEquals(result.get(1).name, "r2");
        assertEquals(result.get(1).value, "0x666f6f");
    }

    @Test
    public void decodeReturnV1String() {
        final byte[] data = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000c48656c6c6f20576f726c64210000000000000000000000000000000000000000");
        Function f = new Function(f3);

        // Uncomment following line for debug.
        // System.out.println(f.decodeReturnV1Json(data, true, true));
        
        List<V1ParamWrapper> result = f.decodeReturnV1(data, true);
        assertEquals(result.get(0).name, "");
        assertEquals(result.get(0).value, "Hello World!");

        List<V1ParamWrapper> result2 = f.decodeReturnV1(data, false);
        assertEquals(result.get(0).name, "");
        assertEquals(result2.get(0).value, "Hello World!");
    }

    @Test
    public void decodeReturnV1Bool() {
        final byte[] data = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000001");
        Function f = new Function(f4);
        // Uncomment following line for debug.
        // System.out.println(f.decodeReturnV1Json(data, true, true));

        List<V1ParamWrapper> result = f.decodeReturnV1(data, false);
        assertEquals(result.get(0).value, true);
    }

    @Test
    public void decodeReturnV1BigInteger() {
        final byte[] data = Utils.hexToBytes("000000000000000000000000000000000000000000000000000000000001e240fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe1dc0");
        Function f = new Function(f5);

        // Uncomment following line for debug.
        // System.out.println(f.decodeReturnV1Json(data, true, true));

        // Should be BigInteger if not "human".
        List<V1ParamWrapper> result = f.decodeReturnV1(data, false);
        assertEquals(result.get(0).value, new BigInteger("123456"));
        assertEquals(result.get(1).value, new BigInteger("-123456"));

        // Should be String representation choose to "human".
        List<V1ParamWrapper> result2 = f.decodeReturnV1(data, true);
        assertEquals(result2.get(0).value, "123456");
        assertEquals(result2.get(1).value, "-123456");
    }
}
