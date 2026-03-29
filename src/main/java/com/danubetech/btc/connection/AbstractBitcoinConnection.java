package com.danubetech.btc.connection;

import com.danubetech.btc.records.Block;
import com.danubetech.btc.records.Tx;

import java.util.List;

public abstract class AbstractBitcoinConnection implements BitcoinConnection {

    @Override
    public Block getBlockByBlockHeight(Integer blockHeight) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Tx getTransactionById(String txid) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Block getBlockByTargetTime(Long targetTime) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Block getBlockByMinConfirmations(Integer confirmations) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Tx> getAddressTransactions(String address) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Block getBlockByTransaction(String txid) {
        throw new RuntimeException("Not implemented");
    }
}
