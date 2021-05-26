package net.thauvin.erik.crypto

import net.thauvin.erik.crypto.CryptoPrice.Companion.apiCall
import net.thauvin.erik.crypto.CryptoPrice.Companion.marketPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.toPrice
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * The `CryptoPriceTest` class.
 */
class CryptoPriceTest {
    @Test
    @Throws(CryptoException::class)
    fun testBTCPrice() {
        val price = marketPrice("BTC")
        assertEquals(price.base, "BTC", "BTC")
        assertEquals(price.currency, "USD", "is USD")
        assertTrue(price.amount > 0.00, "BTC > 0")
    }

    @Test
    @Throws(CryptoException::class)
    fun testETHPrice() {
        val price = marketPrice("ETH", "EUR")
        assertEquals(price.base, "ETH", "ETH")
        assertEquals(price.currency, "EUR", "is EUR")
        assertTrue(price.amount > 0.00, "ETH > 0")
    }

    @Test
    @Throws(CryptoException::class)
    fun testETH2Price() {
        val price = marketPrice("ETH2", "GBP")
        assertEquals(price.base, "ETH2", "ETH2")
        assertEquals(price.currency, "GBP", "is GBP")
        assertTrue(price.amount > 0.00, "GBP > 0")
    }

    @Test
    @Throws(CryptoException::class)
    fun testBCHPrice() {
        val price = marketPrice("BCH", "GBP", LocalDate.now().minusDays(1))
        assertEquals(price.base, "BCH", "BCH")
        assertEquals(price.currency, "GBP", "is GBP")
        assertTrue(price.amount > 0.00, "BCH > 0")
    }

    @Test
    @Throws(CryptoException::class)
    fun testApiCall() {
        val price = apiCall(listOf("prices", "BTC-USD", "buy"), emptyMap()).toPrice()
        assertEquals(price.base, "BTC", "buy BTC")
        assertEquals(price.currency, "USD", "buy BTC is USD")
        assertTrue(price.amount > 0.00, "buy BTC > 0")
    }

    @Test
    @Throws(CryptoException::class)
    fun testMarketPriceExceptions() {
        assertFailsWith(
            message = "FOO did not fail",
            exceptionClass = CryptoException::class,
            block = { marketPrice("FOO") }
        )

        assertFailsWith(
            message = "BAR did not fail",
            exceptionClass = CryptoException::class,
            block = { marketPrice("BTC", "BAR") }
        )

        try {
            marketPrice("FOOBAR")
        } catch (e: CryptoException) {
            assertTrue(e.statusCode != 400, "FOOBAR status code is not 400")
        }
    }

    @Test
    @Throws(IllegalArgumentException::class)
    fun testToCurrency() {
        val eur = CryptoPrice("BTC", "EUR", 12345.67)
        assertEquals(eur.toCurrency(), "€12,345.67", "EUR format")
        
        val gbp = CryptoPrice("ETH", "GBP", 12345.67)
        assertEquals(gbp.toCurrency(), "£12,345.67", "GBP format")
        
        val aud = CryptoPrice("BTC", "AUD", 12345.67)
        assertEquals(aud.toCurrency(), "A$12,345.67", "AUD format")
    }

    @Test
    @Throws(CryptoException::class)
    fun testToPrice() {
        val d = 57515.69
        val json = "{\"data\":{\"base\":\"BTC\",\"currency\":\"USD\",\"amount\":\"$d\"}}"
        val price = json.toPrice()
        assertEquals(price.base, "BTC", "base is BTC")
        assertEquals(price.currency, "USD", "currency is USD")
        assertEquals(price.amount, d, "amount is 57515.69")

        assertFailsWith(
            message = "double conversion did not fail",
            exceptionClass = CryptoException::class,
            block = { json.replace("5", "a").toPrice() }
        )

        assertFailsWith(
            message = "empty did not fail",
            exceptionClass = CryptoException::class,
            block = { "{}".toPrice() }
        )

        assertFailsWith(
            message = "no base did not fail",
            exceptionClass = CryptoException::class,
            block = { json.replace("base", "foo").toPrice() }
        )
    }
}
