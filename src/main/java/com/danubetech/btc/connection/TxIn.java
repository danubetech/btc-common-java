package com.danubetech.btc.connection;

public record TxIn(
        String txId,
        Integer vout) {
}
