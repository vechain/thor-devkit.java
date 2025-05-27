package org.vechain.devkit;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.esaulpaugh.headlong.rlp.RLPDecoder;
import com.esaulpaugh.headlong.rlp.RLPEncoder;
import com.esaulpaugh.headlong.rlp.RLPItem;

import org.vechain.devkit.cry.Address;
import org.vechain.devkit.cry.Blake2b;
import org.vechain.devkit.cry.Secp256k1;
import org.vechain.devkit.cry.Utils;
import org.vechain.devkit.types.Clause;
import org.vechain.devkit.types.NumericKind;
import org.vechain.devkit.types.Reserved;
import org.vechain.devkit.types.CompactFixedBlobKind;
import org.vechain.devkit.types.NullableFixedBlobKind;
import java.math.BigInteger;

/**
 * Tx = {
 *  chainTag:
 *  blockRef:
 *  expiration:
 *  clauses: [clause, clause, ...]
 *  gasPriceCoef: (optional for txType 0x51)
 *  gas:
 *  dependsOn:
 *  nonce:
 *  reserved:
 *  txType: (new for Galactica, 0x51 for EIP-1559 style transactions)
 *  maxFeePerGas: (new for Galactica when txType=0x51)
 *  maxPriorityFeePerGas: (new for Galactica when txType=0x51)
 * }
 */
public class Transaction {
    // static fields.
    private final static int DELEGATED_MASK = 1;
    
    // Transaction types
    public static final byte TX_TYPE_LEGACY = 0x00;
    public static final byte TX_TYPE_DYNAMIC_FEE = 0x51; // Galactica dynamic fee transaction type

    // member fields.
    NumericKind chainTag = new NumericKind(1);
    CompactFixedBlobKind blockRef = new CompactFixedBlobKind(8);
    NumericKind expiration = new NumericKind(4);
    Clause[] clauses;
    NumericKind gasPriceCoef = new NumericKind(1);
    NumericKind gas = new NumericKind(8);
    NullableFixedBlobKind dependsOn = new NullableFixedBlobKind(32);
    NumericKind nonce = new NumericKind(8);
    Reserved reserved;
    
    // Galactica fields
    byte txType = TX_TYPE_LEGACY; // Default to legacy transaction type
    NumericKind maxFeePerGas = null; // Only used when txType is TX_TYPE_DYNAMIC_FEE
    NumericKind maxPriorityFeePerGas = null; // Only used when txType is TX_TYPE_DYNAMIC_FEE

    // Set this member later.
    // Only signed transaction has signature.
    byte[] signature = null;

    // Don't expose.
    private Transaction(
        byte[] chainTag,
        byte[] blockRef,
        byte[] expiration,
        List<byte[]> clauses,
        byte[] gasPriceCoef,
        byte[] gas,
        byte[] dependsOn,
        byte[] nonce,
        List<byte[]> reserved,
        byte txType,
        byte[] maxFeePerGas,
        byte[] maxPriorityFeePerGas
    ){
        this.chainTag.fromBytes(chainTag);
        this.blockRef.fromBytes(blockRef);
        this.expiration.fromBytes(expiration);
        
        List<Clause> _clauses = new ArrayList<Clause>();
        for (byte[] c: clauses) {
            _clauses.add(Clause.decode(c));
        }
        Clause[] temp = new Clause[_clauses.size()];
        temp = _clauses.toArray(temp);
        this.clauses = temp;
        
        // For dynamic fee transactions, gasPriceCoef is optional
        if (gasPriceCoef != null) {
            this.gasPriceCoef.fromBytes(gasPriceCoef);
        }
        this.gas.fromBytes(gas);
        this.dependsOn.fromBytes(dependsOn);
        this.nonce.fromBytes(nonce);
        if (reserved != null){
            if (reserved.size() > 0) {
                this.reserved = Reserved.unpack(reserved);
            } else {
                this.reserved = Reserved.getNullReserved();
            }
        } else {
            this.reserved = Reserved.getNullReserved();
        }
        
        // Set Galactica fields
        this.txType = txType;
        if (txType == TX_TYPE_DYNAMIC_FEE) {
            if (maxFeePerGas != null) {
                this.maxFeePerGas = new NumericKind(32); // 256-bit value
                this.maxFeePerGas.fromBytes(maxFeePerGas);
            }
            if (maxPriorityFeePerGas != null) {
                this.maxPriorityFeePerGas = new NumericKind(32); // 256-bit value
                this.maxPriorityFeePerGas.fromBytes(maxPriorityFeePerGas);
            }
        }
        
    }

