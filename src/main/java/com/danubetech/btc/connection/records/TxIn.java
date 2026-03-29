package com.danubetech.btc.connection.records;

public record TxIn(
        String txId,
        Integer vout) {
}
