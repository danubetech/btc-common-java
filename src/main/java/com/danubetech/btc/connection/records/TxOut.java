package com.danubetech.btc.connection.records;

public record TxOut(
        String scriptPubKey,
        String scriptPubKeyAsm,
        String scriptPubKeyType,
        String scriptPubKeyAddress,
        Long value) {
}