    /**
     * Construct a legacy Transaction.
     * @param chainTag eg. "1"
     * @param blockRef eg. "0x00000000aabbccdd"
     * @param expiration eg. "32"
     * @param clauses See Clause.java
     * @param gasPriceCoef eg. "128"
     * @param gas eg. "21000"
     * @param dependsOn eg. "0x..." as block ID, or null if not wish to depends on.
     * @param nonce eg. "12345678", as a random positive number max width is 8 bytes.
     * @param reserved See Reserved.java
     */
    public Transaction(
        String chainTag,
        String blockRef,
        String expiration,
        Clause[] clauses, // don't be null.
        String gasPriceCoef,
        String gas,
        String dependsOn, // can be null
        String nonce,
        Reserved reserved // can be null
    ){
        this.chainTag.setValue(chainTag);
        this.blockRef.setValue(blockRef);
        this.expiration.setValue(expiration);

        if (clauses == null) {
            throw new IllegalArgumentException("Fill in the clauses, please.");
        } else {
            this.clauses = clauses;
        }

        this.gasPriceCoef.setValue(gasPriceCoef);
        this.gas.setValue(gas);
        this.dependsOn.setValue(dependsOn);
        this.nonce.setValue(nonce);
        if (reserved == null) {
            this.reserved = Reserved.getNullReserved();
        } else {
            this.reserved = reserved;
        }
        
        // Set as legacy transaction type
        this.txType = TX_TYPE_LEGACY;
    }

    public byte[] getSignature() {
        return this.signature;
    }

    public void setSignature(byte[] data) {
        this.signature = data;
    }

    // non-visible to users. For debug only.
    void setClauses(Clause[] clauses) {
        this.clauses = clauses;
    }

    // non-visible to users. For debug only.
    void setReserved(Reserved reserved) {
        if (reserved == null) {
            this.reserved = Reserved.getNullReserved();
        } else {
            this.reserved = reserved;
        }
    }

    // non-visible to users. For debug only.
    Reserved getReserved() {
        return this.reserved;
    }

