package org.vechain.devkit;

import org.vechain.devkit.cry.Utils;
import org.vechain.devkit.cry.Address;
import org.vechain.devkit.cry.Blake2b;
import org.vechain.devkit.cry.Secp256k1;
import org.vechain.devkit.types.Clause;
import org.vechain.devkit.types.Reserved;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import com.google.common.primitives.Bytes;

public class TransactionTest {

    final static Clause[] clauses = new Clause[]{
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

    final static Transaction transaction = new Transaction(
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

    @Test
    public void encodeUnsignedTx() {

        Transaction tx = transaction.clone();

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
    public void encodeEmptyClausesTx() {
        Transaction tx = transaction.clone();
        // empty clauses!
        tx.setClauses(new Clause[]{});

        assertEquals(
            tx.getIntrinsicGas(),
            21000
        );
    }

    @Test
    public void encodeOneClauseEmptyDataTx() {
        Clause[] clauses = new Clause[]{
            new Clause(
                null,
                "0",
                "0x" // data = 0x
            )
        };
        
        Transaction tx = transaction.clone();
        tx.setClauses(clauses);

        assertEquals(
            tx.getIntrinsicGas(),
            53000
        );

        tx.encode();
    }

    @Test
    public void signedTx() {

        Transaction tx = transaction.clone();

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

        // See: tx1.equals(tx);
        assertEquals(tx1, tx);
    }

    @Test
    public void incorrectlySigned() {
        Transaction tx = transaction.clone();
        tx.setSignature(new byte[]{1,2,3}); // wrong signature.
        assertEquals(tx.getOriginAsPublicKey(), null);
        assertEquals(tx.getId(), null);
    }

    @Test
    public void cloneTest() {
        Transaction tx = transaction.clone();
        assertEquals(tx, transaction);
    }


    @Test
    public void features() {
        Transaction tx = transaction.clone();
        Reserved reserved = new Reserved(1, Arrays.asList(new byte[][]{
            Utils.hexToBytes("1234")
        }));
        tx.setReserved(reserved);

        assertEquals(tx.isDelegated(), true);

        // Sender
        byte[] priv_1 = Utils.hexToBytes("58e444d4fe08b0f4d9d86ec42f26cf15072af3ddc29a78e33b0ceaaa292bcf6b");
        byte[] addr_1 = Address.publicKeyToAddressBytes(Secp256k1.derivePublicKey(priv_1, false));

        // Gas Payer
        byte[] priv_2 = Utils.hexToBytes("0bfd6a863f347f4ef2cf2d09c3db7b343d84bb3e6fc8c201afee62de6381dc65");
        byte[] addr_2 = Address.publicKeyToAddressBytes(Secp256k1.derivePublicKey(priv_2, false));

        // Sender sign the message himself.
        byte[] h = tx.getSigningHash(null);
        byte[] senderHash = Secp256k1.sign(h, priv_1);

        // Gas payer sign the hash for the sender.
        byte[] dh = tx.getSigningHash("0x" + Utils.bytesToHex(addr_1));
        byte[] payerHash = Secp256k1.sign(dh, priv_2);

        // Assemble signature
        byte[] sig = Bytes.concat(senderHash, payerHash);
        assertEquals(sig.length, 65 * 2);

        // Set the signature onto the tx.
        tx.setSignature(sig);

        assertEquals(
            tx.getOriginAsAddressBytes(),
            addr_1
        );

        assertEquals(
            tx.getDeleagtorAsAddressBytes(),
            addr_2
        );
    }

    @Test
    public void unused() {
        Transaction tx = transaction.clone();
        Reserved reserved = new Reserved(1, Arrays.asList(new byte[][]{
            Utils.hexToBytes("0F0F"),
            Utils.hexToBytes("0101")
        }));
        tx.setReserved(reserved);

        Transaction tx2 = Transaction.decode(tx.encode(), true);
        assertEquals(tx, tx2);

        List<byte[]> r = tx.getReserved().pack();
        assertEquals(r.get(0), Utils.hexToBytes("01"));
        assertEquals(r.get(1), Utils.hexToBytes("0F0F"));
        assertEquals(r.get(2), Utils.hexToBytes("0101"));
    }

    @Test
    public void unused2() {
        Transaction tx = transaction.clone();
        Reserved reserved = new Reserved(1, Arrays.asList(new byte[][]{
            Utils.hexToBytes("0F0F"),
            new byte[] {}
        }));
        tx.setReserved(reserved);

        Transaction tx2 = Transaction.decode(tx.encode(), true);
        assertEquals(tx, tx2);

        List<byte[]> r = tx.getReserved().pack();
        assertEquals(r.get(0), Utils.hexToBytes("01"));
        assertEquals(r.get(1), Utils.hexToBytes("0F0F"));
    }
}
