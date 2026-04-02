package com.danubetech.btc.connection.records;

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
}
