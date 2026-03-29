package com.danubetech.btc.connection.records;

public record TxOut(
        String txId,
        String scriptPubKeyAddress,
        String asm) {
}
