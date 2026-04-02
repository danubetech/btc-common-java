package com.danubetech.btc.connection.records;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public record TxOut(
        String txId,
        Integer txOutIndex,
        String scriptPubKey,
        String scriptPubKeyAsm,
        String scriptPubKeyType,
        String scriptPubKeyAddress,
        Long value) {

    public TxOut txId(String txId) {
        return new TxOut(txId, txOutIndex, scriptPubKey, scriptPubKeyAsm, scriptPubKeyType, scriptPubKeyAddress, value);
    }

    public TxOut txOutIndex(Integer txOutIndex) {
        return new TxOut(txId, txOutIndex, scriptPubKey, scriptPubKeyAsm, scriptPubKeyType, scriptPubKeyAddress, value);
    }

    public byte[] txIdBytes() {
        try {
            return Hex.decodeHex(txId);
        } catch (DecoderException ex) {
            throw new IllegalStateException("Cannot hex-decode txId: " + txId + ": " + ex.getMessage(), ex);
        }
    }

    public byte[] scriptBytes() {
        try {
            return Hex.decodeHex(scriptPubKey);
        } catch (DecoderException ex) {
            throw new IllegalStateException("Cannot hex-decode scriptPubKey: " + scriptPubKey + ": " + ex.getMessage(), ex);
        }
    }
}
