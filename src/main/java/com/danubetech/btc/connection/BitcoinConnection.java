package com.danubetech.btc.connection;

import com.danubetech.btc.records.Block;
import com.danubetech.btc.records.Tx;

import java.util.List;
import java.util.Map;

public interface BitcoinConnection {

    Block getBlockByBlockHeight(Integer blockHeight);
    Tx getTransactionById(String txid);
    Block getBlockByTargetTime(Long targetTime);
    Block getBlockByMinConfirmations(Integer confirmations);
    List<Tx> getAddressTransactions(String address);
    Block getBlockByTransaction(String txid);
    Map<String, Object> getMetadata();
}
