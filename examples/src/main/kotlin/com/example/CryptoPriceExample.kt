package com.example

import net.thauvin.erik.crypto.CryptoPrice.Companion.apiCall
import net.thauvin.erik.crypto.CryptoPrice.Companion.spotPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.toPrice

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
    // Get current Bitcoin spot price.
    val price = spotPrice("BTC")
    println("The current Bitcoin price is ${price.toCurrency()}")

    // Get current Bitcoin spot price in Euro.
    val euroPrice = spotPrice("BTC", "EUR")
    println("The current Bitcoin price is ${euroPrice.toCurrency()}")

    println()

    // Get current Ethereum spot price in Pound sterling.
    val gbpPrice = spotPrice("ETH", "GBP")
    println("The current Ehtereum price is ${gbpPrice.toCurrency()}")

    println()
    
    // Get current Bitcoin buy price using API.
    // See: https://developers.coinbase.com/api/v2#get-buy-price
    val buyPrice = apiCall(listOf("prices", "BTC-USD", "buy"), emptyMap()).toPrice()
    println("The current ${buyPrice.base} buy price is ${buyPrice.amount} in ${buyPrice.currency}")
}
