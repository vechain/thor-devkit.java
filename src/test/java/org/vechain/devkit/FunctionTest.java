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

        List<V1DisplayWrapper> result = f.decodeReturnV1(data, true);
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
        
        List<V1DisplayWrapper> result = f.decodeReturnV1(data, true);
        assertEquals(result.get(0).name, "");
        assertEquals(result.get(0).value, "Hello World!");

        List<V1DisplayWrapper> result2 = f.decodeReturnV1(data, false);
        assertEquals(result.get(0).name, "");
        assertEquals(result2.get(0).value, "Hello World!");
    }

    @Test
    public void decodeReturnV1Bool() {
        final byte[] data = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000001");
        Function f = new Function(f4);
        // Uncomment following line for debug.
        // System.out.println(f.decodeReturnV1Json(data, true, true));

        List<V1DisplayWrapper> result = f.decodeReturnV1(data, false);
        assertEquals(result.get(0).value, true);
    }

    @Test
    public void decodeReturnV1BigInteger() {
        final byte[] data = Utils.hexToBytes("000000000000000000000000000000000000000000000000000000000001e240fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe1dc0");
        Function f = new Function(f5);

        // Uncomment following line for debug.
        // System.out.println(f.decodeReturnV1Json(data, true, true));

        // Should be BigInteger if not "human".
        List<V1DisplayWrapper> result = f.decodeReturnV1(data, false);
        assertEquals(result.get(0).value, new BigInteger("123456"));
        assertEquals(result.get(1).value, new BigInteger("-123456"));

        // Should be String representation choose to "human".
        List<V1DisplayWrapper> result2 = f.decodeReturnV1(data, true);
        assertEquals(result2.get(0).value, "123456");
        assertEquals(result2.get(1).value, "-123456");
    }

    // @Test
    // public void decodeReturnV2() {
    //     Function f = new Function(f2);
    //     f.decodeReturnToJsonV2(
    //         Utils.hexToBytes("000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000060000000000000000000000006935455ef590eb8746f5230981d09d3552398018000000000000000000000000b5358b034647202d0cd3d1bf615e63e498e0268249984a53f9397370079bba8d95f5c15c743098fb318483e0cb6bbf46ec89ccfb00000000000000000000000000000000000000000000000000000000000000000000000000000000000000005ff66ee3a3ea2aba2857ea8276edb6190d9a1661000000000000000000000000d51666c6b4fed6070a78691f1f3c8e79ad02e3a076f090d383f49d8faab2eb151241528a552f0ae645f460360a7635b8883987a60000000000000000000000000000000000000000000000000000000000000000000000000000000000000000c5a02c1eac7516a9275d86c1cb39a5262b8684a4000000000000000000000000e32499b4143830f2526c79d388ecee530b6357aac635894a50ce5c74c62d238dbe95bd6a0fa076029d913d76b0d0b111c538153f00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000e8fd586e022f825a109848832d7e552132bc332000000000000000000000000224626926a7a12225a60e127cec119c939db4a5cdbf2712e19af00dc4d376728f7cb06cc215c8e7c53b94cb47cefb4a26ada2a6c0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000ea2e8c9d6dcad9e4be4f1c88a3befb8ea742832e0000000000000000000000001a011475baa1d368fa2d8328a1b7a8d848b62c94c68dc811199d40ff7ecd8c8d46454ad9ac5f5cde9bae32f927fec10d82dbdf7800000000000000000000000000000000000000000000000000000000000000000000000000000000000000004977d68df97bb313b23238520580d8d3a59939bf0000000000000000000000007ad1d568b3fe5bad3fc264aca70bc7bcd5e4a6ff83b137cf7e30864b8a4e56453eb1f094b4434685d86895de38ac2edcf5d3f5340000000000000000000000000000000000000000000000000000000000000000")
    //     );
    // }
}
