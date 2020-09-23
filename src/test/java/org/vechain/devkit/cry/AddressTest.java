package org.vechain.devkit.cry;


import org.testng.annotations.*;
import static org.testng.Assert.*;

public class AddressTest {
    @Test
    public void remove0xTest() {
        String[] addresses = {
            "0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed",
            "0xfB6916095ca1df60bB79Ce92cE3Ea74c37c5d359",
            "0xdbF03B407c01E7cD3CBea99509d93f8DDDC8C6FB",
            "0xD1220A0cf47c7B9Be7A2E6BA89F429762e7b9aDb"
        };

        for (String item : addresses) {
            String result = Address.remove_0x(item);
            assertFalse(result.startsWith("0x"));
        }

        assertEquals(
            "D1220A0cf47c7B9Be7A2E6BA89F429762e7b9aDb", 
            "D1220A0cf47c7B9Be7A2E6BA89F429762e7b9aDb"
        );
    }
}
