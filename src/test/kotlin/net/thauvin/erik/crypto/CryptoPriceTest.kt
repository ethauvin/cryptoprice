package net.thauvin.erik.crypto

import net.thauvin.erik.crypto.CryptoPrice.Companion.apiCall
import net.thauvin.erik.crypto.CryptoPrice.Companion.spotPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.toPrice
import java.time.LocalDate
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * [CryptoPrice] Tests
 */
class CryptoPriceTest {
    private val jsonPrice = "{\"base\":\"BTC\",\"currency\":\"USD\",\"amount\":\"%s\"}"
    private val jsonData = "{\"data\":$jsonPrice}"

    @Test
    @Throws(CryptoException::class)
    fun testBitcoinPrice() {
        val price = spotPrice("BTC")
        assertEquals("BTC", price.base, "BTC")
        assertEquals("USD", price.currency, "is USD")
        assertTrue(price.amount.signum() > 0, "BTC > 0")
    }

    @Test
    @Throws(CryptoException::class)
    fun testEtherPrice() {
        val price = spotPrice("ETH", "EUR")
        assertEquals("ETH", price.base, "ETH")
        assertEquals("EUR", price.currency, "is EUR")
        assertTrue(price.amount.signum() > 0, "ETH > 0")
    }

    @Test
    @Throws(CryptoException::class)
    fun testLitecoinPrice() {
        val price = spotPrice("LTC", "GBP")
        assertEquals("LTC", price.base, "LTC")
        assertEquals("GBP", price.currency, "is GBP")
        assertTrue(price.amount.signum() > 0, "LTC > 0")
    }

    @Test
    @Throws(CryptoException::class)
    fun testBitcoinCashPrice() {
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
        val d = 12345.60.toBigDecimal()
        val usd = CryptoPrice("BTC", "USD", d)
        assertEquals("$12,345.60", usd.toCurrency(), "USD format")

        assertEquals(usd, usd, "USD = USD")
        assertNotEquals(usd, CryptoPrice("BTC", "USD", 12345.70.toBigDecimal()), ".60 !- .70")

        val eur = CryptoPrice("BTC", "EUR", d)
        assertEquals("€12,345.60", eur.toCurrency(), "EUR format")

        assertNotEquals(usd.hashCode(), eur.hashCode(), "hashCode()")
        assertNotEquals(usd, eur, "USD != EUR")

        val gbp = CryptoPrice("ETH", "GBP", d)
        assertEquals("£12,345.60", gbp.toCurrency(), "GBP format")

        assertNotEquals(usd, gbp, "BTC != ETH")

        val aud = CryptoPrice("LTC", "AUD", d)
        assertEquals("A$12,345.60", aud.toCurrency(), "AUD format")

        val dk = CryptoPrice("BCH", "DKK", d)
        assertEquals("12.345,60 kr.", dk.toCurrency(Locale("da", "DK")), "EUR-DKK format")

        val jp = CryptoPrice("BTC", "JPY", d)
        assertEquals("￥12,345.60", jp.toCurrency(Locale.JAPAN), "EUR-JPY format")

        assertEquals("$12,345.6000", usd.toCurrency(minFractionDigits = 4), "minFractionDigits = 4")
        assertEquals("$12,345.6", usd.toCurrency(minFractionDigits = 0), "minFractionDigits = 0")
    }

    @Test
    fun testToJson() {
        listOf("1234.5", "1234.56", "1234.567").forEachIndexed { i, d ->
            val data = jsonData.format(d)
            with(data.toPrice()) {
                assertEquals(data, toJson(), "toJson($d)")
                assertEquals(data.replace("data","price$i"), toJson("price$i"), "toJson(price$i)")
            }
        }
    }


    @Test
    @Throws(CryptoException::class)
    fun testToPrice() {
        val d = "57515.60"
        val data = jsonData.format(d)
        val price = data.toPrice()
        assertEquals("BTC", price.base, "base is BTC")
        assertEquals("USD", price.currency, "currency is USD")
        assertEquals(d, price.amount.toString(), "amount is $d")

        assertEquals(price, price.toString().toPrice(""), "toPrice('')")
        assertEquals(price, price.toJson("test").toPrice("test"), "toPrice(test)")
        
        assertFailsWith(
            message = "amount conversion did not fail",
            exceptionClass = CryptoException::class,
            block = { data.replace("5", "a").toPrice() }
        )

        assertFailsWith(
            message = "empty did not fail",
            exceptionClass = CryptoException::class,
            block = { "{}".toPrice() }
        )

        assertFailsWith(
            message = "no base did not fail",
            exceptionClass = CryptoException::class,
            block = { data.replace("base", "foo").toPrice() }
        )
    }

    @Test
    fun testToString() {
        val json = jsonPrice.format("1234.5") 
        val price = json.toPrice("")
        assertEquals(json, price.toString(), "toString()")
        assertEquals(price.toString(), price.toJson(""), "toString() = toJson('')")
    }
}
