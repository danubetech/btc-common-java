package com.danubetech.btc.connection.records;

import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionOutput;

public record TxOut(
        String txId,
        String scriptPubKeyAddress,
        String asm) {

    public TransactionOutput toBitcoinjTransactionOut() {
        return new TransactionOutput();
    }
}
