package org.vechain.devkit.cry;

import org.testng.annotations.*;
import static org.testng.Assert.*;

import com.google.common.base.Charsets;

public class KeccakTest {

    @Test
    public void keccak256Test() {
        String input = "hello world";
        byte[] output = Keccak.keccak256(
            input.getBytes(Charsets.US_ASCII)
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
            inputs[0].getBytes(Charsets.US_ASCII),
            inputs[1].getBytes(Charsets.US_ASCII),
            inputs[2].getBytes(Charsets.US_ASCII)
        );
        assertEquals(
            Utils.bytesToHex(output),
            "47173285a8d7341e5e972fc677286384f802f8ef42a5ec5f03bbfa254cb01fad"
        );
    }
}
