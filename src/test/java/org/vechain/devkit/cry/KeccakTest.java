package org.vechain.devkit.cry;

import org.testng.annotations.*;
import static org.testng.Assert.*;

import java.nio.charset.Charset;

public class KeccakTest {

    public static String toHexString(byte[] input) {
        StringBuffer ret = new StringBuffer();
        for (byte a : input) {
            ret.append(String.format("%02x", a));
        }
        return ret.toString();
    }

    @Test
    public void keccak256Test() {
        String input = "hello world";
        byte[] output = Keccak.keccak256(input.getBytes(Charset.forName("ASCII")));
        assertEquals(
            toHexString(output),
            "47173285a8d7341e5e972fc677286384f802f8ef42a5ec5f03bbfa254cb01fad"
        );
    }

    @Test
    public void keccak256Test2() {
        String[] inputs = {"hello", " ", "world"};
        byte[] output = Keccak.keccak256(
            inputs[0].getBytes(Charset.forName("ASCII")),
            inputs[1].getBytes(Charset.forName("ASCII")),
            inputs[2].getBytes(Charset.forName("ASCII"))
        );
        assertEquals(
            toHexString(output),
            "47173285a8d7341e5e972fc677286384f802f8ef42a5ec5f03bbfa254cb01fad"
        );
    }
}
