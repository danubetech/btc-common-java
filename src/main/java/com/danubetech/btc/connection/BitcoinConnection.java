package com.danubetech.btc.connection;

import com.danubetech.btc.connection.records.Block;
import com.danubetech.btc.connection.records.Tx;
import com.danubetech.btc.connection.records.TxOut;

import java.util.List;
import java.util.Map;

public interface BitcoinConnection {

    Network getNetwork();
    Map<String, Object> getMetadata();

    Block getBlockByBlockHeight(Integer blockHeight);
    Tx getTransactionById(String txid);
    Block getBlockByTargetTime(Long targetTime);
    Block getBlockByMinConfirmations(Integer confirmations);
    List<Tx> getAddressTransactions(String address);
    List<TxOut> getAddressUtxos(String address);
    Block getBlockByTransaction(Tx tx);
    void broadcastRawTransaction(byte[] rawTransaction);
}
