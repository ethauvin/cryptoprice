[![License (3-Clause BSD)](https://img.shields.io/badge/license-BSD%203--Clause-blue.svg?style=flat-square)](http://opensource.org/licenses/BSD-3-Clause) [![Release](https://img.shields.io/github/release/ethauvin/cryptoprice.svg)](https://github.com/ethauvin/cryptoprice/releases/latest) [![Maven Central](https://img.shields.io/maven-central/v/net.thauvin.erik/cryptoprice.svg?label=maven%20central)](https://search.maven.org/search?q=g:%22net.thauvin.erik%22%20AND%20a:%22cryptoprice%22) [![Nexus Snapshot](https://img.shields.io/nexus/s/net.thauvin.erik/cryptoprice?server=https%3A%2F%2Foss.sonatype.org%2F)](https://oss.sonatype.org/content/repositories/snapshots/net/thauvin/erik/cryptoprice/)

[![Known Vulnerabilities](https://snyk.io/test/github/ethauvin/cryptoprice/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/ethauvin/cryptoprice?targetFile=pom.xml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ethauvin_cryptoprice&metric=alert_status)](https://sonarcloud.io/dashboard?id=ethauvin_cryptoprice) [![GitHub CI](https://github.com/ethauvin/cryptoprice/actions/workflows/gradle.yml/badge.svg)](https://github.com/ethauvin/cryptoprice/actions/workflows/gradle.yml) [![CircleCI](https://circleci.com/gh/ethauvin/cryptoprice/tree/master.svg?style=shield)](https://circleci.com/gh/ethauvin/cryptoprice/tree/master)

# Retrieve cryptocurrencies current prices

A simple Kotlin/Java/Android implementation of the spot price [Coinbase Public API](https://developers.coinbase.com/api/v2#get-spot-price).

## Examples (TL;DR)

```kotlin
import net.thauvin.erik.crypto.CryptoPrice.Companion.spotPrice

// ...

val btc = spotPrice("BTC") // Bitcoin
println(btc.amount)

val eth = spotPrice("ETH", "EUR") // Ethereum in Euros
println(eth.amount)

```
 - View [Kotlin](https://github.com/ethauvin/cryptoprice/blob/master/examples/src/main/kotlin/com/example/CryptoPriceExample.kt) or [Java](https://github.com/ethauvin/cryptoprice/blob/master/examples/src/main/java/com/example/CryptoPriceSample.java) Examples.

### Gradle, Maven, etc.

To use with [Gradle](https://gradle.org/), include the following dependency in your [build](https://github.com/ethauvin/cryptoprice/blob/master/examples/build.gradle.kts) file:

```gradle
dependencies {
    implementation("net.thauvin.erik:cryptoprice:0.9.0")
}
```

Instructions for using with Maven, Ivy, etc. can be found on [Maven Central](https://search.maven.org/artifact/net.thauvin.erik/cryptoprice/0.9.0/jar).

### Spot Price

The `spotPrice` function defines the following parameters:

```kotlin
spotPrice(
    base: String, // Required 
    currency: String = "USD",
    date: LocalDate? = null,
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
The parameter names match the [Coinbase API](https://developers.coinbase.com/api/v2#get-spot-price).

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

The `data` object matches the [Coinbase API](https://developers.coinbase.com/api/v2#get-spot-price). To specify a different (or no) key, use:

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

A generic `apiCall()` function is available to access other [API data endpoints](https://developers.coinbase.com/api/v2#data-endpoints). For example to retrieve the current [buy price](https://developers.coinbase.com/api/v2#get-buy-price) of a cryptocurrency:

```kotlin
apiCall(paths = listOf("prices", "BTC-USD", "buy"), params = emptyMap())
```
will return something like:

```json
{"data":{"base":"BTC","currency":"USD","amount":"34554.32"}}
```

See the [examples](https://github.com/ethauvin/cryptoprice/blob/master/examples/) for more details.
