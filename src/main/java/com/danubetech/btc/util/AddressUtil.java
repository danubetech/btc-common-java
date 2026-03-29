package com.danubetech.btc.util;

import org.bitcoinj.base.Address;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;

import java.net.URI;

public class AddressUtil {

    public static Address bitcoinUriToBitcoinjAddress(URI addressUri) throws BitcoinURIParseException {
        BitcoinURI bitcoinURI = addressUri == null ? null : BitcoinURI.of(addressUri.toString());
        return bitcoinURI == null ? null : bitcoinURI.getAddress();
    }

    public static String bitcoinUriToAddressString(URI addressUri) throws BitcoinURIParseException {
        Address address = bitcoinUriToBitcoinjAddress(addressUri);
        return address == null ? null : address.toString();
    }
}
