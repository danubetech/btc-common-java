package com.danubetech.btc.connection.impl;

import com.danubetech.btc.connection.BitcoinConnection;
import com.danubetech.btc.connection.records.Block;
import com.danubetech.btc.connection.records.Tx;

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
    public Block getBlockByTransaction(Tx tx) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void broadcastRawTransaction(byte[] rawTransaction) {
        throw new RuntimeException("Not implemented");
    }
}
