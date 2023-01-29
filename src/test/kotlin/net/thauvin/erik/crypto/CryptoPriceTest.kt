/*
 * CryptoPriceTest.kt
 *
 * Copyright (c) 2021-2022, Erik C. Thauvin (erik@thauvin.net)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *   Neither the name of this project nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.thauvin.erik.crypto

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import assertk.assertions.prop
import net.thauvin.erik.crypto.CryptoPrice.Companion.apiCall
import net.thauvin.erik.crypto.CryptoPrice.Companion.buyPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.sellPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.spotPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.toPrice
import org.json.JSONObject
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

/**
 * [CryptoPrice] Tests
 */
class CryptoPriceTest {
    private val jsonPrice = "{\"base\":\"BTC\",\"currency\":\"USD\",\"amount\":\"%s\"}"
    private val jsonData = "{\"data\":$jsonPrice}"

    @Test
    @Throws(CryptoException::class)
    fun testBitcoinPrice() {
        val prices = listOf(spotPrice("BTC"), buyPrice("BTC"), sellPrice("BTC"))
        for (price in prices) {
            assertThat(price).all {
                prop(CryptoPrice::base).isEqualTo("BTC")
                prop(CryptoPrice::currency).isEqualTo("USD")
                prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
            }
        }
    }

    @Test
    @Throws(CryptoException::class)
    fun testEtherPrice() {
        val prices = listOf(spotPrice("ETH", "EUR"), buyPrice("ETH", "EUR"), sellPrice("ETH", "EUR"))
        for (price in prices) {
            assertThat(price).all {
                prop(CryptoPrice::base).isEqualTo("ETH")
                prop(CryptoPrice::currency).isEqualTo("EUR")
                prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
            }

        }
    }

    @Test
    @Throws(CryptoException::class)
    fun testLitecoinPrice() {
        val prices = listOf(spotPrice("LTC", "GBP"), buyPrice("LTC", "GBP"), sellPrice("LTC", "GBP"))
        for (price in prices) {
            assertThat(price).all {
                prop(CryptoPrice::base).isEqualTo("LTC")
                prop(CryptoPrice::currency).isEqualTo("GBP")
                prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
            }
        }
    }

    @Test
    @Throws(CryptoException::class)
    fun testBitcoinCashPrice() {
        val price = spotPrice("BCH", "GBP", LocalDate.now().minusDays(1))
        assertThat(price, "spotPrice(BCH,GPB)").all {
            prop(CryptoPrice::base).isEqualTo("BCH")
            prop(CryptoPrice::currency).isEqualTo("GBP")
            prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
        }
    }

    @Test
    @Throws(CryptoException::class)
    fun testApiCall() {
        val price = apiCall(listOf("prices", "BTC-USD", "buy"), emptyMap()).toPrice()
        assertThat(price, "apiCall(prices,BTC-USD,buy)").all {
            prop(CryptoPrice::base).isEqualTo("BTC")
            prop(CryptoPrice::currency).isEqualTo("USD")
            prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
        }
        val response = apiCall(listOf("exchange-rates"), mapOf("currency" to "usd"))
        val rates = JSONObject(response).getJSONObject("data").getJSONObject("rates")
        assertEquals("1.0", rates.getString("USD"), "apiCall(exchange-rates,USD)")
    }

    @Test
    @Throws(CryptoException::class)
    fun testPrices() {
        assertFailsWith(
            message = "spotPrice(FOO)",
            exceptionClass = CryptoException::class,
            block = { spotPrice("FOO") }
        )

        try {
            spotPrice("BAR")
        } catch (e: CryptoException) {
            assertThat(e.id, "spotPrice(bar) error id").isEqualTo("not_found")
            assertThat(e.message, "spotPrice(bar) error message").isEqualTo("Invalid base currency")
        }

        assertFailsWith(
            message = "buyPrice(BTC,BAR)",
            exceptionClass = CryptoException::class,
            block = { buyPrice("BTC", "BAR") }
        )

        try {
            sellPrice("FOOBAR")
        } catch (e: CryptoException) {
            assertThat(e, "sellPrice(FOOBAR)").all {
                prop(CryptoException::statusCode).isEqualTo(404)
                prop(CryptoException::message).isNotNull().contains("invalid", true)
            }
        }
    }

