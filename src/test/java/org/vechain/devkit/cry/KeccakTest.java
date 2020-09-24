package org.vechain.devkit.cry;

import org.testng.annotations.*;
import static org.testng.Assert.*;


public class KeccakTest {

    @Test
    public void keccak256Test() {
        String input = "hello world";
        byte[] output = Keccak.keccak256(
            Utils.AsciiToBytes(input)
        );
        assertEquals(
            Utils.bytesToHex(output),
            "47173285a8d7341e5e972fc677286384f802f8ef42a5ec5f03bbfa254cb01fad"
        );
    }

    @Test
    public void keccak256Test2() {
        String[] inputs = {"hello", " ", "world"};
        byte[] output = Keccak.keccak256(
            Utils.AsciiToBytes(inputs[0]),
            Utils.AsciiToBytes(inputs[1]),
            Utils.AsciiToBytes(inputs[2])
        );
        assertEquals(
            Utils.bytesToHex(output),
            "47173285a8d7341e5e972fc677286384f802f8ef42a5ec5f03bbfa254cb01fad"
        );
    }
}
