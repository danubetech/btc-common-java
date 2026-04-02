package com.danubetech.btc.connection.records;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public record TxIn(
        String txId,
        Integer txInIndex,
        String prevTxId,
        Integer prevTxOutIndex) {

    public TxIn txId(String txId) {
        return new TxIn(txId, txInIndex, prevTxId, prevTxOutIndex);
    }

    public TxIn txInIndex(Integer txInIndex) {
        return new TxIn(txId, txInIndex, prevTxId, prevTxOutIndex);
    }

    public byte[] txIdBytes() {
        try {
            return Hex.decodeHex(txId);
        } catch (DecoderException ex) {
            throw new IllegalStateException("Cannot hex-decode txId: " + txId + ": " + ex.getMessage(), ex);
        }
    }
}
