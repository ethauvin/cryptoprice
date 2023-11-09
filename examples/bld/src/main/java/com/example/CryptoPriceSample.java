package com.example;

import net.thauvin.erik.crypto.CryptoException;
import net.thauvin.erik.crypto.CryptoPrice;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class CryptoPriceSample {
    public static void main(final String[] args) {
        try {
            if (args.length > 0) {
                final String currency;
                if (args.length == 2) {
                    currency = args[1];
                } else {
                    currency = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
                }
                final var price = CryptoPrice.spotPrice(args[0], currency);
                System.out.println("The current " + price.getBase() + " price is " + price.toCurrency());
            } else {
                // Get current Bitcoin spot price.
                final var price = CryptoPrice.spotPrice("BTC");
                System.out.println("The current Bitcoin price is " + price.toCurrency());

                System.out.println();

                // Get current Ethereum buy price in Pound sterling.
                final var gbpPrice = CryptoPrice.buyPrice("ETH", "GBP");
                System.out.println("The current Ethereum buy price is " + gbpPrice.toCurrency());

                // Get current Litecoin sell price in Euros.
                final var euroPrice = CryptoPrice.sellPrice("LTC", "EUR");
                System.out.println("The current Litecoin sell price is " + euroPrice.toCurrency());

                System.out.println();

                // Get exchange rate using API.
                // See: https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-exchange-rates
                final var response = CryptoPrice.apiCall(List.of("exchange-rates"),
                        Collections.singletonMap("currency", "USD"));
                final var rates = new JSONObject(response).getJSONObject("data").getJSONObject("rates");
                System.out.println("The USD-EUR exchange rate is: " + rates.getString("EUR"));
            }
        } catch (CryptoException e) {
            System.err.println("HTTP Status Code: " + e.getStatusCode());
            System.err.println(e.getMessage() + " (" + e.getId() + ')');
        } catch (IllegalArgumentException e) {
            System.err.println("Could not display the specified currency: " + args[1]);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
