package com.danubetech.btc.connection.impl;

import com.danubetech.btc.connection.BitcoinConnection;
import com.danubetech.btc.connection.Network;
import com.danubetech.btc.connection.records.Block;
import com.danubetech.btc.connection.records.Tx;
import com.danubetech.btc.connection.records.TxIn;
import com.danubetech.btc.connection.records.TxOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class BitcoindRPCBitcoinConnection extends AbstractBitcoinConnection implements BitcoinConnection {

	private static final Logger log = LoggerFactory.getLogger(BitcoindRPCBitcoinConnection.class);

	private final BitcoinJSONRPCClient bitcoindRpcClient;

	private BitcoindRPCBitcoinConnection(Network network, BitcoinJSONRPCClient bitcoindRpcClient) {
		super(network);
		if (log.isDebugEnabled()) log.debug("Creating BitcoindRPCBitcoinConnection: " + bitcoindRpcClient);
		this.bitcoindRpcClient = bitcoindRpcClient;
	}

	public static BitcoindRPCBitcoinConnection create(Network network, URL rpcUrl) {
		if (log.isDebugEnabled()) log.debug("Creating BitcoindRPCBitcoinConnection: " + rpcUrl);
		return new BitcoindRPCBitcoinConnection(network, new BitcoinJSONRPCClient(rpcUrl));
	}

	@Override
	public Map<String, Object> getMetadata() {
		return Map.of(
				"rpcURL", this.getBitcoinJsonRpcClient().rpcURL);
	}

	@Override
	public Block getBlockByBlockHeight(Integer blockHeight) {
		BitcoinJSONRPCClient bitcoinJSONRPCClient = this.getBitcoinJsonRpcClient();
		wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Block bitcoinjBlock = bitcoinJSONRPCClient.getBlock(blockHeight);
		List<Tx> txs = bitcoinjBlock.tx().stream().map(tx -> txFromBitcoinRawTransaction(bitcoinJSONRPCClient, tx)).toList();
		Block block = blockFromBitcoinBlock(bitcoinjBlock);
		if (log.isDebugEnabled()) log.debug("getBlockByBlockHeight for {}: {}", blockHeight, block);
		return block;
	}

	@Override
	public Tx getTransactionById(String txId) {
		BitcoinJSONRPCClient bitcoinJSONRPCClient = this.getBitcoinJsonRpcClient();
		Tx tx = txFromBitcoinRawTransaction(bitcoinJSONRPCClient, txId);
		if (log.isDebugEnabled()) log.debug("getTransactionById for {}: {}", txId, tx);
		return tx;
	}

	@Override
	public Block getBlockByTargetTime(Long targetTime) {
		BitcoinJSONRPCClient bitcoinJSONRPCClient = this.getBitcoinJsonRpcClient();
		Integer blocks = bitcoinJSONRPCClient.getBlockChainInfo().blocks();
		Block block = null;
		for (int blockHeight=blocks-1; blockHeight>=0; blockHeight--) {
			wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Block bitcoinjBlock = bitcoinJSONRPCClient.getBlock(blockHeight);
			if (bitcoinjBlock.time().getTime() < targetTime) {
				block = blockFromBitcoinBlock(bitcoinjBlock);
				break;
			}
		}
		if (log.isDebugEnabled()) log.debug("getBlockByTargetTime for {}: {}", targetTime, block);
		return block;
	}

	@Override
	public Block getBlockByMinConfirmations(Integer minConfirmations) {
		BitcoinJSONRPCClient bitcoinJSONRPCClient = this.getBitcoinJsonRpcClient();
		Integer blocks = bitcoinJSONRPCClient.getBlockChainInfo().blocks();
		Block block = null;
		for (int blockHeight=blocks-1; blockHeight>=0; blockHeight--) {
			wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Block bitcoinjBlock = bitcoinJSONRPCClient.getBlock(blockHeight);
			if (bitcoinjBlock.confirmations() >= minConfirmations) {
				block = blockFromBitcoinBlock(bitcoinjBlock);
				break;
			}
		}
		if (log.isDebugEnabled()) log.debug("getBlockByMinConfirmations for {}: {}", minConfirmations, block);
		return block;
	}

	/*
	 * Helper methods
	 */

	private static Block blockFromBitcoinBlock(wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Block bitcoinjBlock) {
		return new Block(bitcoinjBlock.height(), bitcoinjBlock.hash(), bitcoinjBlock.time().getTime(), bitcoinjBlock.confirmations());
	}

	private static Tx txFromBitcoinRawTransaction(BitcoinJSONRPCClient bitcoinJSONRPCClient, String txId) {
		if (Tx.COINBASE_TX_IDENTIFIER.equals(txId) || Tx.GENESIS_TX_IDENTIFIER.equals(txId)) {
			return new Tx(txId, null, null);
		}
		wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.RawTransaction bitcoinRawTransaction = bitcoinJSONRPCClient.getRawTransaction(txId);
		List<TxIn> txIns = bitcoinRawTransaction.vIn().stream().map(BitcoindRPCBitcoinConnection::txInFromBitcoinIn).toList();
		List<TxOut> txOuts = bitcoinRawTransaction.vOut().stream().map(BitcoindRPCBitcoinConnection::txOutFromBitcoinOut).toList();
		return new Tx(txId, txIns, txOuts);
	}

	private static TxIn txInFromBitcoinIn(wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.RawTransaction.In in) {
        var txId = in.txid();
		Integer vout = txId == null ? null : in.getTransactionOutput().n();
		return new TxIn(txId, vout);
	}

	private static TxOut txOutFromBitcoinOut(wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.RawTransaction.Out out) {
		String scriptPubKey = out.scriptPubKey().hex();
		String scriptPubKeyAsm = out.scriptPubKey().asm();
		String scriptPubKeyType = out.scriptPubKey().type();
		String scriptPubKeyAddress = out.scriptPubKey().mapStr("address");
		Long value = out.value().longValue();
		return new TxOut(scriptPubKey, scriptPubKeyAsm, scriptPubKeyType, scriptPubKeyAddress, value);
	}

	/*
	 * Getters and setters
	 */

	public BitcoinJSONRPCClient getBitcoinJsonRpcClient() {
		return bitcoindRpcClient;
	}
}
