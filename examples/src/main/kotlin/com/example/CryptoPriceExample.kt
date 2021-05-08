package com.example

import net.thauvin.erik.crypto.CryptoPrice.Companion.marketPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.apiCall
import net.thauvin.erik.crypto.CryptoPrice.Companion.toPrice

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
    // Get current Bitcoin market price.
    var price = marketPrice("BTC")
    println("The current Bitcoin price is ${price.amount} in ${price.currency}")

    // Get current Bitcoin market price in Euro.
    var euroPrice = marketPrice("BTC", "EUR")
    println("The current Bitcoin price is ${euroPrice.amount} in Euros")

    // Get current Bitcoin buy price using API.
    var buyPrice = apiCall(listOf("prices", "BTC-USD", "buy"), emptyMap()).toPrice()
    println("The current BTC buy price is ${buyPrice.amount} in ${buyPrice.currency}")

    println()

    // Get current Ethereum market price in Pound sterling.
    var gbpPrice = marketPrice("ETH", "GBP")
    println("The current Ehtereum price is ${gbpPrice.amount} in Pound sterling")
}
