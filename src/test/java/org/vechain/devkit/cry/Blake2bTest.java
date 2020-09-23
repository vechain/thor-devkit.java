package org.vechain.devkit.cry;

import org.testng.annotations.*;
import static org.testng.Assert.*;

import java.nio.charset.Charset;

public class Blake2bTest {

    @Test
    public void blake2bTest() {
        String input = "hello world";
        byte[] output = Blake2b.blake2b(
            input.getBytes(Charset.forName("US-ASCII"))
        );
        assertEquals(
            Utils.bytesToHex(output),
            "256c83b297114d201b30179f3f0ef0cace9783622da5974326b436178aeef610"
        );
    }

    @Test
    public void blake2bTest2() {
        String[] inputs = {"hello", " ", "world"};
        byte[] output = Blake2b.blake2b(
            inputs[0].getBytes(Charset.forName("US-ASCII")),
            inputs[1].getBytes(Charset.forName("US-ASCII")),
            inputs[2].getBytes(Charset.forName("US-ASCII"))
        );
        assertEquals(
            Utils.bytesToHex(output),
            "256c83b297114d201b30179f3f0ef0cace9783622da5974326b436178aeef610"
        );
    }
}
