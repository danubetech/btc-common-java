package com.danubetech.btc.connection.records;

public record TxIn(
        String txId,
        Integer vout) {

    public TxIn txId(String txId) {
        return new TxIn(txId, vout);
    }
}
