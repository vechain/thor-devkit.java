# VeChain Thor Devkit (SDK) in Java

Java (8+) library to assist a smoother development process on VeChainThor for all developers and hobbyists.

|                          Content                          |
| --------------------------------------------------------- |
| Public key, private key, address conversion.              |
| Mnemonic Wallets.                                         |
| HD Wallet.                                                |
| Keystore.                                                 |
| Various Hashing functions.                                |
| Signing messages.                                         |
| Verify signature of messages.                             |
| Bloom filter.                                             |
| Transaction Assembling (**Multi-task Transaction, MTT**). |
| Fee Delegation Transaction (**VIP-191**).                 |
| Self-signed Certificate (**VIP-192**).                    |
| ABI decoding of "functions" and "events" in logs.         |

... and will always be updated with the **newest** features on VeChain.

# Install
- Checkout the JAR file on the "release" page.
- Or build locally with `gradle build` (higher security).

# Tutorials

### Private/Public Keys
```java
import org.vechain.devkit.cry.Utils;
import org.vechain.devkit.cry.Secp256k1;

byte[] priv = Secp256k1.newPrivateKey(); // byte[32].
byte[] pub = Secp256k1.derivePublicKey(priv, false); // byte[65].
byte[] addr = Address.publicKeyToAddressBytes(pub); // byte[20].

String address = "0x" + Utils.bytesToHex(addr);
System.out.println(address);
// 0x63ad8a6d015ae579ad128e0c63040bb860cc5d34

String checksumAddress = Address.toChecksumAddress(address); // String.
System.out.println(checksumAddress);
// 0x63ad8A6D015aE579ad128e0c63040bB860Cc5D34
```

### Sign & Verify Signatures

```java
import java.util.Arrays;
import org.vechain.devkit.cry.Keccak;
import org.vechain.devkit.cry.Secp256k1;
import org.vechain.devkit.cry.Signature;
import org.vechain.devkit.cry.Utils;

byte[] priv = Utils.hexToBytes(
    "7582be841ca040aa940fff6c05773129e135623e41acce3e0b8ba520dc1ae26a"
); // byte[32].

byte[] msgHash = Keccak.keccak256(
    Utils.AsciiToBytes("hello world")
); // byte[32].

// Sign the message hash.
byte[] sigBytes = Secp256k1.sign(msgHash, priv);// byte[65].

// Recover public key from given message hash and signature.
byte[] pub = Secp256k1.recover(
    msgHash,
    new Signature(sigBytes).getECDSASignature(),
    new Signature(sigBytes).getV()
); // byte[65].

// Verify if the public key matches.
Arrays.equals(pub, Secp256k1.derivePublicKey(priv, false));// true.
```

### Mnemonic Wallet

```java
import org.vechain.devkit.cry.Mnemonic;

List<String> words = Mnemonic.generate(128);
System.out.println(words);
// [carry, slow, attack, december, number, film, scale, faith, can, old, cage, expose]

boolean flag = Mnemonic.validate(words);
System.out.println(flag); // true.

// Quickly get a Bip32 master seed for HD wallets.
// How to use the seed? See "HD wallet" below.
byte[] seed = Mnemonic.derive_seed(words);

// Quickly get a private key at index 0.
// Need to generate more? See "HD wallet" below.
byte[] priv = Mnemonic.derive_private_key(words, 0);
```

### HD Wallet

Hierarchical Deterministic Wallets. 
See [bip-32](https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki) 
and [bip-44](https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki).

