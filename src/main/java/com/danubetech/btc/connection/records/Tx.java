package com.danubetech.btc.connection.records;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.List;

public record Tx(
        String txId,
        List<TxIn> txIns,
        List<TxOut> txOuts) {

    public static final String COINBASE_TX_IDENTIFIER = "0000000000000000000000000000000000000000000000000000000000000000";
    public static final String GENESIS_TX_IDENTIFIER = "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b";

    public byte[] txIdBytes() {
        try {
            return Hex.decodeHex(txId);
        } catch (DecoderException ex) {
            throw new IllegalStateException("Cannot hex-decode txId: " + txId + ": " + ex.getMessage(), ex);
        }
    }
}
