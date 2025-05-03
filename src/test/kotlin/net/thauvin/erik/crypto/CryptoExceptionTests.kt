/*
 * CryptoExceptionTests.kt
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

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test

class CryptoExceptionTests {
    @Test
    fun `Constructor with all parameters`() {
        val statusCode = 404
        val id = "testId"
        val message = "Test error message"
        val cause = RuntimeException("Root cause")

        val exception = CryptoException(statusCode, id, message, cause)

        assertThat(exception.statusCode).isEqualTo(statusCode)
        assertThat(exception.id).isEqualTo(id)
        assertThat(exception.message).isEqualTo(message)
        assertThat(exception.cause).isEqualTo(cause)
    }

    @Test
    fun `Constructor with empty id`() {
        val emptyId = ""
        val message = "Test error message with empty id"
        val cause = RuntimeException("Cause for empty id")

        val exception = CryptoException(id = emptyId, message = message, cause = cause)

        assertThat(exception.statusCode).isEqualTo(CryptoException.NO_STATUS)
        assertThat(exception.id).isEqualTo(emptyId)
        assertThat(exception.message).isEqualTo(message)
        assertThat(exception.cause).isEqualTo(cause)
    }

    @Test
    fun `Constructor with empty message`() {
        val id = "testIdEmptyMessage"
        val emptyMessage = ""
        val cause = IllegalStateException("Cause for empty message")

        val exception = CryptoException(id = id, message = emptyMessage, cause = cause)

        assertThat(exception.statusCode).isEqualTo(CryptoException.NO_STATUS)
        assertThat(exception.id).isEqualTo(id)
        assertThat(exception.message).isEqualTo(emptyMessage)
        assertThat(exception.cause).isEqualTo(cause)
    }

    @Test
    fun `Constructor with null cause`() {
        val statusCode = 500
        val id = "testIdWithStatusNoCause"
        val message = "Test error message, with status, no cause"

        val exception = CryptoException(statusCode = statusCode, id = id, message = message)

        assertThat(exception.statusCode).isEqualTo(statusCode)
        assertThat(exception.id).isEqualTo(id)
        assertThat(exception.message).isEqualTo(message)
        assertThat(exception.cause).isNull()
    }

    @Test
    fun `Constructor without cause`() {
        val id = "testIdNoStatusNoCause"
        val message = "Test error message, no status, no cause"

        val exception = CryptoException(id = id, message = message)

        assertThat(exception.statusCode).isEqualTo(CryptoException.NO_STATUS)
        assertThat(exception.id).isEqualTo(id)
        assertThat(exception.message).isEqualTo(message)
        assertThat(exception.cause).isNull()
    }

    @Test
    fun `Constructor without status`() {
        val id = "testIdNoStatus"
        val message = "Test error message without status"
        val cause = IllegalArgumentException("Another cause")

        val exception = CryptoException(id = id, message = message, cause = cause)

        assertThat(exception.statusCode).isEqualTo(CryptoException.NO_STATUS)
        assertThat(exception.id).isEqualTo(id)
        assertThat(exception.message).isEqualTo(message)
        assertThat(exception.cause).isEqualTo(cause)
    }
}
