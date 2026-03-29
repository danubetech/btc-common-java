package com.danubetech.btc.records;

public record TxOut(
        String txId,
        String scriptPubKeyAddress,
        String asm) {
}
