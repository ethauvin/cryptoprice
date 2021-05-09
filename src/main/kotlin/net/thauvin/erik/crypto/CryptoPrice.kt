/*
 * CryptoPrice.kt
 *
 * Copyright (c) 2021, Erik C. Thauvin (erik@thauvin.net)
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
import org.json.JSONObject
import java.time.LocalDate

/**
 * The `CryptoPrice` class
 */
open class CryptoPrice(val base: String, val currency: String, val amount: Double) {
    companion object {
        // Coinbase API URL
        private const val COINBASE_API_URL = "https://api.coinbase.com/v2/"

        @JvmStatic
        @Throws(CryptoException::class)
        fun String.toPrice(): CryptoPrice {
            val json = JSONObject(this)
            if (json.has("data")) {
                with(json.getJSONObject("data")) {
                    return CryptoPrice(
                            getString("base"), getString("currency"), getString("amount").toDouble()
                    )
                }
            } else {
                throw CryptoException(message = "Missing JSON data.")
            }
        }

        /**
         * Make an API call.
         */
        @JvmStatic
        @JvmOverloads
        @Throws(CryptoException::class)
        fun apiCall(paths: List<String>, params: Map<String, String> = emptyMap()): String {
            val client = OkHttpClient()
            val url = COINBASE_API_URL.toHttpUrl().newBuilder()
            paths.forEach {
                url.addPathSegment(it)
            }
            params.forEach {
                url.addQueryParameter(it.key, it.value)
            }

            val request = Request.Builder().url(url.build()).build()
            val response = client.newCall(request).execute()
            val body = response.body?.string()
            if (body != null) {
                val json = JSONObject(body)
                if (!response.isSuccessful) {
                    if (json.has("errors")) {
                        val data = json.getJSONArray("errors")
                        throw CryptoException(response.code, data.getJSONObject(0).getString("message"))
                    } else {
                        throw CryptoException(response.code, "Invalid API response.")
                    }
                } else {
                    return body
                }
            } else {
                throw CryptoException(response.code, "Empty API response.")
            }
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val price = marketPrice("BTC")
            println("BTC: ${price.amount}")
        }

        /**
         * Retrieves the current market price.
         */
        @JvmStatic
        @JvmOverloads
        @Throws(CryptoException::class)
        fun marketPrice(base: String, currency: String = "USD", date: LocalDate? = null): CryptoPrice {
            val params = if (date != null) mapOf("date" to "$date") else emptyMap()
            val body = apiCall(listOf("prices", "$base-$currency", "spot"), params)
            return body.toPrice()
        }
    }
}
