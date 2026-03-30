package com.danubetech.btc.connection.records;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public record TxOut(
        String txId,
        String scriptPubKey,
        String scriptPubKeyAsm,
        String scriptPubKeyType,
        String scriptPubKeyAddress,
        Long value) {

    public TxOut txId(String txId) {
        return new TxOut(txId, scriptPubKey, scriptPubKeyAsm, scriptPubKeyType, scriptPubKeyAddress, value);
    }

    public byte[] scriptBytes() {
        try {
            return Hex.decodeHex(scriptPubKey);
        } catch (DecoderException ex) {
            throw new IllegalStateException("Cannot hex-decode script: " + scriptPubKey + ": " + ex.getMessage(), ex);
        }
    }
}
