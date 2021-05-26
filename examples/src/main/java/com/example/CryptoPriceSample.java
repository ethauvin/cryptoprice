package com.example;

import net.thauvin.erik.crypto.CryptoException;
import net.thauvin.erik.crypto.CryptoPrice;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CryptoPriceSample {
    public static void main(String[] args) {
        try {
            // Get current Bitcoin market price.
            final CryptoPrice price = CryptoPrice.marketPrice("BTC");
            System.out.println("The current Bitcoin price is " + price.toCurrency());

            // Get current Bitcoin market price in Euros.
            final CryptoPrice euroPrice = CryptoPrice.marketPrice("BTC", "EUR");
            System.out.println("The current Bitcoin price is " + euroPrice.toCurrency());

            System.out.println();

            // Get current Ethereum market price in Pound sterling.
            final CryptoPrice gbpPrice = CryptoPrice.marketPrice("ETH", "GBP");
            System.out.println("The current Ethereum price is " + gbpPrice.toCurrency());

            System.out.println();

            // Get current Bitcoin buy price using API.
            // See: https://developers.coinbase.com/api/v2#get-buy-price
            final CryptoPrice buyPrice =
                    CryptoPrice.toPrice(
                            CryptoPrice.apiCall(
                                    List.of("prices", "BTC-USD", "buy"),
                                    Collections.emptyMap()
                            )
                    );
            System.out.println("The current " + buyPrice.getBase() + " buy price is " + buyPrice.getAmount()
                    + " in " + buyPrice.getCurrency());

        } catch (CryptoException | IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
