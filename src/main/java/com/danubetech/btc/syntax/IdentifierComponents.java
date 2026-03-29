package com.danubetech.btc.syntax;

import com.danubetech.btc.connection.Network;

public record IdentifierComponents(
        int version,
        Network network,
        byte[] genesisBytes,
        GenesisBytesType genesisBytesType) {
}
