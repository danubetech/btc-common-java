package com.danubetech.btc.connection.impl;

import com.danubetech.btc.connection.BitcoinConnection;
import com.danubetech.btc.connection.Network;
import org.bitcoinj.kits.WalletAppKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

public class BitcoinjSPVBitcoinConnection extends AbstractBitcoinConnection implements BitcoinConnection {

	private static final Logger log = LoggerFactory.getLogger(BitcoindRPCBitcoinConnection.class);

	private final WalletAppKit walletAppKit;

	private BitcoinjSPVBitcoinConnection(Network network, WalletAppKit walletAppKit) {
		super(network);
		if (log.isDebugEnabled()) log.debug("Creating BitcoindRPCBitcoinConnection: " + walletAppKit);
		this.walletAppKit = walletAppKit;
	}

	public static BitcoinjSPVBitcoinConnection create(Network network) {
		if (log.isDebugEnabled()) log.debug("Creating BitcoindRPCBitcoinConnection: " + network);
		return new BitcoinjSPVBitcoinConnection(network, WalletAppKit.launch(network.toBitcoinjNetwork(), new File("."), network.name()));
	}

	@Override
	public Map<String, Object> getMetadata() {
		return Map.of(
				"chain", "" +this.getWalletAppKit().chain(),
				"network", "" + this.getWalletAppKit().network());
	}

	/*
	 * Getters and setters
	 */

	public WalletAppKit getWalletAppKit() {
		return walletAppKit;
	}
}