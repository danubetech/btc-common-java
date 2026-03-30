package com.danubetech.btc.connection.impl;

import com.danubetech.btc.connection.BitcoinConnection;
import com.danubetech.btc.connection.Network;
import com.danubetech.btc.connection.records.Block;
import com.danubetech.btc.connection.records.Tx;
import com.danubetech.btc.connection.records.TxIn;
import com.danubetech.btc.connection.records.TxOut;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EsploraElectrsRESTBitcoinConnection extends AbstractBitcoinConnection implements BitcoinConnection {

	private static final Logger log = LoggerFactory.getLogger(EsploraElectrsRESTBitcoinConnection.class);

	private static final JsonMapper jsonMapper = JsonMapper.builder()
			.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.build();

	private final URI apiEndpointBase;

	private EsploraElectrsRESTBitcoinConnection(Network network, URI apiEndpointBase) {
		super(network);
		if (log.isDebugEnabled()) log.debug("Creating EsploraElectrsRESTBitcoinConnection: " + apiEndpointBase);
		this.apiEndpointBase = apiEndpointBase;
	}

	public static EsploraElectrsRESTBitcoinConnection create(Network network, URI apiEndpointBase) {
		if (log.isDebugEnabled()) log.debug("Creating EsploraElectrsRESTBitcoinConnection: " + apiEndpointBase);
		return new EsploraElectrsRESTBitcoinConnection(network, apiEndpointBase);
	}

	@Override
	public Map<String, Object> getMetadata() {
		return Map.of(
				"apiEndpointBase", "" + this.getApiEndpointBase());
	}

	@Override
	public Block getBlockByBlockHeight(Integer blockHeight) {
		URI apiEndpoint1 = URI.create(this.apiEndpointBase + "block-height/" + blockHeight);
		Map<String, Object> response1 = readObject(apiEndpoint1);
		URI apiEndpoint2 = URI.create(this.apiEndpointBase + "block/" + response1.get("height") + "/txs");
		List<Map<String, Object>> response2 = readArray(apiEndpoint2);
		Integer responseBlockHeight = ((Number) response1.get("height")).intValue();
		String responseHash = (String) response1.get("id");
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Tx getTransactionById(String txid) {
		URI apiEndpoint = URI.create(this.apiEndpointBase + "tx/" + txid);
		Map<String, Object> response = readObject(apiEndpoint);
		Tx tx = EsploraElectrsRESTBitcoinConnection.txFromMap(response);
		if (log.isDebugEnabled()) log.debug("getTransactionById for {}: {}", txid, tx);
		return tx;
	}

	@Override
	public List<Tx> getAddressTransactions(String address) {
		URI apiEndpoint = URI.create(this.apiEndpointBase + "address/" + address + "/txs");
		List<Map<String, Object>> response = readArray(apiEndpoint);
		List<Tx> txs = response.stream().map(EsploraElectrsRESTBitcoinConnection::txFromMap).toList();
		if (log.isDebugEnabled()) log.debug("getAddressTransactions for {}: {}", address, txs);
		return txs;
	}

	@Override
	public List<TxOut> getAddressUtxos(String address) {
		URI apiEndpoint = URI.create(this.apiEndpointBase + "address/" + address + "/utxo");
		List<Map<String, Object>> response = readArray(apiEndpoint);
		List<TxOut> txOuts = new ArrayList<>();
		for (Map<String, Object> responseEntry : response) {
			String txId = (String) responseEntry.get("txid");
			int vout = ((Number) responseEntry.get("vout")).intValue();
			Tx tx = this.getTransactionById(txId);
			TxOut txOut = tx.txOuts().get(vout);
			txOuts.add(txOut);
		}
		if (log.isDebugEnabled()) log.debug("getAddressUtxos for {}: {}", address, txOuts);
		return txOuts;
	}

	@Override
	public Block getBlockByTransaction(Tx tx) {
		URI apiEndpoint = URI.create(this.apiEndpointBase + "tx/" + tx.txId());
		Map<String, Object> response = readObject(apiEndpoint);
		Map<String, Object> status = (Map<String, Object>) response.get("status");
		Integer blockHeight = status == null ? null : ((Number) status.get("block_height")).intValue();
		String blockHash = status == null ? null : (String) status.get("block_hash");
		Long blockTime = status == null ? null : ((Number) status.get("block_time")).longValue();
		Integer confirmations = status == null ? null : (((Boolean) status.get("confirmed")) ? 1 : 0);
		Block block = new Block(blockHeight, blockHash, blockTime, confirmations);
		if (log.isDebugEnabled()) log.debug("getBlockByTransaction for {}: {}", tx, block);
		return block;
	}

	@Override
	public void broadcastRawTransaction(byte[] rawTransaction) {
		URI apiEndpoint = URI.create(this.apiEndpointBase + "tx");
		writeBytes(apiEndpoint, rawTransaction);
		if (log.isDebugEnabled()) log.debug("broadcastRawTransaction: {}", Hex.encodeHexString(rawTransaction));
	}

	/*
	 * Helper methods
	 */

	private static Tx txFromMap(Map<String, Object> map) {
		String txId = (String) map.get("txid");
		List<TxIn> txIns = ((List<Map<String, Object>>) map.get("vin")).stream().map(EsploraElectrsRESTBitcoinConnection::txInFromMap).toList();
		List<TxOut> txOuts = ((List<Map<String, Object>>) map.get("vout")).stream().map(EsploraElectrsRESTBitcoinConnection::txOutFromMap).toList();
		return new Tx(txId, txIns, txOuts);
	}

	private static TxIn txInFromMap(Map<String, Object> map) {
		String txId = (String) map.get("txid");
		Integer vout = ((Number) map.get("vout")).intValue();
		return new TxIn(txId, vout);
	}

	private static TxOut txOutFromMap(Map<String, Object> map) {
		String scriptPubKey = (String) map.get("scriptpubkey");
		String scriptPubKeyAsm = (String) map.get("scriptpubkey_asm");
		String scriptPubKeyType = (String) map.get("scriptpubkey_type");
		String scriptPubKeyAddress = (String) map.get("scriptpubkey_address");
		Long value = ((Number) map.get("value")).longValue();
		return new TxOut(scriptPubKey, scriptPubKeyAsm, scriptPubKeyType, scriptPubKeyAddress, value);
	}

	private static String readString(URI uri) {
		HttpURLConnection connection;
		StringBuilder buffer = new StringBuilder();
		try {
			connection = (HttpURLConnection) uri.toURL().openConnection();
			int httpStatus = connection.getResponseCode();
			if (httpStatus != HttpURLConnection.HTTP_OK) throw new IOException("Unexpected HTTP status: " + httpStatus);
			try (InputStream inputStream = connection.getInputStream()) {
				if (inputStream == null) throw new IOException("No input stream");
				if (connection.getInputStream() != null) {
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null) buffer.append(inputLine);
					in.close();
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException("Cannot read from " + uri + "; " + ex.getMessage(), ex);
		}
		if (log.isDebugEnabled()) log.debug("Read response from " + uri + ": " + buffer);
		return buffer.toString();
    }

	private static Map<String, Object> readObject(URI uri) {
		try {
			return (Map<String, Object>) jsonMapper.readValue(readString(uri), Map.class);
		} catch (JsonProcessingException ex) {
			throw new RuntimeException("Cannot parse object response from " + uri + "; " + ex.getMessage(), ex);
		}
	}

	private static List<Map<String, Object>> readArray(URI uri) {
		try {
			return (List<Map<String, Object>>) jsonMapper.readValue(readString(uri), List.class);
		} catch (JsonProcessingException ex) {
			throw new RuntimeException("Cannot parse array response from " + uri + "; " + ex.getMessage(), ex);
		}
	}

	private static void writeBytes(URI uri, byte[] bytes) {
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) uri.toURL().openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			try (OutputStream outputStream = connection.getOutputStream()) {
				byte[] bytesHex = Hex.encodeHexString(bytes).getBytes(StandardCharsets.UTF_8);
				outputStream.write(bytesHex, 0, bytesHex.length);
			}
			int httpStatus = connection.getResponseCode();
			if (httpStatus != HttpURLConnection.HTTP_OK) throw new IOException("Unexpected HTTP status: " + httpStatus);
			connection.disconnect();
		} catch (IOException ex) {
			throw new RuntimeException("Cannot read from " + uri + "; " + ex.getMessage(), ex);
		}
		if (log.isDebugEnabled()) log.debug("Wrote to " + uri);
	}

	/*
	 * Getters and setters
	 */

	public URI getApiEndpointBase() {
		return apiEndpointBase;
	}
}
