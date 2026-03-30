package com.danubetech.btc.connection.records;

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
}
