package com.danubetech.btc.records;

import java.util.List;

public record Block(
        Integer blockHeight,
        String blockHash,
        Long blockTime,
        List<Tx> txs) {
}