```java
import java.util.List;
import com.google.common.base.Splitter;
import org.vechain.devkit.cry.Address;
import org.vechain.devkit.cry.HDNode;
import org.vechain.devkit.cry.Utils;

String sentence = "ignore empty bird silly journey junior ripple have guard waste between tenant";
List<String> words = Splitter.on(" ").splitToList(sentence);

// Construct an HD node from words. (Recommended)
HDNode topMostNode = HDNode.fromMnemonic(words);

// Or, construct from seed. (Advanced)
String seed_hex = "28bc19620b4fbb1f8892b9607f6e406fcd8226a0d6dc167ff677d122a1a64ef936101a644e6b447fd495677f68215d8522c893100d9010668614a68b3c7bb49f";

HDNode topMostNode2 = HDNode.fromSeed(
    Utils.hexToBytes(seed_hex)
);

// Access the HD node's properties.
byte[] priv = topMostNode.getPrivateKey();
byte[] pub = topMostNode.getPublicKey();
byte[] cc = topMostNode.getChainCode();

// Or, construct from a private key. (Advanced)
HDNode topMostNode3 = HDNode.fromPrivateKey(priv, cc);

// Or, construct from a public key. (Advanced)
// Notice: This HD node CANNOT derive child HD node contains "private key".
HDNode topMostNode4 = HDNode.fromPublicKey(pub, cc);

// Let it derive further child HD nodes.
for (int i = 0; i < 3; i++) {
    HDNode child = topMostNode.derive(i);
    System.out.println(
        "addr: " + Address.publicKeyToAddressString(child.getPublicKey())
    );
    System.out.println(
        "priv: " + Utils.bytesToHex(child.getPrivateKey())
    );
}
// addr: 0x339fb3c438606519e2c75bbf531fb43a0f449a70
// priv: 27196338e7d0b5e7bf1be1c0327c53a244a18ef0b102976980e341500f492425
// addr: 0x5677099d06bc72f9da1113afa5e022feec424c8e
// priv: cf44074ec3bf912d2a46b7c84fa6eb745652c9c74e674c3760dc7af07fc98b62
// addr: 0x86231b5cdcbfe751b9ddcd4bd981fc0a48afe921
// priv: 2ca054a50b53299ea3949f5362ee1d1cfe6252fbe30bea3651774790983e9348
```

### Keystore

```kotlin
import org.vechain.devkit.cry.Keystore;

// You need Java (15+) and up to use text blocks.
// Otherwise just use a StringBuilder.
String ks = """
{
    "version": 3,
    "id": "f437ebb1-5b0d-4780-ae9e-8640178ffd77",
    "address": "dc6fa3ec1f3fde763f4d59230ed303f854968d26",
    "crypto":
    {
        "kdf": "scrypt",
        "kdfparams": {
            "dklen": 32,
            "salt": "b57682e5468934be81217ad5b14ca74dab2b42c2476864592c9f3b370c09460a",
            "n": 262144,
            "r": 8,
            "p": 1
        },
        "cipher": "aes-128-ctr",
        "ciphertext": "88cb876f9c0355a89cad88ee7a17a2179700bc4306eaf78fa67320efbb4c7e31",
        "cipherparams": {
            "iv": "de5c0c09c882b3f679876b22b6c5af21"
        },
        "mac": "8426e8a1e151b28f694849cb31f64cbc9ae3e278d02716cf5b61d7ddd3f6e728""
    }
}
""";

// Must be UTF_8 string.
String password = "123456";
// Decrypt from keystore to a private key.
byte[] priv = Keystore.decrypt(ks, password);
// Encrypt from a private key to a keystore.
String ks = Keystore.encrypt(priv, password, true);
```

### Hash
```java
import org.vechain.devkit.cry.Keccak;
import org.vechain.devkit.cry.Blake2b;
import org.vechain.devkit.cry.Utils;

String input = "hello world";
String[] inputs = {"hello", " ", "world"};

byte[] output1 = Keccak.keccak256(Utils.AsciiToBytes(input));
byte[] output2 = Keccak.keccak256(
    Utils.AsciiToBytes(inputs[0]),
    Utils.AsciiToBytes(inputs[1]),
    Utils.AsciiToBytes(inputs[2])
); // output1 == outpu2

byte[] output3 = Blake2b.blake2b256(Utils.AsciiToBytes(input));
byte[] output4 = Blake2b.blake2b256(
    Utils.AsciiToBytes(inputs[0]),
    Utils.AsciiToBytes(inputs[1]),
    Utils.AsciiToBytes(inputs[2])
); // output3 == outpu4
```

### Bloom Filter

```java
import org.vechain.devkit.Bloom;
import org.vechain.devkit.cry.Utils;

// Create a bloom filter that stores 100 items.
int k = Bloom.estimateK(100);
Bloom b = new Bloom(k);

// Add to it.
b.add(Utils.UTF8ToBytes("hello world"));

// Test if exists.
b.test(Utils.UTF8ToBytes("hello world")); // true.
b.test(Utils.UTF8ToBytes("bye bye blue bird")); // false.
```

### Hex/Bytes/String Convert
```java
import org.vechain.devkit.cry.Utils;

// hex -> byte[]
assert new byte[]{15,15} == Utils.hexToBytes("0F0F") // true

// byte[] -> hex
assert Utils.bytesToHex(new byte[]{15,15}) == "0f0f"; // true

// ascii -> byte[]
assert new byte[]{49,50,51} == Utils.AsciiToBytes("123") // true
```

