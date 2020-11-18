package org.vechain.devkit;

import org.vechain.devkit.cry.Utils;
import org.vechain.devkit.cry.Address;
import org.vechain.devkit.cry.Blake2b;
import org.vechain.devkit.cry.Secp256k1;
import org.vechain.devkit.types.Clause;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class TransactionTest {

    @Test
    public void encodeUnsignedTx() {

        Clause[] clauses = new Clause[]{
            new Clause(
                "0x7567d83b7b8d80addcb281a71d54fc7b3364ffed",
                "10000",
                "0x000000606060"
            ),
            new Clause(
                "0x7567d83b7b8d80addcb281a71d54fc7b3364ffed",
                "20000",
                "0x000000606060"
            )
        };

        Transaction tx = new Transaction(
            "1",
            "0x00000000aabbccdd",
            "32",
            clauses,
            "128",
            "21000",
            null,
            "12345678",
            null
        );

        // Uncomment below line for debugging.
        // Utils.prettyPrint(tx.getUnsignedTxBody().toArray(), 0);

        assertEquals(
            tx.encode(),
            Utils.hexToBytes("f8540184aabbccdd20f840df947567d83b7b8d80addcb281a71d54fc7b3364ffed82271086000000606060df947567d83b7b8d80addcb281a71d54fc7b3364ffed824e208600000060606081808252088083bc614ec0")
        );

        assertEquals(
            tx.getSigningHash(null),
            Utils.hexToBytes("2a1c25ce0d66f45276a5f308b99bf410e2fc7d5b6ea37a49f2ab9f1da9446478")
        );

        assertEquals(tx.getId(), null);
        assertEquals(tx.getIntrinsicGas(), 37432);
        assertEquals(tx.getSignature(), null);
        assertEquals(tx.getOriginAsPublicKey(), null);
        assertEquals(tx.getOriginAsAddressBytes(), null);
        assertEquals(tx.getOriginAsAddressString(), null);
    }

    @Test
    public void encodeEmptyClauseTx() {
        Clause[] clauses = new Clause[]{};
        Transaction tx = new Transaction(
            "1",
            "0x00000000aabbccdd",
            "32",
            clauses,
            "128",
            "21000",
            null,
            "12345678",
            null
        );
        assertEquals(
            tx.getIntrinsicGas(),
            21000
        );
    }

    @Test
    public void encodeOneClauseTx() {
        Clause[] clauses = new Clause[]{
            new Clause(
                null,
                "0",
                "0x" // data = 0x
            )
        };
        Transaction tx = new Transaction(
            "1",
            "0x00000000aabbccdd",
            "32",
            clauses,
            "128",
            "21000",
            null,
            "12345678",
            null
        );
        assertEquals(
            tx.getIntrinsicGas(),
            53000
        );

        tx.encode();
    }

    @Test
    public void signedTx() {
        Clause[] clauses = new Clause[]{
            new Clause(
                "0x7567d83b7b8d80addcb281a71d54fc7b3364ffed",
                "10000",
                "0x000000606060"
            ),
            new Clause(
                "0x7567d83b7b8d80addcb281a71d54fc7b3364ffed",
                "20000",
                "0x000000606060"
            )
        };

        Transaction tx = new Transaction(
            "1",
            "0x00000000aabbccdd",
            "32",
            clauses,
            "128",
            "21000",
            null,
            "12345678",
            null
        );

        byte[] privateKey = Utils.hexToBytes("7582be841ca040aa940fff6c05773129e135623e41acce3e0b8ba520dc1ae26a");
        byte[] h = Blake2b.blake2b256(tx.encode());
        byte[] sig = Secp256k1.sign(h, privateKey);
        tx.setSignature(sig);

        byte[] publicKeyUncompressed = Secp256k1.derivePublicKey(privateKey, false);
        byte[] addressBytes = Address.publicKeyToAddressBytes(publicKeyUncompressed);

        assertEquals(
            tx.getSignature(),
            Utils.hexToBytes("f76f3c91a834165872aa9464fc55b03a13f46ea8d3b858e528fcceaf371ad6884193c3f313ff8effbb57fe4d1adc13dceb933bedbf9dbb528d2936203d5511df00")
        );

        assertEquals(
            tx.getOriginAsAddressBytes(),
            addressBytes
        );

        assertEquals(
            tx.getIdAsString(),
            "0xda90eaea52980bc4bb8d40cb2ff84d78433b3b4a6e7d50b75736c5e3e77b71ec"
        );

        assertEquals(
            tx.getSigningHash("0x" + Utils.bytesToHex(addressBytes)),
            Utils.hexToBytes("da90eaea52980bc4bb8d40cb2ff84d78433b3b4a6e7d50b75736c5e3e77b71ec")
        );

        assertEquals(
            tx.encode(),
            Utils.hexToBytes("f8970184aabbccdd20f840df947567d83b7b8d80addcb281a71d54fc7b3364ffed82271086000000606060df947567d83b7b8d80addcb281a71d54fc7b3364ffed824e208600000060606081808252088083bc614ec0b841f76f3c91a834165872aa9464fc55b03a13f46ea8d3b858e528fcceaf371ad6884193c3f313ff8effbb57fe4d1adc13dceb933bedbf9dbb528d2936203d5511df00")
        );

        byte[] encodedTx = Utils.hexToBytes("f8970184aabbccdd20f840df947567d83b7b8d80addcb281a71d54fc7b3364ffed82271086000000606060df947567d83b7b8d80addcb281a71d54fc7b3364ffed824e208600000060606081808252088083bc614ec0b841f76f3c91a834165872aa9464fc55b03a13f46ea8d3b858e528fcceaf371ad6884193c3f313ff8effbb57fe4d1adc13dceb933bedbf9dbb528d2936203d5511df00");
        Transaction tx1 = Transaction.decode(encodedTx, false);

        // also: tx1.equals(tx);
        assertEquals(tx1, tx);
    }

    @Test
    public void incorrectlySigned() {
        Clause[] clauses = new Clause[]{};
        Transaction tx = new Transaction(
            "1",
            "0x00000000aabbccdd",
            "32",
            clauses,
            "128",
            "21000",
            null,
            "12345678",
            null
        );
        tx.setSignature(new byte[]{1,2,3}); // wrong signature.
        assertEquals(tx.getOriginAsPublicKey(), null);
        assertEquals(tx.getId(), null);
    }
}
