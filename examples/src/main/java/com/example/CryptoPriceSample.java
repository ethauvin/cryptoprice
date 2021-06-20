package com.example;

import net.thauvin.erik.crypto.CryptoException;
import net.thauvin.erik.crypto.CryptoPrice;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CryptoPriceSample {
    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                final CryptoPrice price;
                if (args.length == 2) {
                    price = CryptoPrice.spotPrice(args[0], args[1]);
                } else {
                    price = CryptoPrice.spotPrice(args[0]);
                }
                System.out.println("The current " + price.getBase() + " price is " + price.getAmount() + " in "
                        + price.getCurrency());
            } else {
                // Get current Bitcoin spot price.
                final CryptoPrice price = CryptoPrice.spotPrice("BTC");
                System.out.println("The current Bitcoin price is " + price.toCurrency());

                System.out.println();

                // Get current Ethereum spot price in Pound sterling.
                final CryptoPrice gbpPrice = CryptoPrice.spotPrice("ETH", "GBP");
                System.out.println("The current Ethereum price is " + gbpPrice.toCurrency());

                // Get current Litecoin spot price in Euros.
                final CryptoPrice euroPrice = CryptoPrice.spotPrice("LTC", "EUR");
                System.out.println("The current Litecoin price is " + euroPrice.toCurrency());

                System.out.println();

                // Get current Bitcoin buy price using API.
                // See: https://developers.coinbase.com/api/v2#get-buy-price
                final CryptoPrice buyPrice = CryptoPrice
                        .toPrice(CryptoPrice.apiCall(List.of("prices", "BTC-USD", "buy"), Collections.emptyMap()));
                System.out.println("The current " + buyPrice.getBase() + " buy price is " + buyPrice.getAmount()
                        + " in " + buyPrice.getCurrency());
            }
        } catch (CryptoException e) {
            System.err.println("HTTP Status Code: " + e.getStatusCode());
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
