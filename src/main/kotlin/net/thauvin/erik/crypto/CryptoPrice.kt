/*
 * CryptoPrice.kt
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

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONStringer
import java.io.IOException
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.util.*

/**
 * Retrieves and holds a cryptocurrency price.
 *
 * @constructor Constructs a new [CryptoPrice] object.
 *
 * @param base The cryptocurrency ticker symbol, such as `BTC`, `ETH`, `LTC`, etc.
 * @param currency The fiat currency ISO 4217 code, such as `USD`, `GPB`, `EUR`, etc.
 * @param amount The cryptocurrency price.
 */
open class CryptoPrice(val base: String, val currency: String, val amount: BigDecimal) {
    companion object {
        // Coinbase API URL
        private const val COINBASE_API_URL = "https://api.coinbase.com/v2/"

        /**
         * Converts JSON data object to [CryptoPrice].
         *
         * @param key Specifies the JSON object key the price data is wrapped in.
         */
        @JvmStatic
        @JvmOverloads
        @Throws(CryptoException::class)
        fun String.toPrice(key: String = "data"): CryptoPrice {
            try {
                val json = if (key.isNotBlank()) JSONObject(this).getJSONObject(key) else JSONObject(this)
                with(json) {
                    return CryptoPrice(getString("base"), getString("currency"), getString("amount").toBigDecimal())
                }
            } catch (e: NumberFormatException) {
                throw CryptoException(id = "convert_error", message = "Could not convert amount to number.", cause = e)
            } catch (e: JSONException) {
                throw CryptoException(id = "parse_error", message = "Could not parse price data.", cause = e)
            }
        }

        /**
         * Makes an API call.
         *
         * @param paths The list of path segments for the API URL. For example: `["prices", "BTC-USD", "spot"]`.
         * @param params The map of query parameters.
         */
        @JvmStatic
        @JvmOverloads
        @Throws(CryptoException::class, IOException::class)
        fun apiCall(paths: List<String>, params: Map<String, String> = emptyMap()): String {
            val client = OkHttpClient()
            val httpUrl = COINBASE_API_URL.toHttpUrl().newBuilder().apply {
                paths.forEach {
                    addPathSegment(it)
                }
                params.forEach {
                    addQueryParameter(it.key, it.value)
                }
            }.build()

            val request = Request.Builder().url(httpUrl).build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: throw CryptoException(
                response.code,
                id = "empty_response",
                message = "Empty response."
            )
            try {
                val json = JSONObject(body)
                if (response.isSuccessful) {
                    return body
                } else {
                    val data = json.getJSONArray("errors")
                    throw CryptoException(
                        response.code, data.getJSONObject(0).getString("id"),
                        data.getJSONObject(0).getString("message")
                    )
                }
            } catch (e: JSONException) {
                throw CryptoException(response.code, id = "parse_error", "Could not parse data.", e)
            }
        }

        /**
         * Retrieves the buy price.
         *
         * @param base The cryptocurrency ticker symbol, such as `BTC`, `ETH`, `LTC`, etc.
         * @param currency The fiat currency ISO 4217 code, such as `USD`, `GPB`, `EUR`, etc.
         */
        @JvmStatic
        @JvmOverloads
        @Throws(CryptoException::class, IOException::class)
        fun buyPrice(base: String, currency: String = "USD"): CryptoPrice {
            return apiCall(listOf("prices", "$base-$currency", "buy"), emptyMap()).toPrice()
        }

        /**
         * Prints the current prices for the specified cryptocurrencies.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            args.forEach {
                with(spotPrice(it)) {
                    println("$base:\t" + "%10s".format(toCurrency()))
                }
            }
        }

        /**
         * Retrieves the sell price.
         *
         * @param base The cryptocurrency ticker symbol, such as `BTC`, `ETH`, `LTC`, etc.
         * @param currency The fiat currency ISO 4217 code, such as `USD`, `GPB`, `EUR`, etc.
         */
        @JvmStatic
        @JvmOverloads
        @Throws(CryptoException::class, IOException::class)
        fun sellPrice(base: String, currency: String = "USD"): CryptoPrice {
            return apiCall(listOf("prices", "$base-$currency", "sell"), emptyMap()).toPrice()
        }

        /**
         * Retrieves the spot price.
         *
         * @param base The cryptocurrency ticker symbol, such as `BTC`, `ETH`, `LTC`, etc.
         * @param currency The fiat currency ISO 4217 code, such as `USD`, `GPB`, `EUR`, etc.
         * @param date The [LocalDate] for historical price data.
         */
        @JvmStatic
        @JvmOverloads
        @Throws(CryptoException::class, IOException::class)
        fun spotPrice(base: String, currency: String = "USD", date: LocalDate? = null): CryptoPrice {
            val params = if (date != null) mapOf("date" to "$date") else emptyMap()
            return apiCall(listOf("prices", "$base-$currency", "spot"), params).toPrice()
        }
    }

    /**
     * Returns the [amount] as a currency formatted string.
     *
     * For example: `$1,203.33`.
     *
     * @param locale The desired locale.
     * @param minFractionDigits The minimum number of digits allowed in the fraction portion of the currency.
     */
    @JvmOverloads
    @Throws(IllegalArgumentException::class)
    fun toCurrency(locale: Locale = Locale.getDefault(Locale.Category.FORMAT), minFractionDigits: Int = 2): String {
        return NumberFormat.getCurrencyInstance(locale).let {
            it.currency = Currency.getInstance(currency)
            it.minimumFractionDigits = minFractionDigits
            it.format(amount)
        }
    }

    /**
     * Indicates whether some other object is _equal to_ this one.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CryptoPrice

        if (base != other.base) return false
        if (currency != other.currency) return false
        if (amount != other.amount) return false

        return true
    }

    /**
     * Returns a hash code value for the object.
     */
    override fun hashCode(): Int {
        var result = base.hashCode()
        result = 31 * result + currency.hashCode()
        result = 31 * result + amount.hashCode()
        return result
    }

    /**
     * Returns a JSON representation of the [CryptoPrice].
     *
     * For example, with the default `data` object key:
     *
     * ```
     * {"data":{"base":"BTC","currency":"USD","amount":"58977.17"}}
     * ```
     *
     * @param key Specifies a JSON object key to wrap the price data in.
     */
    @JvmOverloads
    fun toJson(key: String = "data"): String {
        val json = JSONStringer()
        if (key.isNotBlank()) json.`object`().key(key)
        json.`object`()
            .key("base").value(base)
            .key("currency").value(currency)
            .key("amount").value(amount.toString())
            .endObject()
        if (key.isNotBlank()) json.endObject()
        return json.toString()
    }

    /**
     * Returns a JSON representation of the [CryptoPrice].
     *
     * For example:
     *
     * ```
     * {"base":"BTC","currency":"USD","amount":"58977.17"}
     * ```
     *
     * @see [toJson]
     */
    override fun toString(): String = toJson("")
}
