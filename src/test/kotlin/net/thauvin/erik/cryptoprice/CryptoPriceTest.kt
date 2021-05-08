package net.thauvin.erik.crypto

import net.thauvin.erik.crypto.CryptoPrice.Companion.marketPrice
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import java.time.LocalDate

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
        assertEquals(price.base, "BCH", "ETH2")
        assertEquals(price.currency, "GBP", "is GBP")
        assertTrue(price.amount > 0.00, "BCH > 0")
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
    }
}
