package com.danubetech.btc.connection;

public record Block(
        Integer blockHeight,
        String blockHash,
        Long blockTime,
        Integer confirmations) {
}