    @Test
    @Throws(IllegalArgumentException::class)
    fun testToCurrency() {
        val d = 12345.60.toBigDecimal()
        val usd = CryptoPrice("BTC", "USD", d)
        assertEquals("$12,345.60", usd.toCurrency(), "CryptoPrice(BTC,USD)")

        assertEquals(usd, usd, "CryptoPrice(BTC,USD) = CryptoPrice(BTC,USD)")
        assertNotEquals(
            usd,
            CryptoPrice("BTC", "USD", 12345.70.toBigDecimal()),
            "CryptoPrice(BTC,USD) = CryptoPrice(BTC,USD,BigDecimal)"
        )

        val eur = CryptoPrice("BTC", "EUR", d)
        assertEquals("€12,345.60", eur.toCurrency(), "CryptoPrice(BTC,EUR)")

        assertNotEquals(
            usd.hashCode(),
            eur.hashCode(),
            "CryptoPrice(BTC,USD).hashCode != CryptoPrice(BTC,EUR).hashCode"
        )
        assertNotEquals(usd, eur, "CryptoPrice(BTC,USD) != CryptoPrice(BTC,EUR)")

        val gbp = CryptoPrice("ETH", "GBP", d)
        assertEquals("£12,345.60", gbp.toCurrency(), "CryptoPrice(ETH,GBP)")

        assertNotEquals(usd, gbp, "CryptoPrice(BTC,USD) != CryptoPrice(BTC,GBP)")

        val aud = CryptoPrice("LTC", "AUD", d)
        assertEquals("A$12,345.60", aud.toCurrency(), "CryptoPrice(LTC,AUD)")

        val dk = CryptoPrice("BCH", "DKK", d)
        assertEquals(
            "12.345,60 kr.", dk.toCurrency(
                Locale.Builder().setLanguage("da").setRegion("DK").build()
            ), "CryptoPrice(BCH,DKK)"
        )

        val jp = CryptoPrice("BTC", "JPY", d)
        assertEquals("￥12,345.60", jp.toCurrency(Locale.JAPAN), "CryptoPrice(BTC,JPY)")

        assertEquals("$12,345.6000", usd.toCurrency(minFractionDigits = 4), "toCurrency(minFractionDigits = 4)")
        assertEquals("$12,345.6", usd.toCurrency(minFractionDigits = 0), "toCurrency(minFractionDigits = 0)")
    }

    @Test
    fun testToJson() {
        listOf("1234.5", "1234.56", "1234.567").forEachIndexed { i, d ->
            val data = jsonData.format(d)
            with(data.toPrice()) {
                assertEquals(data, toJson(), "toJson($d)")
                assertEquals(data.replace("data", "price$i"), toJson("price$i"), "toJson(price$i)")
            }
        }
    }

    @Test
    @Throws(CryptoException::class)
    fun testToPrice() {
        val d = "57515.60"
        val data = jsonData.format(d)
        val price = data.toPrice()
        assertThat(price, "toPrice()").all {
            prop(CryptoPrice::base).isEqualTo("BTC")
            prop(CryptoPrice::currency).isEqualTo("USD")
            prop(CryptoPrice::amount).isEqualTo(BigDecimal(d))
        }

        assertEquals(price, price.toString().toPrice(""), "toPrice('')")
        assertEquals(price, price.toJson("test").toPrice("test"), "toPrice(test)")

        assertFailsWith(
            message = "a.toPrice()",
            exceptionClass = CryptoException::class,
            block = { data.replace("5", "a").toPrice() }
        )

        assertFailsWith(
            message = "{}.toPrice()",
            exceptionClass = CryptoException::class,
            block = { "{}".toPrice() }
        )

        assertFailsWith(
            message = "foo.toPrice()",
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
