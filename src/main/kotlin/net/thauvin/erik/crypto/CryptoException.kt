/*
 * CryptoExtension.kt
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

/**
 * Thrown when an exceptional condition has occurred.
 */
@Suppress("unused")
class CryptoException : Exception {
    var statusCode = NO_STATUS

    /** Constructs a new exception with the specified status code, message and cause. */
    constructor(statusCode: Int = NO_STATUS, message: String, cause: Throwable) : super(message, cause) {
        this.statusCode = statusCode
    }

    /** Constructs a new exception with the specified status code and message. */
    constructor(statusCode: Int = NO_STATUS, message: String) : super(message) {
        this.statusCode = statusCode
    }

    /** Constructs a new exception with the specified status code and cause. */
    constructor(statusCode: Int = NO_STATUS, cause: Throwable) : super(cause) {
        this.statusCode = statusCode
    }

    companion object {
        const val NO_STATUS = -1

        private const val serialVersionUID = 1L
    }
}