    /**
     * Calculate the gas used by the data section.
     * 
     * @param data Thre pure bytes of the data.
     * @return
     */
    static int calcDataGas(byte[] data) {
        final int Z_GAS = 4;
        final int NZ_GAS = 68;

        int sum = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == (byte) 0) {
                sum += Z_GAS;
            } else {
                sum += NZ_GAS;
            }
        }
        return sum;
    }

    /**
     * Calculate the gas used by the data section.
     * 
     * @param hexString "0x..." the data.
     * @return
     */
    static int calcDataGas(String hexString) {
        String realHex = Utils.remove0x(hexString);
        byte[] realData = Utils.hexToBytes(realHex);
        return calcDataGas(realData);
    }

    /**
     * Calculate roughly the gas from a list of clauses.
     * 
     * @param clauses A list of clauses.
     * @return
     */
    static int calcIntrinsicGas(Clause[] clauses) {
        final int TX_GAS = 5000;
        final int CLAUSE_GAS = 16000;
        final int CLAUSE_CONTRACT_CREATION = 48000;

        // Hey bro fill in something.
        if (clauses == null) {
            throw new IllegalArgumentException("Input is null");
        }

        // Must pay a static fee even empty!
        if (clauses.length == 0) {
            return TX_GAS + CLAUSE_GAS;
        }

        int sum = 0;
        sum += TX_GAS;

        for (Clause c: clauses) {
            int clauseSum = 0;

            if (c.to.toBytes().length == 0) { // contract creation
                clauseSum += CLAUSE_CONTRACT_CREATION;
            } else { // or a normal clause
                clauseSum += CLAUSE_GAS;
            }

            clauseSum += calcDataGas(c.data.toBytes());
            sum += clauseSum;
        }

        return sum;
    }

    /**
     * Get the rough gas this tx will consume.
     * @return
     */
    public int getIntrinsicGas() {
        return calcIntrinsicGas(this.clauses);
    }

    /**
     * Check if is a delegated transaction (vip-191)
     * @return
     */
    public boolean isDelegated() {
        if (this.reserved == null) {
            return false;
        }
        if (this.reserved.getFeatures() == 0) {
            return false;
        }

        ByteBuffer delegatedMask = ByteBuffer.allocate(4);
        delegatedMask.putInt(DELEGATED_MASK);

        ByteBuffer features = ByteBuffer.allocate(4);
        features.putInt(this.reserved.getFeatures());

        byte[] maskedArray = new byte[4];

        for (int i = 0 ; i < 4; i++) {
            maskedArray[i] = (byte) ( (byte) delegatedMask.get(i) & (byte) features.get(i) );
        }

        if (Arrays.equals(maskedArray, delegatedMask.array())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if the signature is valid.
     * @return
     */
    private boolean isSignatureValid(){
        int expectedSignatureLength;
        if (this.isDelegated()){
            expectedSignatureLength = 65 * 2;
        } else {
            expectedSignatureLength = 65;
        }

        if (this.getSignature() == null) {
            return false;
        } else {
            return (this.getSignature().length == expectedSignatureLength);
        }
    }

    /**
     * Pack the objects bytes in a fixed order.
     * @return
     */
    List<Object> packUnsignedTxBody() {
        // Prepare reserved.
        List<byte[]> _reserved = this.reserved.pack();
        // Prepare clauses.
        List<Object[]> _clauses = new ArrayList<Object[]>();
        for (Clause c: this.clauses) {
            _clauses.add(c.pack());
        }
        
        // Build transaction body based on transaction type
        List<Object> bodyElements = new ArrayList<>();
        bodyElements.add(this.chainTag.toBytes());
        bodyElements.add(this.blockRef.toBytes());
        bodyElements.add(this.expiration.toBytes());
        bodyElements.add(_clauses);
        
        if (this.txType == TX_TYPE_LEGACY || this.gasPriceCoef != null) {
            bodyElements.add(this.gasPriceCoef.toBytes());
        }
        
        bodyElements.add(this.gas.toBytes());
        bodyElements.add(this.dependsOn.toBytes());
        bodyElements.add(this.nonce.toBytes());
        bodyElements.add(_reserved);
        
        // Add dynamic fee transaction fields if applicable
        if (this.txType == TX_TYPE_DYNAMIC_FEE) {
            if (this.maxFeePerGas != null) {
                bodyElements.add(this.maxFeePerGas.toBytes());
            }
            if (this.maxPriorityFeePerGas != null) {
                bodyElements.add(this.maxPriorityFeePerGas.toBytes());
            }
        }
        
        // Prepare unsigned tx.
        Object[] unsignedBody = bodyElements.toArray();

        return new ArrayList<Object>(Arrays.asList(unsignedBody));
    }

    /**
     * Compute the hash result to be signed.
     * @param delegateFor "0x..." the address to delegate for him or null.
     * @return
     */
    public byte[] getSigningHash(String delegateFor) {
        // Get a unsigned Tx body as an array.
        Object[] unsignedTxBody = this.packUnsignedTxBody().toArray();
        // RLP encode them to bytes.
        byte[] buff = RLPEncoder.encodeAsList(unsignedTxBody);
        // Hash it.
        byte[] h = Blake2b.blake2b256(buff);

        if (delegateFor != null) {
            if (!Address.isAddress(delegateFor)) {
                throw new IllegalArgumentException("delegateFor should be address type.");
            }
            return Blake2b.blake2b256(h, Utils.hexToBytes(delegateFor.substring(2)));
        } else {
            return h;
        }
    }

    /**
     * Get "origin" of the tx by public key bytes style.
     * @return If can't decode just return null.
     */
    public byte[] getOriginAsPublicKey(){
        if (!this.isSignatureValid()) {
            return null;
        }

        try {
            byte[] h = this.getSigningHash(null);
            byte[] sig = Arrays.copyOfRange(this.getSignature(), 0, 65);
            byte[] pubKey = Secp256k1.recover(h, sig);
            return pubKey;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get "origin" of the tx by string Address style.
     * Notice: Address != public key.
     * @return
     */
    public String getOriginAsAddressString(){
        byte[] pubKey = this.getOriginAsPublicKey();
        return pubKey == null ? null : Address.publicKeyToAddressString(pubKey);
    }

    /**
     * Get "origin" of the tx by bytes Address style.
     * Notice: Address != public key.
     * @return
     */
    public byte[] getOriginAsAddressBytes(){
        byte[] pubKey = this.getOriginAsPublicKey();
        return pubKey == null ? null : Address.publicKeyToAddressBytes(pubKey);
    }

    /**
     * Get the delegator public key as bytes.
     * @return or null.
     */
    public byte[] getDelegator() {
        if (!this.isDelegated()) {
            return null;
        }

        if (!this.isSignatureValid()) {
            return null;
        }

        String origin = this.getOriginAsAddressString();
        if (origin == null) {
            return null;
        }

        try {
            byte[] h = this.getSigningHash(origin);
            byte[] sig = Arrays.copyOfRange(this.getSignature(), 65, this.getSignature().length);
            return Secp256k1.recover(h,sig);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the delegator as Address type, in bytes.
     * @return or null.
     */
    public byte[] getDeleagtorAsAddressBytes() {
        byte[] pubKey = this.getDelegator();
        return pubKey == null ? null : Address.publicKeyToAddressBytes(pubKey);
    }

    /**
     * Get the delegator as Address type, in string.
     * @return or null.
     */
    public String getDelegatorAsAddressString() {
        byte[] pubKey = this.getDelegator();
        return pubKey == null ? null : Address.publicKeyToAddressString(pubKey);
    }

    /**
     * Calculate Tx id (32 bytes).
     * @return or null.
     */
    public byte[] getId() {
        if (!this.isSignatureValid()) {
            return null;
        }
        try {
            byte[] h = this.getSigningHash(null);
            byte[] sig = Arrays.copyOfRange(this.getSignature(), 0, 65);
            byte[] pubKey = Secp256k1.recover(h, sig);
            byte[] addressBytes = Address.publicKeyToAddressBytes(pubKey);
            return Blake2b.blake2b256(h, addressBytes);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get TX id as "0x..." = 2 chars 0x + 64 chars hex
     * @return or null.
     */
    public String getIdAsString() {
        byte[] b = this.getId();
        return b == null ? null : "0x" + Utils.bytesToHex(b);
    }

    /**
     * Construct a dynamic fee Transaction (EIP-1559 style).
     * @param chainTag eg. "1"
     * @param blockRef eg. "0x00000000aabbccdd"
     * @param expiration eg. "32"
     * @param clauses See Clause.java
     * @param gas eg. "21000"
     * @param dependsOn eg. "0x..." as block ID, or null if not wish to depends on.
     * @param nonce eg. "12345678", as a random positive number max width is 8 bytes.
     * @param maxFeePerGas The maximum fee per gas the sender is willing to pay
     * @param maxPriorityFeePerGas The maximum priority fee per gas
     * @param reserved See Reserved.java
     */
    public Transaction(
        String chainTag,
        String blockRef,
        String expiration,
        Clause[] clauses, // don't be null.
        String gas,
        String dependsOn, // can be null
        String nonce,
        String maxFeePerGas,
        String maxPriorityFeePerGas,
        Reserved reserved // can be null
    ){
        this.chainTag.setValue(chainTag);
        this.blockRef.setValue(blockRef);
        this.expiration.setValue(expiration);

        if (clauses == null) {
            throw new IllegalArgumentException("Fill in the clauses, please.");
        } else {
            this.clauses = clauses;
        }

        // gasPriceCoef is not used in dynamic fee transactions
        this.gas.setValue(gas);
        this.dependsOn.setValue(dependsOn);
        this.nonce.setValue(nonce);
        if (reserved == null) {
            this.reserved = Reserved.getNullReserved();
        } else {
            this.reserved = reserved;
        }
        
        // Set as dynamic fee transaction
        this.txType = TX_TYPE_DYNAMIC_FEE;
        
        // Set dynamic fee parameters
        this.maxFeePerGas = new NumericKind(32);
        this.maxFeePerGas.setValue(maxFeePerGas);
        
        this.maxPriorityFeePerGas = new NumericKind(32);
        this.maxPriorityFeePerGas.setValue(maxPriorityFeePerGas);
    }
    
    /**
     * Get the transaction type.
     * @return TX_TYPE_LEGACY (0x00) or TX_TYPE_DYNAMIC_FEE (0x51)
     */
    public byte getTxType() {
        return this.txType;
    }
    
    /**
     * Get the maximum fee per gas for dynamic fee transactions.
     * @return maxFeePerGas value as a string, or null if not set
     */
    public String getMaxFeePerGas() {
        return this.maxFeePerGas != null ? this.maxFeePerGas.toString() : null;
    }
    
    /**
     * Get the maximum priority fee per gas for dynamic fee transactions.
     * @return maxPriorityFeePerGas value as a string, or null if not set
     */
    public String getMaxPriorityFeePerGas() {
        return this.maxPriorityFeePerGas != null ? this.maxPriorityFeePerGas.toString() : null;
    }
    
    /**
     * Encode a tx into bytes.
     * @return
     */
    public byte[] encode() {
        List<Object> unsignedTxBody = this.packUnsignedTxBody();
        unsignedTxBody.add(this.getSignature());

        byte[] rlpTx = RLPEncoder.encodeAsList(unsignedTxBody.toArray());
        
        // For dynamic fee transactions, prepend the transaction type
        if (this.txType == TX_TYPE_DYNAMIC_FEE) {
            byte[] result = new byte[rlpTx.length + 1];
            result[0] = TX_TYPE_DYNAMIC_FEE;
            System.arraycopy(rlpTx, 0, result, 1, rlpTx.length);
            return result;
        }
        
        return rlpTx;
    }

    /**
     * Decode a tx from byte[] data.
     * 1) Tx can be signed,
     * 2) Tx can be unsigned.
     * 3) Tx can be legacy (0x00) or dynamic fee (0x51)
     * @param data
     * @param unsigned
     * @return
     */
    public static Transaction decode(byte[] data, boolean unsigned) {
        if (data[0] == TX_TYPE_DYNAMIC_FEE) {
            // Remove the transaction type byte
            byte[] txData = new byte[data.length - 1];
            System.arraycopy(data, 1, txData, 0, data.length - 1);
            data = txData;
        }

        Iterator<RLPItem> l = RLPDecoder.RLP_STRICT.listIterator(data);

        byte[] _chainTag = l.next().asBytes();
        byte[] _blockRef = l.next().asBytes();
        byte[] _expiration = l.next().asBytes();

        byte[] _clausesSection = l.next().asBytes();
        Iterator<RLPItem> clausesIterator = RLPDecoder.RLP_STRICT.sequenceIterator(_clausesSection);
        List<byte[]> _clauses = new ArrayList<byte[]>();
        while(clausesIterator.hasNext()){
            _clauses.add(clausesIterator.next().asBytes());
        }

        byte[] _gasPriceCoef = l.next().asBytes();
        byte[] _gas = l.next().asBytes();
        byte[] _dependsOn = l.next().asBytes();
        byte[] _nonce = l.next().asBytes();

        byte[] _reservedSection = l.next().asBytes();
        Iterator<RLPItem> reservedIterator = RLPDecoder.RLP_STRICT.sequenceIterator(_reservedSection);
        List<byte[]> _reserved = new ArrayList<byte[]>();
        while(reservedIterator.hasNext()) {
            _reserved.add(reservedIterator.next().asBytes());
        }

        byte txType = TX_TYPE_LEGACY;
        byte[] _maxFeePerGas = null;
        byte[] _maxPriorityFeePerGas = null;

        if (data.length > l.getOffset()) {
            // Check if there are additional fields for dynamic fee transactions
            if (l.hasNext()) {
                _maxFeePerGas = l.next().asBytes();
            }
            if (l.hasNext()) {
                _maxPriorityFeePerGas = l.next().asBytes();
            }
            txType = TX_TYPE_DYNAMIC_FEE;
        }

        Transaction tx = new Transaction(
            _chainTag,
            _blockRef,
            _expiration,
            _clauses,
            _gasPriceCoef,
            _gas,
            _dependsOn,
            _nonce,
            _reserved,
            txType,
            _maxFeePerGas,
            _maxPriorityFeePerGas
        );

        if (!unsigned) {
            byte[] sig = l.next().asBytes();
            tx.setSignature(sig);
        }

        return tx;
    }

    @Override
    public boolean equals(Object other) {

        if (other == this) { 
            return true; 
        } 

        if (!(other instanceof Transaction)) { 
            return false; 
        }

        Transaction that = (Transaction) other;

        boolean flag1 = Arrays.equals(this.getSignature(), that.getSignature());
        boolean flag2 = Arrays.equals(this.encode(), that.encode());

        return flag1 && flag2;
    }

    /**
     * Clone current tx into a standalone object.
     * @return
     */
    public Transaction clone() {
        boolean unsigned = this.getSignature() == null;
        return Transaction.decode(this.encode(), unsigned);
    }
}
