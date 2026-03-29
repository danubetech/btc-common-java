package com.danubetech.btc.connection;

public record TxOut(
        String txId,
        String scriptPubKeyAddress,
        String asm) {
}
