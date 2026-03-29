package com.danubetech.btc.connection;

import com.danubetech.btc.connection.records.Block;
import com.danubetech.btc.connection.records.Tx;

import java.util.List;
import java.util.Map;

public interface BitcoinConnection {

    Block getBlockByBlockHeight(Integer blockHeight);
    Tx getTransactionById(String txid);
    Block getBlockByTargetTime(Long targetTime);
    Block getBlockByMinConfirmations(Integer confirmations);
    List<Tx> getAddressTransactions(String address);
    Block getBlockByTransaction(Tx tx);
    void broadcastRawTransaction(byte[] rawTransaction);
    Map<String, Object> getMetadata();
}