### ABI: Enode Function Calls.
```kotlin
import org.vechain.devkit.Function;
import org.vechain.devkit.cry.Utils;

// You need Java (15+) and up to use text blocks.
// Otherwise just use a StringBuilder.
String f1 = """
{
    "constant": false,
    "inputs": [
        {
            "name": "a1",
            "type": "uint256"
        },
        {
            "name": "a2",
            "type": "string"
        }
    ],
    "name": "f1",
    "outputs": [
        {
            "name": "r1",
            "type": "address"
        },
        {
            "name": "r2",
            "type": "bytes"
        }
    ],
    "payable": false,
    "stateMutability": "nonpayable",
    "type": "function"
}
"""; // Function definition.

Function f = new Function(f1);

// Calculate the selector of the function.
assert f.selector() == Utils.hexToBytes("27fcbb2f");

// Encode a function call with params (1, "foo").
assert f.encodeToHex(true, BigInteger.valueOf(1), "foo") == "0x27fcbb2f000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000003666f6f0000000000000000000000000000000000000000000000000000000000";
// Alternatively,
// f.encodeToBytes() -> to byte[]
// f.encode() -> to ByteBuffer
```

### ABI: Decode Function Return
```kotlin
import org.vechain.devkit.Function;
import org.vechain.devkit.cry.Utils;

// You need Java (15+) and up to use text blocks.
// Otherwise just use a StringBuilder.
String f1 = """
{
    "constant": false,
    "inputs": [
        {
            "name": "a1",
            "type": "uint256"
        },
        {
            "name": "a2",
            "type": "string"
        }
    ],
    "name": "f1",
    "outputs": [
        {
            "name": "r1",
            "type": "address"
        },
        {
            "name": "r2",
            "type": "bytes"
        }
    ],
    "payable": false,
    "stateMutability": "nonpayable",
    "type": "function"
}
"""; // Function definition.

Function f = new Function(f1);

// The function call return value.
final byte[] data = Utils.hexToBytes("000000000000000000000000abc000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000003666f6f0000000000000000000000000000000000000000000000000000000000");

// Decode above data to JSON String. (ABI v1)
String decoded = f.decodeReturnV1Json(data, true, true);
String expected = """
[
  {
    "index": 0,
    "name": "r1",
    "canonicalType": "address",
    "value": "0xabc0000000000000000000000000000000000001"
  },
  {
    "index": 1,
    "name": "r2",
    "canonicalType": "bytes",
    "value": "0x666f6f"
  }
]
""" // decoded == expected

// Alternatively, f.decodeReturnV1() -> Get raw Java types.
List<V1ParamWrapper> result = f.decodeReturnV1(data, true);
result.get(0).name;  // "r1"
result.get(0).value; // "0xabc0000000000000000000000000000000000001"
result.get(1).name;  // "r2"
result.get(1).value; // "0x666f6f"
```

### ABI: Decode Function Return (Cont.)
```kotlin
import org.vechain.devkit.Function;
import org.vechain.devkit.cry.Utils;

String f2 = """
{
    "inputs": [],
    "name": "getBigNumbers",
    "outputs": [
        {
            "internalType": "uint256",
            "name": "a",
            "type": "uint256"
        },
        {
            "internalType": "int256",
            "name": "b",
            "type": "int256"
        }
    ],
    "stateMutability": "pure",
    "type": "function"
}
""";
Function f = new Function(f2);

// The function call return value in bytes.
byte[] data = Utils.hexToBytes("000000000000000000000000000000000000000000000000000000000001e240fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe1dc0");

// For unit64 and larger number types (eg. int72, unit256, address),
// decode to BigInteger or to human-readable String.

// human=false
List<V1ParamWrapper> result = f.decodeReturnV1(data, false);
result.get(0).value; // BigInteger("123456");
result.get(1).value; // BigInteger("-123456");

// For bytes1 ~ bytes32 and bytes[],
// decode to byte[] or human-readable hex String.

// human=true
List<V1ParamWrapper> result = f.decodeReturnV1(data, true);
result.get(0).value;// "123456"
result.get(1).value;// "-123456"
```

