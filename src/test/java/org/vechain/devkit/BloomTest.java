package org.vechain.devkit;


import org.vechain.devkit.cry.Utils;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class BloomTest {
    @Test
    public void estimateTest() {
        assertEquals(Bloom.estimateK(1), 16);
        assertEquals(Bloom.estimateK(100), 14);
        assertEquals(Bloom.estimateK(200), 7);
        assertEquals(Bloom.estimateK(300), 5);
        assertEquals(Bloom.estimateK(400), 4);
        assertEquals(Bloom.estimateK(500), 3);
    }

    @Test
    public void addTest() {
        Bloom b = new Bloom(14);
        b.add(Utils.UTF8ToBytes("hello world"));
        assertEquals(Utils.bytesToHex(b.getStorage()), "00000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000004000000000000000000040000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000100000000000000001000000000000020000000000000000000000000008000000000000000000000000000000080000000100000000000000000000040020000000000080000000000000000000080000000000000000000000000");
    }

    @Test
    public void testTest() {
        Bloom b = new Bloom(14);
        for(int i = 0; i < 100; i++) {
            b.add(Utils.AsciiToBytes(String.valueOf(i)));
        }

        for(int i = 0; i < 100; i++) {
            b.test(Utils.AsciiToBytes(String.valueOf(i)));
        }
    }
}
