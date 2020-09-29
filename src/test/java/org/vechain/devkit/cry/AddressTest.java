package org.vechain.devkit.cry;

import org.testng.annotations.*;
import static org.testng.Assert.*;

public class AddressTest {
    @Test
    public void checksumAddressTest() {
        String[] addresses = {
            "0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed",
            "0xfB6916095ca1df60bB79Ce92cE3Ea74c37c5d359",
            "0xdbF03B407c01E7cD3CBea99509d93f8DDDC8C6FB",
            "0xD1220A0cf47c7B9Be7A2E6BA89F429762e7b9aDb"
        };

        for (String item : addresses) {
            assertTrue(Address.isAddress(item));
            assertEquals(Address.toChecksumAddress(item), item);
        }
    }

    @Test
    public void publicKeyToAddress() {
        byte[] pub = Utils.hexToBytes("04b90e9bb2617387eba4502c730de65a33878ef384a46f1096d86f2da19043304afa67d0ad09cf2bea0c6f2d1767a9e62a7a7ecc41facf18f2fa505d92243a658f");
        byte[] address = Address.publicKeyToAddressBytes(pub);
        assertEquals(address, Utils.hexToBytes("d989829d88b0ed1b06edf5c50174ecfa64f14a64"));
        String address2 = Address.publicKeyToAddressString(pub);
        assertEquals(address2, "0xd989829d88b0ed1b06edf5c50174ecfa64f14a64");
    }

    @Test
    public void publicKeyToLower() {
        String a = "0x6e8b475c7786A1023a28C45dB93B74BbD7faF354";
        assertEquals(Address.toLowerCaseAddress(a), "0x6e8b475c7786a1023a28c45db93b74bbd7faf354");
    }
}
