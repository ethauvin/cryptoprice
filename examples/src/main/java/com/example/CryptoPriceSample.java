package com.example;

import net.thauvin.erik.crypto.CryptoPrice;
import net.thauvin.erik.crypto.CryptoException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CryptoPriceSample {
    public static void main(String[] args) {
        try {
            // Get current Bitcoin market price.
            final CryptoPrice price = CryptoPrice.marketPrice("BTC");
            System.out.println("The current Bitcoin price is " + price.getAmount() + " in " + price.getCurrency());

            // Get current Bitcoin market price in Euros.
            final CryptoPrice euroPrice = CryptoPrice.marketPrice("BTC", "EUR");
            System.out.println("The current Bitcoin price is " + euroPrice.getAmount() + " in Euros");

            // Get current Bitcoin buy price using API.
            final CryptoPrice buyPrice =
                    CryptoPrice.toPrice(
                            CryptoPrice.apiCall(
                                    List.of("prices", "BTC-USD", "buy"),
                                    Collections.<String, String>emptyMap()
                            )
                    );
            System.out.println("The current BTC buy price is " + price.getAmount() + " in " + price.getCurrency());

            System.out.println();

            // Get current Ethereum market price in Pound sterling.
            final CryptoPrice gbpPrice = CryptoPrice.marketPrice("ETH", "GBP");
            System.out.println("The current Ethereum price is " + gbpPrice.getAmount() + " in Pound sterling");

        } catch (CryptoException e) {
            System.err.println(e.getMessage());
        }
    }
}
