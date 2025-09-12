/*
 * CryptoPriceTests.kt
 *
 * Copyright 2021-2025 Erik C. Thauvin (erik@thauvin.net)
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
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.prop
import net.thauvin.erik.crypto.CryptoPrice.Companion.apiCall
import net.thauvin.erik.crypto.CryptoPrice.Companion.buyPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.sellPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.spotPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.toPrice
import org.json.JSONObject
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import rife.bld.extension.testing.LoggingExtension
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

@ExtendWith(LoggingExtension::class)
class CryptoPriceTests {
    companion object {
        @RegisterExtension
        @JvmField
        @Suppress("unused")
        val loggingExtension = LoggingExtension(CryptoPrice.logger)
    }

    @Nested
    @DisplayName("API Call Tests")
    inner class ApiCallTests {
        @Test
        @Throws(CryptoException::class)
        fun apiCallExchangeRates() {
            val response = apiCall(listOf("exchange-rates"), mapOf("currency" to "usd"))
            val rates = JSONObject(response).getJSONObject("data").getJSONObject("rates")
            assertEquals("1.0", rates.getString("USD"), "apiCall(exchange-rates,USD)")
        }

        @Test
        @Throws(CryptoException::class)
        fun apiCallPrices() {
            val price = apiCall(listOf("prices", "BTC-USD", "buy"), emptyMap()).toPrice()
            assertThat(price, "apiCall(prices,BTC-USD,buy)").all {
                prop(CryptoPrice::base).isEqualTo("BTC")
                prop(CryptoPrice::currency).isEqualTo("USD")
                prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
            }
        }
    }

    @Nested
    @DisplayName("Crypto Price Tests")
    inner class CryptoPriceTests {
        @Nested
        @DisplayName("Bitcoin Price Tests")
        inner class BitcoinPriceTests {
            @Test
            @Throws(CryptoException::class)
            fun bitcoinBuyPrice() {
                assertThat(buyPrice("BTC")).all {
                    prop(CryptoPrice::base).isEqualTo("BTC")
                    prop(CryptoPrice::currency).isEqualTo("USD")
                    prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
                }
            }

            @Test
            @Throws(CryptoException::class)
            fun bitcoinCashPrice() {
                val price = spotPrice("BCH", "GBP", LocalDate.now().minusDays(1))
                assertThat(price, "spotPrice(BCH,GPB)").all {
                    prop(CryptoPrice::base).isEqualTo("BCH")
                    prop(CryptoPrice::currency).isEqualTo("GBP")
                    prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
                }
            }

            @Test
            @Throws(CryptoException::class)
            fun bitcoinSellPrice() {
                assertThat(sellPrice("BTC")).all {
                    prop(CryptoPrice::base).isEqualTo("BTC")
                    prop(CryptoPrice::currency).isEqualTo("USD")
                    prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
                }
            }

            @Test
            @Throws(CryptoException::class)
            fun bitcoinSpotPrice() {
                assertThat(spotPrice("BTC")).all {
                    prop(CryptoPrice::base).isEqualTo("BTC")
                    prop(CryptoPrice::currency).isEqualTo("USD")
                    prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
                }
            }
        }

        @Nested
        @DisplayName("Ethereum Price Tests")
        inner class EthereumPriceTests {
            @Test
            @Throws(CryptoException::class)
            fun etherBuyPrice() {
                assertThat(buyPrice("ETH", "EUR")).all {
                    prop(CryptoPrice::base).isEqualTo("ETH")
                    prop(CryptoPrice::currency).isEqualTo("EUR")
                    prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
                }
            }

            @Test
            @Throws(CryptoException::class)
            fun etherSellPrice() {
                assertThat(sellPrice("ETH", "EUR")).all {
                    prop(CryptoPrice::base).isEqualTo("ETH")
                    prop(CryptoPrice::currency).isEqualTo("EUR")
                    prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
                }
            }

            @Test
            @Throws(CryptoException::class)
            fun etherSpotPrice() {
                assertThat(spotPrice("ETH", "EUR")).all {
                    prop(CryptoPrice::base).isEqualTo("ETH")
                    prop(CryptoPrice::currency).isEqualTo("EUR")
                    prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
                }
            }
        }

        @Test
        @Throws(CryptoException::class)
        fun invalidBaseSymbol() {
            try {
                sellPrice("FOOBAR")
            } catch (e: CryptoException) {
                assertThat(e, "sellPrice(FOOBAR)").all {
                    prop(CryptoException::statusCode).isEqualTo(404)
                    prop(CryptoException::message).isEqualTo("not found")
                    prop(CryptoException::id).isEqualTo("not found")
                }
            }
        }

        @Test
        @Throws(CryptoException::class)
        fun invalidCurrency() {
            assertFailsWith(
                message = "buyPrice(BTC,BAR)",
                exceptionClass = CryptoException::class,
                block = { buyPrice("BTC", "BAR") }
            )
        }

        @Nested
        @DisplayName("Litecoin Price Tests")
        inner class LitecoinPriceTests {
            @Test
            @Throws(CryptoException::class)
            fun litecoinBuyPrice() {
                assertThat(buyPrice("LTC", "GBP")).all {
                    prop(CryptoPrice::base).isEqualTo("LTC")
                    prop(CryptoPrice::currency).isEqualTo("GBP")
                    prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
                }
            }

            @Test
            @Throws(CryptoException::class)
            fun litecoinSellPrice() {
                assertThat(sellPrice("LTC", "GBP")).all {
                    prop(CryptoPrice::base).isEqualTo("LTC")
                    prop(CryptoPrice::currency).isEqualTo("GBP")
                    prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
                }
            }

            @Test
            @Throws(CryptoException::class)
            fun litecoinSpotPrice() {
                assertThat(spotPrice("LTC", "GBP")).all {
                    prop(CryptoPrice::base).isEqualTo("LTC")
                    prop(CryptoPrice::currency).isEqualTo("GBP")
                    prop(CryptoPrice::amount).isGreaterThan(BigDecimal(0))
                }
            }
        }
    }

    @Nested
    @DisplayName("Currency Conversion Tests")
    inner class CurrencyConversionTests {
        private val amount = 12345.60.toBigDecimal()
        private val usd = CryptoPrice("BTC", "USD", amount)

        @Test
        @Throws(IllegalArgumentException::class)
        fun bchToDkk() {
            val dk = CryptoPrice("BCH", "DKK", amount)
            assertEquals(
                "12.345,60 kr.", dk.toCurrency(
                    Locale.Builder().setLanguage("da").setRegion("DK").build()
                ), "CryptoPrice(BCH,DKK)"
            )
        }

        @Test
        @Throws(IllegalArgumentException::class)
        fun bchToJpy() {
            val jp = CryptoPrice("BTC", "JPY", amount)
            assertEquals("￥12,345.60", jp.toCurrency(Locale.JAPAN), "CryptoPrice(BTC,JPY)")
        }

        @Test
        @Throws(IllegalArgumentException::class)
        fun bchToJpyWithFractionDigits() {
            CryptoPrice("BTC", "JPY", amount)
            assertEquals(
                "$12,345.6000", usd.toCurrency(minFractionDigits = 4),
                "toCurrency(minFractionDigits = 4)"
            )
        }

        @Test
        @Throws(IllegalArgumentException::class)
        fun bchToJpyWithZeroFractionDigits() {
            CryptoPrice("BTC", "JPY", amount)
            assertEquals(
                "$12,345.6", usd.toCurrency(minFractionDigits = 0),
                "toCurrency(minFractionDigits = 0)"
            )
        }

        @Test
        @Throws(IllegalArgumentException::class)
        fun btcToEur() {
            val eur = CryptoPrice("BTC", "EUR", amount)
            assertEquals("€12,345.60", eur.toCurrency(), "CryptoPrice(BTC,EUR)")

            assertNotEquals(
                usd.hashCode(),
                eur.hashCode(),
                "CryptoPrice(BTC,USD).hashCode != CryptoPrice(BTC,EUR).hashCode"
            )
            assertNotEquals(usd, eur, "CryptoPrice(BTC,USD) != CryptoPrice(BTC,EUR)")
        }

        @Test
        @Throws(IllegalArgumentException::class)
        fun btcToUsd() {
            assertEquals("$12,345.60", usd.toCurrency(), "CryptoPrice(BTC,USD)")

            assertEquals(usd, usd, "CryptoPrice(BTC,USD) = CryptoPrice(BTC,USD)")
            assertNotEquals(
                usd,
                CryptoPrice("BTC", "USD", 12345.70.toBigDecimal()),
                "CryptoPrice(BTC,USD) = CryptoPrice(BTC,USD,BigDecimal)"
            )
        }

        @Test
        @Throws(IllegalArgumentException::class)
        fun ethToGbp() {
            val gbp = CryptoPrice("ETH", "GBP", amount)
            assertEquals("£12,345.60", gbp.toCurrency(), "CryptoPrice(ETH,GBP)")

            assertNotEquals(usd, gbp, "CryptoPrice(BTC,USD) != CryptoPrice(BTC,GBP)")
        }

        @Test
        @Throws(IllegalArgumentException::class)
        fun ltcToAud() {
            val aud = CryptoPrice("LTC", "AUD", amount)
            assertEquals("A$12,345.60", aud.toCurrency(), "CryptoPrice(LTC,AUD)")
        }
    }

    @Nested
    @DisplayName("Formatting Tests")
    inner class FormattingTests {
        private val jsonPrice = "{\"base\":\"BTC\",\"currency\":\"USD\",\"amount\":\"%s\"}"
        private val jsonData = "{\"data\":$jsonPrice}"

        @ParameterizedTest
        @ValueSource(strings = ["1234.5", "1234.56", "1234.567"])
        fun toJson(price: String) {
            val data = jsonData.format(price)
            with(data.toPrice()) {
                assertEquals(data, toJson(), "toJson(price)")
                assertEquals(
                    data.replace("data", "test"),
                    toJson("test"), "toJson(test)"
                )
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["1234.5", "1234.56", "1234.567"])
        fun toJsonWithKey(price: String) {
            val data = jsonData.format(price)
            with(data.toPrice()) {
                assertEquals(
                    data.replace("data", "test"),
                    toJson("test"), "toJson(test)"
                )
            }
        }

        @Test
        fun toStringMethod() {
            val json = jsonPrice.format("1234.5")
            val price = json.toPrice("")
            assertEquals(json, price.toString(), "toString()")
            assertEquals(price.toString(), price.toJson(""), "toString() = toJson('')")
        }

        @Test
        @Throws(CryptoException::class)
        fun toPriceEmptyJson() {
            assertFailsWith(
                message = "{}.toPrice()",
                exceptionClass = CryptoException::class,
                block = { "{}".toPrice() }
            )
        }

        @Test
        @Throws(CryptoException::class)
        fun toPriceInvalidAmount() {
            val d = "57515.60"
            val data = jsonData.format(d)
            assertFailsWith(
                message = "a.toPrice()",
                exceptionClass = CryptoException::class,
                block = { data.replace("5", "a").toPrice() }
            )
        }

        @Test
        @Throws(CryptoException::class)
        fun toPriceInvalidKey() {
            val d = "57515.60"
            val data = jsonData.format(d)
            assertFailsWith(
                message = "foo.toPrice()",
                exceptionClass = CryptoException::class,
                block = { data.replace("base", "foo").toPrice() }
            )
        }

        @Test
        @Throws(CryptoException::class)
        fun toPriceSuccess() {
            val d = "57515.60"
            val data = jsonData.format(d)
            val price = data.toPrice()
            assertThat(price, "toPrice()").all {
                prop(CryptoPrice::base).isEqualTo("BTC")
                prop(CryptoPrice::currency).isEqualTo("USD")
                prop(CryptoPrice::amount).isEqualTo(BigDecimal(d))
            }

            assertEquals(price, price.toString().toPrice(""), "toPrice('')")
            assertEquals(
                price, price.toJson("test").toPrice("test"),
                "toPrice(test)"
            )
        }
    }
}