### ABI: Decode Event Log
```kotlin
import org.vechain.devkit.Event;
import org.vechain.devkit.cry.Utils;

String e1 = """
{
    "anonymous": false,
    "inputs": [
        {
            "indexed": true,
            "name": "a1",
            "type": "uint256"
        },
        {
            "indexed": false,
            "name": "a2",
            "type": "string"
        }
    ],
    "name": "E1",
    "type": "event"
}
""";

Event e = new Event(e1);

// Calculate Signature
byte[] expected = Utils.hexToBytes("47b78f0ec63d97830ace2babb45e6271b15a678528e901a9651e45b65105e6c2");
assert e.calcEventSignature() == expected;

// Suppose we have topics coming from servers (indexed params)
List<byte[]> topics = new ArrayList<byte[]>();
topics.add(Utils.hexToBytes("47b78f0ec63d97830ace2babb45e6271b15a678528e901a9651e45b65105e6c2"));
topics.add(Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000001"));

// Decode the topics.
List<V1ParamWrapper> indexedParams = e.decodeTopics(topics, false);
indexedParams.size(); // 1
indexedParams.get(0).canonicalType; // "uint256"
indexedParams.get(0).name; // "a1"
indexedParams.get(0).value; // BigInteger("1")
// Alternatively,
// e.decodeTopicsJson() -> Decode to JSON String.

// Data (non-indexed params)
byte[] data = Utils.hexToBytes("00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000003666f6f0000000000000000000000000000000000000000000000000000000000");

// Deocde the data.
List<V1ParamWrapper> nonIndexedParams = e.decodeDataV1(data, true);
nonIndexedParams.size(); // 1
nonIndexedParams.get(0).name; // "a2"
nonIndexedParams.get(0).canonicalType; // "string"
nonIndexedParams.get(0).value; // "foo"
// Alternatively,
// e.decodeDataV1Json() -> Decode to JSON String.
```

### Transaction

```java
// Transaction Structure:
// See: https://docs.vechain.org/thor/learn/transaction-model.html#model
import org.vechain.devkit.types.Clause;
import org.vechain.devkit.Transaction;
import org.vechain.devkit.cry.Blake2b;
import org.vechain.devkit.cry.Secp256k1;
import org.vechain.devkit.cry.Utils;

// Set up clauses.
Clause[] clauses = new Clause[]{
    new Clause(
        "0x7567d83b7b8d80addcb281a71d54fc7b3364ffed",   // to
        "10000",                                        // value
        "0x000000606060"                                // data
    ),
    new Clause(
        "0x7567d83b7b8d80addcb281a71d54fc7b3364ffed",
        "20000",
        "0x000000606060"
    )
};

// Create a tx.
Transaction tx = new Transaction(
    "1",                    // chainTag
    "0x00000000aabbccdd",   // blockRef
    "32",                   // expiration
    clauses,                // clauses
    "128",                  // gasPriceCoef
    "21000",                // gas
    null,                   // dependsOn
    "12345678",             // nonce
    null                    // reserved
);

// Sign the tx.
byte[] privateKey = Utils.hexToBytes("7582be841ca040aa940fff6c05773129e135623e41acce3e0b8ba520dc1ae26a");
byte[] h = Blake2b.blake2b256(
    tx.encode() // unsigned tx encoded.
);
byte[] sig = Secp256k1.sign(h, privateKey);

// Set signature on tx.
tx.setSignature(sig);

// Properties.
tx.getId();
tx.getSignature();
tx.getIntrinsicGas(); // 37432
tx.getOriginAsAddressBytes();
tx.getOriginAsAddressString();

// Signed tx encoded.
byte[] encodedTx = tx.encode();
// Then you can HTTP POST to send the encodedTx to VeChain...
// See the REST API details:
// testnet: https://sync-testnet.vechain.org/doc/swagger-ui/
// mainnet: https://sync-mainnet.vechain.org/doc/swagger-ui/
```

