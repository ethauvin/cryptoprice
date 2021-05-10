package com.example

import net.thauvin.erik.crypto.CryptoPrice.Companion.apiCall
import net.thauvin.erik.crypto.CryptoPrice.Companion.marketPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.toPrice

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
    // Get current Bitcoin market price.
    val price = marketPrice("BTC")
    println("The current Bitcoin price is ${price.amount} in ${price.currency}")

    // Get current Bitcoin market price in Euro.
    val euroPrice = marketPrice("BTC", "EUR")
    println("The current Bitcoin price is ${euroPrice.amount} in Euros")

    // Get current Bitcoin buy price using API.
    // See: https://developers.coinbase.com/api/v2#get-buy-price
    val buyPrice = apiCall(listOf("prices", "BTC-USD", "buy"), emptyMap()).toPrice()
    println("The current BTC buy price is ${buyPrice.amount} in ${buyPrice.currency}")

    println()

    // Get current Ethereum market price in Pound sterling.
    val gbpPrice = marketPrice("ETH", "GBP")
    println("The current Ehtereum price is ${gbpPrice.amount} in Pound sterling")
}
