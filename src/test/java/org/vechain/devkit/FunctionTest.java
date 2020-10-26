package org.vechain.devkit;

import java.math.BigInteger;
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
        Function f = new Function(f1);
        f.decodeReturnToJsonV1(
            Utils.hexToBytes("000000000000000000000000abc000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000003666f6f0000000000000000000000000000000000000000000000000000000000"),
            true,
            true
        );
    }

    @Test
    public void decodeReturnV2() {
        Function f = new Function(f2);
        f.decodeReturnToJsonV2(
            Utils.hexToBytes("000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000060000000000000000000000006935455ef590eb8746f5230981d09d3552398018000000000000000000000000b5358b034647202d0cd3d1bf615e63e498e0268249984a53f9397370079bba8d95f5c15c743098fb318483e0cb6bbf46ec89ccfb00000000000000000000000000000000000000000000000000000000000000000000000000000000000000005ff66ee3a3ea2aba2857ea8276edb6190d9a1661000000000000000000000000d51666c6b4fed6070a78691f1f3c8e79ad02e3a076f090d383f49d8faab2eb151241528a552f0ae645f460360a7635b8883987a60000000000000000000000000000000000000000000000000000000000000000000000000000000000000000c5a02c1eac7516a9275d86c1cb39a5262b8684a4000000000000000000000000e32499b4143830f2526c79d388ecee530b6357aac635894a50ce5c74c62d238dbe95bd6a0fa076029d913d76b0d0b111c538153f00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000e8fd586e022f825a109848832d7e552132bc332000000000000000000000000224626926a7a12225a60e127cec119c939db4a5cdbf2712e19af00dc4d376728f7cb06cc215c8e7c53b94cb47cefb4a26ada2a6c0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000ea2e8c9d6dcad9e4be4f1c88a3befb8ea742832e0000000000000000000000001a011475baa1d368fa2d8328a1b7a8d848b62c94c68dc811199d40ff7ecd8c8d46454ad9ac5f5cde9bae32f927fec10d82dbdf7800000000000000000000000000000000000000000000000000000000000000000000000000000000000000004977d68df97bb313b23238520580d8d3a59939bf0000000000000000000000007ad1d568b3fe5bad3fc264aca70bc7bcd5e4a6ff83b137cf7e30864b8a4e56453eb1f094b4434685d86895de38ac2edcf5d3f5340000000000000000000000000000000000000000000000000000000000000000")
        );
    }
}
