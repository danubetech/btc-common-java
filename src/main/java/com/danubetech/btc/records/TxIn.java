package com.danubetech.btc.records;

public record TxIn(
        String txId,
        Integer vout) {
}
