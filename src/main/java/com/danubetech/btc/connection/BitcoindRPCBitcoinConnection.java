package com.danubetech.btc.connection;

import com.danubetech.btc.records.Block;
import com.danubetech.btc.records.Tx;
import com.danubetech.btc.records.TxIn;
import com.danubetech.btc.records.TxOut;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BitcoindRPCBitcoinConnection extends AbstractBitcoinConnection implements BitcoinConnection {

	private static final Logger log = LoggerFactory.getLogger(BitcoindRPCBitcoinConnection.class);

	private static final JsonMapper mapper = JsonMapper.builder()
			.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.build();

	private final BitcoinJSONRPCClient bitcoindRpcClient;

	private BitcoindRPCBitcoinConnection(BitcoinJSONRPCClient bitcoindRpcClient) {
		if (log.isDebugEnabled()) log.debug("Creating BitcoindRPCBitcoinConnection: " + bitcoindRpcClient);
		this.bitcoindRpcClient = bitcoindRpcClient;
	}

	public static BitcoindRPCBitcoinConnection create(URL rpcUrl) {
		if (log.isDebugEnabled()) log.debug("Creating BitcoindRPCBitcoinConnection: " + rpcUrl);
		return new BitcoindRPCBitcoinConnection(new BitcoinJSONRPCClient(rpcUrl));
	}

	@Override
	public Block getBlockByBlockHeight(Integer blockHeight) {
		BitcoinJSONRPCClient bitcoinJSONRPCClient = this.getBitcoinJsonRpcClient();
		wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Block bitcoinBlock = bitcoinJSONRPCClient.getBlock(blockHeight);
		List<Tx> txs = bitcoinBlock.tx().stream().map(tx -> txFromBitcoinRawTransaction(bitcoinJSONRPCClient, tx)).collect(Collectors.toList());
		Block block = new Block(bitcoinBlock.height(), bitcoinBlock.hash(), bitcoinBlock.time().getTime(), txs);
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
			wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Block bitcoinBlock = bitcoinJSONRPCClient.getBlock(blockHeight);
			if (bitcoinBlock.time().getTime() < targetTime) {
				block = new Block(bitcoinBlock.height(), bitcoinBlock.hash(), bitcoinBlock.time().getTime(), null);
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
			wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Block bitcoinBlock = bitcoinJSONRPCClient.getBlock(blockHeight);
			if (bitcoinBlock.confirmations() >= minConfirmations) {
				block = new Block(bitcoinBlock.height(), bitcoinBlock.hash(), bitcoinBlock.time().getTime(), null);
				break;
			}
		}
		if (log.isDebugEnabled()) log.debug("getBlockByMinConfirmations for {}: {}", minConfirmations, block);
		return block;
	}

	@Override
	public Map<String, Object> getMetadata() {
		return Map.of(
				"rpcURL", this.getBitcoinJsonRpcClient().rpcURL);
	}

	/*
	 * Helper methods
	 */

	private static Tx txFromBitcoinRawTransaction(BitcoinJSONRPCClient bitcoinJSONRPCClient, String txId) {
		if (Tx.COINBASE_TX_IDENTIFIER.equals(txId) || Tx.GENESIS_TX_IDENTIFIER.equals(txId)) {
			return new Tx(txId, null, null, null);
		}
		wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.RawTransaction bitcoinRawTransaction = bitcoinJSONRPCClient.getRawTransaction(txId);
		String txHex = bitcoinRawTransaction.hex();
		List<TxIn> txIns = bitcoinRawTransaction.vIn().stream().map(BitcoindRPCBitcoinConnection::txInFromBitcoinIn).toList();
		List<TxOut> txOuts = bitcoinRawTransaction.vOut().stream().map(BitcoindRPCBitcoinConnection::txOutFromBitcoinOut).toList();
		return new Tx(txId, txHex, txIns, txOuts);
	}

	private static TxIn txInFromBitcoinIn(wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.RawTransaction.In in) {
		String txId = in.txid();
		Integer vout = txId == null ? null : in.getTransactionOutput().n();
		return new TxIn(txId, vout);
	}

	private static TxOut txOutFromBitcoinOut(wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.RawTransaction.Out out) {
		String txId = out.transaction().txId();
		String scriptPubKeyAddress = out.scriptPubKey().mapStr("address");
		String asm = out.scriptPubKey().asm();
		return new TxOut(txId, scriptPubKeyAddress, asm);
	}

	/*
	 * Getters and setters
	 */

	public BitcoinJSONRPCClient getBitcoinJsonRpcClient() {
		return bitcoindRpcClient;
	}
}
