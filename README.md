[![License (3-Clause BSD)](https://img.shields.io/badge/license-BSD%203--Clause-blue.svg?style=flat-square)](https://opensource.org/licenses/BSD-3-Clause) [![Release](https://img.shields.io/github/release/ethauvin/cryptoprice.svg)](https://github.com/ethauvin/cryptoprice/releases/latest) [![Maven Central](https://img.shields.io/maven-central/v/net.thauvin.erik/cryptoprice.svg?label=maven%20central&color=blue)](https://search.maven.org/search?q=g:%22net.thauvin.erik%22%20AND%20a:%22cryptoprice%22) <!-- [![Nexus Snapshot](https://img.shields.io/nexus/s/net.thauvin.erik/cryptoprice?server=https%3A%2F%2Foss.sonatype.org%2F)](https://oss.sonatype.org/content/repositories/snapshots/net/thauvin/erik/cryptoprice/) -->

[![Known Vulnerabilities](https://snyk.io/test/github/ethauvin/cryptoprice/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/ethauvin/cryptoprice?targetFile=pom.xml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ethauvin_cryptoprice&metric=alert_status)](https://sonarcloud.io/dashboard?id=ethauvin_cryptoprice) [![GitHub CI](https://github.com/ethauvin/cryptoprice/actions/workflows/gradle.yml/badge.svg)](https://github.com/ethauvin/cryptoprice/actions/workflows/gradle.yml) [![CircleCI](https://circleci.com/gh/ethauvin/cryptoprice/tree/master.svg?style=shield)](https://circleci.com/gh/ethauvin/cryptoprice/tree/master)

# Retrieve cryptocurrencies current (buy, sell or spot) prices

A simple Kotlin/Java/Android implementation of the prices [Coinbase Public API](https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-prices).

## Examples (TL;DR)

```kotlin
import net.thauvin.erik.crypto.CryptoPrice.Companion.buyPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.sellPrice
import net.thauvin.erik.crypto.CryptoPrice.Companion.spotPrice

val btc = spotPrice("BTC") // Bitcoin
println(btc.amount)

val eth = sellPrice("ETH", "EUR") // Ethereum in Euro
println(eth.amount)

val eth = buyPrice("LTC", "GBP") // Litecoin in Pound sterling
println(eth.amount)

```
 - View [Kotlin](https://github.com/ethauvin/cryptoprice/blob/master/examples/src/main/kotlin/com/example/CryptoPriceExample.kt) or [Java](https://github.com/ethauvin/cryptoprice/blob/master/examples/src/main/java/com/example/CryptoPriceSample.java) Examples.

### Gradle, Maven, etc.

To use with [Gradle](https://gradle.org/), include the following dependency in your [build](https://github.com/ethauvin/cryptoprice/blob/master/examples/build.gradle.kts) file:

```gradle
dependencies {
    implementation("net.thauvin.erik:cryptoprice:1.0.0")
}
```

Instructions for using with Maven, Ivy, etc. can be found on [Maven Central](https://search.maven.org/search?q=g:%22net.thauvin.erik%22%20AND%20a:%22cryptoprice%22).

### Prices

The `spotPrice`, `buyPrice` and `sellPrice` functions define the following parameters:

```kotlin
spotPrice(
    base: String, // Required 
    currency: String = "USD",
    date: LocalDate? = null
)

buyPrice(
    base: String, // Required 
    currency: String = "USD"
)

sellPrice(
    base: String, // Required 
    currency: String = "USD"
)
```

Parameters  | Description
:---------- |:------------------------------------------------------------
`base`      | The cryptocurrency ticker symbol (`BTC`, `ETH`, `LTC`, etc.)
`currency`  | The fiat currency ISO 4217 code. (`USD`, `GBP`, `EUR`, etc.)
`date`      | The `LocalDate` for historical price data.

A `CryptoPrice` object is returned defined as follows:

```kotlin
CryptoPrice(val base: String, val currency: String, val amount: BigDecimal)
```
The parameter names match the [Coinbase API](https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-prices).

#### Format

To display the amount as a formatted currency, use the `toCurrency` function:

```kotlin
val euro = CryptoPrice("BTC", "EUR", 23456.78.toBigDecimal())
println(euro.toCurrency()) // €23,456.78

val krone = CryptoPrice("BTC", "DKK", 123456.78.toBigDecimal())
println(krone.toCurrency(Locale("da", "DK"))) // 123.456,78 kr.
```

##### JSON

To convert a `CryptoPrice` object back to JSON, use the `toJson` function:

```kotlin
val price = CryptoPrice("BTC", "USD", 34567.89.toBigDecimal())
println(price.toJson())
```

*output:*

```json
{"data":{"base":"BTC","currency":"USD","amount":"34567.89"}}
```

The `data` object matches the [Coinbase API](https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-prices). To specify a different (or no) key, use:

```kotlin
println(price.toJson("bitcoin"))
println(price.toJson("")) // or price.toString()
```

*output:*

```json
{"bitcoin":{"base":"BTC","currency":"USD","amount":"34567.89"}}
{"base":"BTC","currency":"USD","amount":"34567.89"}
```

Similarly, to create a `CryptoPrice` object from JSON, use the `toPrice` function:

```kotlin
val btc = """{"data":{"base":"BTC","currency":"USD","amount":"34567.89"}}""".toPrice()
val eth = """{"ether":{"base":"ETH","currency":"USD","amount":"2345.67"}}""".toPrice("ether")
```

### Extending

A generic `apiCall()` function is available to access other [data API endpoints](https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-currencies). For example to retrieve the [exchange rates](https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-exchange-rates#get-exchange-rates):

```kotlin
apiCall(listOf("exchange-rates"), mapOf("currency" to "usd"))
```
will return something like:

```json
{"data":{"currency":"BTC","rates":{"AED":"36.73","AFN":"589.50","...":"..."}}}
```

See the [examples](https://github.com/ethauvin/cryptoprice/blob/master/examples/) for more details.
