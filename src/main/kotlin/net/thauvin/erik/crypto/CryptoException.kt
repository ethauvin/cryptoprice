/*
 * CryptoException.kt
 *
 * Copyright 2021-2026 Erik C. Thauvin (erik@thauvin.net)
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

import net.thauvin.erik.crypto.CryptoException.Companion.NO_STATUS


/**
 * Represents an error returned by the Coinbase API or encountered while
 * processing cryptocurrency data.
 *
 * Includes:
 * - [statusCode]: Optional HTTP status code (or [NO_STATUS] if not applicable)
 * - [id]: Coinbase error identifier (e.g., `"not_found"`, `"invalid_request"`)
 *
 * The exception message provides the human‑readable description.
 */
class CryptoException @JvmOverloads constructor(
    val statusCode: Int = NO_STATUS,
    val id: String,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    companion object {
        /** Indicates that no HTTP status code is associated with the error. */
        const val NO_STATUS = -1

        @Suppress("unused")
        private const val serialVersionUID = 1L
    }

    /**
     * Returns a structured representation of the exception for debugging.
     */
    override fun toString(): String =
        "CryptoException(statusCode=$statusCode, id='$id', message='${message}')"
}
