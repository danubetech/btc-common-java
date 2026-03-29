package com.danubetech.btc.connection.records;

public record Block(
        Integer blockHeight,
        String blockHash,
        Long blockTime,
        Integer confirmations) {
}
