package net.thauvin.erik.crypto

import net.thauvin.erik.crypto.CryptoPrice.Companion.apiCall
import net.thauvin.erik.crypto.CryptoPrice.Companion.spotPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.toPrice
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Locale
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
        val price = spotPrice("BTC")
        assertEquals("BTC", price.base, "BTC")
        assertEquals("USD", price.currency, "is USD")
        assertTrue(price.amount.signum() > 0, "BTC > 0")
    }

    @Test
    @Throws(CryptoException::class)
    fun testETHPrice() {
        val price = spotPrice("ETH", "EUR")
        assertEquals("ETH", price.base, "ETH")
        assertEquals("EUR", price.currency, "is EUR")
        assertTrue(price.amount.signum() > 0, "ETH > 0")
    }

    @Test
    @Throws(CryptoException::class)
    fun testETH2Price() {
        val price = spotPrice("ETH2", "GBP")
        assertEquals("ETH2", price.base, "ETH2")
        assertEquals("GBP", price.currency, "is GBP")
        assertTrue(price.amount.signum() > 0, "GBP > 0")
    }

    @Test
    @Throws(CryptoException::class)
    fun testBCHPrice() {
        val price = spotPrice("BCH", "GBP", LocalDate.now().minusDays(1))
        assertEquals("BCH", price.base, "BCH")
        assertEquals("GBP", price.currency, "is GBP")
        assertTrue(price.amount.signum() > 0, "BCH > 0")
    }

    @Test
    @Throws(CryptoException::class)
    fun testApiCall() {
        val price = apiCall(listOf("prices", "BTC-USD", "buy"), emptyMap()).toPrice()
        assertEquals("BTC", price.base, "buy BTC")
        assertEquals("USD", price.currency, "buy BTC is USD")
        assertTrue(price.amount.signum() > 0, "buy BTC > 0")
    }

    @Test
    @Throws(CryptoException::class)
    fun testSpotPrice() {
        assertFailsWith(
            message = "FOO did not fail",
            exceptionClass = CryptoException::class,
            block = { spotPrice("FOO") }
        )

        assertFailsWith(
            message = "BAR did not fail",
            exceptionClass = CryptoException::class,
            block = { spotPrice("BTC", "BAR") }
        )

        try {
            spotPrice("FOOBAR")
        } catch (e: CryptoException) {
            assertTrue(e.statusCode != 400, "FOOBAR status code is not 400")
        }
    }

    @Test
    @Throws(IllegalArgumentException::class)
    fun testToCurrency() {
        val d = 12345.67.toBigDecimal()
        val usd = CryptoPrice("BTC", "USD", d)
        assertEquals("$12,345.67", usd.toCurrency(), "EUR format")

        val eur = CryptoPrice("BTC", "EUR", d)
        assertEquals("€12,345.67", eur.toCurrency(), "EUR format")

        val gbp = CryptoPrice("ETH", "GBP", d)
        assertEquals("£12,345.67", gbp.toCurrency(), "GBP format")

        val aud = CryptoPrice("BTC", "AUD", d)
        assertEquals("A$12,345.67", aud.toCurrency(), "AUD format")

        val fr = CryptoPrice("BTC", "EUR", d)
        assertEquals("12 345,67 €", fr.toCurrency(Locale.FRANCE), "EUR-FR format")

        val jp = CryptoPrice("BTC", "JPY", d)
        assertEquals("￥12,345.67", jp.toCurrency(Locale.JAPAN), "EUR-JPY format")
    }

    @Test
    @Throws(CryptoException::class)
    fun testToPrice() {
        val d = "57515.69"
        val json = "{\"data\":{\"base\":\"BTC\",\"currency\":\"USD\",\"amount\":\"$d\"}}"
        val price = json.toPrice()
        assertEquals("BTC", price.base, "base is BTC")
        assertEquals("USD", price.currency, "currency is USD")
        assertEquals(d, price.amount.toString(), "amount is 57515.69")

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
