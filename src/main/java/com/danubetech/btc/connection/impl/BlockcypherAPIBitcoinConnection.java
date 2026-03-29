package com.danubetech.btc.connection.impl;

import com.danubetech.btc.connection.BitcoinConnection;
import com.danubetech.btc.connection.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockcypherAPIBitcoinConnection extends AbstractBitcoinConnection implements BitcoinConnection {

	private static final Logger log = LoggerFactory.getLogger(BlockcypherAPIBitcoinConnection.class);

	private BlockcypherAPIBitcoinConnection(Network network) {
		super(network);
		if (log.isDebugEnabled()) log.debug("Creating BlockcypherAPIBitcoinConnection");
	}

	public static BlockcypherAPIBitcoinConnection create(Network network) {
		return new BlockcypherAPIBitcoinConnection(network);
	}
}
