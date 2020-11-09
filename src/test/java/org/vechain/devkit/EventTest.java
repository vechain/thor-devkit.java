package org.vechain.devkit;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.vechain.devkit.cry.Utils;
import org.vechain.devkit.types.V1ParamWrapper;

// TODO - add more tests.

public class EventTest {
    final static String e1 =
    "{"
    + "    \"anonymous\": false,"
    + "    \"inputs\": ["
    + "        {"
    + "            \"indexed\": true,"
    + "            \"name\": \"a1\","
    + "            \"type\": \"uint256\""
    + "        },"
    + "        {"
    + "            \"indexed\": false,"
    + "            \"name\": \"a2\","
    + "            \"type\": \"string\""
    + "        }"
    + "    ],"
    + "    \"name\": \"E1\","
    + "    \"type\": \"event\""
    + "}";

    @Test
    public void e1Test() {
        Event e = new Event(e1);

        // Signature test
        byte[] expected = Utils.hexToBytes("47b78f0ec63d97830ace2babb45e6271b15a678528e901a9651e45b65105e6c2");
        assertEquals(e.calcEventSignature(), expected);

        // Topics decode test
        List<byte[]> topics = new ArrayList<byte[]>();
        topics.add(Utils.hexToBytes("47b78f0ec63d97830ace2babb45e6271b15a678528e901a9651e45b65105e6c2"));
        topics.add(Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000001"));

        List<V1ParamWrapper> indexedParams = e.decodeTopics(topics, false);
        assertEquals(indexedParams.size(), 1);
        assertEquals(indexedParams.get(0).canonicalType, "uint256");
        assertEquals(indexedParams.get(0).name, "a1");
        assertEquals(indexedParams.get(0).value, new BigInteger("1"));


        // Data decode test
        byte[] data = Utils.hexToBytes("00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000003666f6f0000000000000000000000000000000000000000000000000000000000");

        List<V1ParamWrapper> nonIndexedParams = e.decodeDataV1(data, true);
        assertEquals(nonIndexedParams.size(), 1);
        assertEquals(nonIndexedParams.get(0).name, "a2");
        assertEquals(nonIndexedParams.get(0).canonicalType, "string");
        assertEquals(nonIndexedParams.get(0).value, "foo");
    }

    final static String e2 =
    "{"
    + "    \"anonymous\": true,"
    + "    \"inputs\": ["
    + "        {"
    + "            \"indexed\": true,"
    + "            \"name\": \"a1\","
    + "            \"type\": \"uint256\""
    + "        },"
    + "        {"
    + "            \"indexed\": false,"
    + "            \"name\": \"a2\","
    + "            \"type\": \"string\""
    + "        }"
    + "    ],"
    + "    \"name\": \"E2\","
    + "    \"type\": \"event\""
    + "}"; // anonymous function.

    @Test
    public void e2Test() {
        Event e = new Event(e2);

        // Topics decode test
        List<byte[]> topics = new ArrayList<byte[]>();
        topics.add(Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000001"));

        List<V1ParamWrapper> indexedParams = e.decodeTopics(topics, false);
        assertEquals(indexedParams.size(), 1);
        assertEquals(indexedParams.get(0).canonicalType, "uint256");
        assertEquals(indexedParams.get(0).name, "a1");
        assertEquals(indexedParams.get(0).value, new BigInteger("1"));
    }

    final static String e3 = 
    "{"
    + "    \"anonymous\": False,"
    + "    \"inputs\": ["
    + "        {"
    + "            \"indexed\": True,"
    + "            \"name\": \"a1\","
    + "            \"type\": \"uint256\""
    + "        }"
    + "    ],"
    + "    \"name\": \"E3\","
    + "    \"type\": \"event\""
    + "}";

    @Test
    public void e3Test() {
        Event e = new Event(e3);

        // Topics decode test
        List<byte[]> topics = new ArrayList<byte[]>();
        topics.add(Utils.hexToBytes("e96585649d926cc4f5031a6113d7494d766198c0ac68b04eb93207460f9d7fd2"));
        topics.add(Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000001"));

        List<V1ParamWrapper> indexedParams = e.decodeTopics(topics, false);
        assertEquals(indexedParams.size(), 1);
        assertEquals(indexedParams.get(0).canonicalType, "uint256");
        assertEquals(indexedParams.get(0).name, "a1");
        assertEquals(indexedParams.get(0).value, new BigInteger("1"));
    }
}