### Transaction (VIP-191)
[https://github.com/vechain/VIPs/blob/master/vips/VIP-191.md](https://github.com/vechain/VIPs/blob/master/vips/VIP-191.md)
```java
import com.google.common.primitives.Bytes;
import org.vechain.devkit.types.Clause;
import org.vechain.devkit.Transaction;
import org.vechain.devkit.cry.Address;
import org.vechain.devkit.cry.Blake2b;
import org.vechain.devkit.cry.Secp256k1;
import org.vechain.devkit.cry.Utils;

// Set up clauses.
Clause[] clauses = new Clause[]{
    new Clause(
        "0x7567d83b7b8d80addcb281a71d54fc7b3364ffed",   // to
        "10000",                                        // value
        "0x000000606060"                                // data
    ),
    new Clause(
        "0x7567d83b7b8d80addcb281a71d54fc7b3364ffed",
        "20000",
        "0x000000606060"
    )
};

// Reserved: VIP-191
Reserved reserved = new Reserved(1, null);

// Create a tx.
Transaction tx = new Transaction(
    "1",                    // chainTag
    "0x00000000aabbccdd",   // blockRef
    "32",                   // expiration
    clauses,                // clauses
    "128",                  // gasPriceCoef
    "21000",                // gas
    null,                   // dependsOn
    "12345678",             // nonce
    reserved                // <--- reserved (VIP-191)
);

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
byte[] sig = Bytes.concat(senderHash, payerHash); // 130 bytes

// Set the signature onto the tx.
tx.setSignature(sig);

tx.getOriginAsAddressBytes(); // Sender: addr_1
tx.getDeleagtorAsAddressBytes(); // Gas Payer: addr_2

// Signed tx encoded.
byte[] encodedTx = tx.encode();
// Then you can HTTP POST to send the encodedTx to VeChain...
// See the REST API details:
// testnet: https://sync-testnet.vechain.org/doc/swagger-ui/
// mainnet: https://sync-mainnet.vechain.org/doc/swagger-ui/
```

### Sign & Verify Certificate (VIP-192)
[https://github.com/vechain/VIPs/blob/master/vips/VIP-192.md](https://github.com/vechain/VIPs/blob/master/vips/VIP-192.md)
```java
import org.vechain.devkit.Certificate;
import org.vechain.devkit.cry.Address;
import org.vechain.devkit.cry.Blake2b;
import org.vechain.devkit.cry.Secp256k1;
import org.vechain.devkit.cry.Utils;

/* For a Certificate looks like this:
{
  "purpose": "identification",
  "payload": {
    "type": "text",
    "content": "fyi"
  },
  "domain": "localhost",
  "timestamp": 1545035330,
  "signer": "0xd989829d88b0ed1b06edf5c50174ecfa64f14a64"
}
*/

byte[] priv = Utils.hexToBytes("7582be841ca040aa940fff6c05773129e135623e41acce3e0b8ba520dc1ae26a");
byte[] addr = Address.publicKeyToAddressBytes(Secp256k1.derivePublicKey(priv, false));

// Create a Certificate.
Map<String, String> payload = new TreeMap<String, String>();
payload.put("type", "text");
payload.put("content", "fyi");

Certificate c = new Certificate(
    "identification",               // purpose
    payload,                        // payload
    "localhost",                    // domian
    1545035330,                     // timestamp
    "0x" + Utils.bytesToHex(addr),  // signer
    null                            // signature
);

// Or create from some external json string.
Certificate c2 = Certificate.fromJsonString(...);

// Or create from some external Map<String, Object>.
Certificate c3 = Certificate.fromMap(...);

// Sign the cert.
// 1) Calculate signature.
String j = c.toJsonString();
byte[] signingHash = Blake2b.blake2b256(Utils.UTF8ToBytes(j));
byte[] sig = Secp256k1.sign(signingHash, priv);
// 2) Set signature on cert.
c.setSignature("0x" + Utils.bytesToHex(sig));
// 3) Verify. If signature matches this cert.
Certificate.verify(c);
```

## Tweak the code
```
── devkit
   ├── Bloom.java
   ├── Certificate.java
   ├── Event.java
   ├── Function.java
   ├── Transaction.java
   ├── cry
   │   ├── Address.java
   │   ├── Blake2b.java
   │   ├── HDNode.java
   │   ├── Keccak.java
   │   ├── Keystore.java
   │   ├── Mnemonic.java
   │   ├── Secp256k1.java
   │   ├── Signature.java
   │   └── Utils.java
   └── types
       ├── BlobKind.java
       ├── Clause.java
       ├── CompactFixedBlobKind.java
       ├── FixedBlobKind.java
       ├── NullableFixedBlobKind.java
       ├── NumericKind.java
       ├── Reserved.java
       ├── ScalarKind.java
       └── V1ParamWrapper.java
```
## Testing
```bash
gradle test
```

## Knowledge

|     Name     | Bytes |                  Description                   |
| ------------ | ----- | ---------------------------------------------- |
| private key  | 32    | random number                                  |
| public key   | 65    | uncompressed, starts with "04"                 |
| address      | 20    | derived from public key                        |
| keccak256    | 32    | hash                                           |
| blake2b256   | 32    | hash                                           |
| message hash | 32    | hash of a message                              |
| signature    | 65    | signing result, last bit as recovery parameter |
| seed         | 64    | used to derive bip32 master key                |
