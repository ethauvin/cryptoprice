package com.example

import net.thauvin.erik.crypto.CryptoException
import net.thauvin.erik.crypto.CryptoPrice.Companion.apiCall
import net.thauvin.erik.crypto.CryptoPrice.Companion.buyPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.sellPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.spotPrice
import org.json.JSONObject
import java.io.IOException
import java.util.*

fun main(args: Array<String>) {
    try {
        if (args.isNotEmpty()) {
            val currency = if (args.size == 2) args[1] else Currency.getInstance(Locale.getDefault()).currencyCode
            val price = spotPrice(args[0], currency)
            println("The current ${price.base} price is ${price.toCurrency()}")
        } else {
            // Get current Bitcoin spot price.
            val price = spotPrice("BTC")
            println("The current Bitcoin price is ${price.toCurrency()}")

            println()

            // Get current Ethereum sell price in Pound sterling.
            val gbpPrice = sellPrice("ETH", "GBP")
            println("The current Ethereum sell price is ${gbpPrice.toCurrency()}")

            // Get current Litecoin buy price in Euro.
            val euroPrice = buyPrice("LTC", "EUR")
            println("The current Litecoin buy price is ${euroPrice.toCurrency()}")

            println()

            // Get exchange rate using API.
            // See: https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-exchange-rates
            val response = apiCall(listOf("exchange-rates"), mapOf("currency" to "usd"))
            val rates = JSONObject(response).getJSONObject("data").getJSONObject("rates")
            println("The USD-EUR exchange rate is: ${rates.getString("EUR")}")
        }
    } catch (e: CryptoException) {
        System.err.println("HTTP Status Code: ${e.statusCode}")
        System.err.println("${e.message} (${e.id})")
    } catch (e: IllegalArgumentException) {
        System.err.println("Could not display the specified currency: ${args[1]}")
    } catch (e: IOException) {
        System.err.println(e.message)
    }
}
