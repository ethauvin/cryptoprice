package com.example

import net.thauvin.erik.crypto.CryptoException
import net.thauvin.erik.crypto.CryptoPrice.Companion.apiCall
import net.thauvin.erik.crypto.CryptoPrice.Companion.spotPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.toPrice

import java.io.IOException

fun main(args: Array<String>) {
    try {
        if (args.isNotEmpty()) {
            val price = if (args.size == 2) spotPrice(args[0], args[1]) else spotPrice(args[0])
            println("The current ${price.base} price is ${price.amount} in ${price.currency}")
        } else {
            // Get current Bitcoin spot price.
            val price = spotPrice("BTC")
            println("The current Bitcoin price is ${price.toCurrency()}")

            println()

            // Get current Ethereum spot price in Pound sterling.
            val gbpPrice = spotPrice("ETH", "GBP")
            println("The current Ethereum price is ${gbpPrice.toCurrency()}")

            // Get current Litecoin spot price in Euro.
            val euroPrice = spotPrice("LTC", "EUR")
            println("The current Litecoin price is ${euroPrice.toCurrency()}")

            println()

            // Get current Bitcoin buy price using API.
            // See: https://developers.coinbase.com/api/v2#get-buy-price
            val buyPrice = apiCall(listOf("prices", "BTC-USD", "buy"), emptyMap()).toPrice()
            println("The current ${buyPrice.base} buy price is ${buyPrice.amount} in ${buyPrice.currency}")
        }
    } catch (e: CryptoException) {
        System.err.println("HTTP Status Code: ${e.statusCode}")
        System.err.println(e.message)
    } catch (e: IOException) {
        System.err.println(e.message)
    }
}
