package com.danubetech.btc.connection.records;

public record Block(
        Integer blockHeight,
        Long blockTime,
        String blockHash,
        Integer confirmations) {
}
