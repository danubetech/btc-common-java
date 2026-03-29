package com.danubetech.btc.util;

import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;

import java.net.URI;

public class AddressUtil {

    public static String bitcoinUriToAddress(URI addressUri) throws BitcoinURIParseException {
        BitcoinURI bitcoinURI = addressUri == null ? null : BitcoinURI.of(addressUri.toString());
        return bitcoinURI == null ? null : bitcoinURI.toString();
    }
}
