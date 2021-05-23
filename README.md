[![License (3-Clause BSD)](https://img.shields.io/badge/license-BSD%203--Clause-blue.svg?style=flat-square)](http://opensource.org/licenses/BSD-3-Clause) [![Release](https://img.shields.io/github/release/ethauvin/cryptoprice.svg)](https://github.com/ethauvin/cryptoprice/releases/latest) [![Maven Central](https://img.shields.io/maven-central/v/net.thauvin.erik/cryptoprice.svg?label=maven%20central)](https://search.maven.org/search?q=g:%22net.thauvin.erik%22%20AND%20a:%22cryptoprice%22)

[![Known Vulnerabilities](https://snyk.io/test/github/ethauvin/cryptoprice/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/ethauvin/cryptoprice?targetFile=pom.xml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ethauvin_cryptoprice&metric=alert_status)](https://sonarcloud.io/dashboard?id=ethauvin_cryptoprice) [![GitHub CI](https://github.com/ethauvin/cryptoprice/actions/workflows/gradle.yml/badge.svg)](https://github.com/ethauvin/cryptoprice/actions/workflows/gradle.yml) [![CircleCI](https://circleci.com/gh/ethauvin/cryptoprice/tree/master.svg?style=shield)](https://circleci.com/gh/ethauvin/cryptoprice/tree/master)

# Retrieve cryptocurrencies current market prices

A simple Kotlin/Java/Android implementation of the spot price [Coinbase Public API](https://developers.coinbase.com/api/v2#get-spot-price).

## Examples (TL;DR)

```kotlin
import net.thauvin.erik.crypto.CryptoPrice.Companion.marketPrice

// ...

val btc = marketPrice("BTC") // Bitcoin
println(btc.amount)

val eth = marketPrice("ETH", "EUR") // Ethereum in Euros
println(eth.amount)

```
 - View [Kotlin](https://github.com/ethauvin/cryptoprice/blob/master/examples/src/main/kotlin/com/example/CryptoPriceExample.kt) or [Java](https://github.com/ethauvin/cryptoprice/blob/master/examples/src/main/java/com/example/CryptoPriceSample.java) Examples.

### Market Price

The `marketPrice` function defines the following parameters:

```kotlin
marketPrice(
    base: String, // Required 
    currency: String = "USD",
    date: LocalDate? = null,
)
```

Parameters  | Description
:---------- |:-------------------------------------------------------------
`base`      | The cryptocurrency ticker symbol (`BTC`, `ETH`, `ETH2`, etc.)
`currency`  | The fiat currency ISO 4217 code. (`USD`, `GBP`, `EUR`, etc.)
`date`      | The `LocalDate` for historical price data.

A `CryptoPrice` is returned defined as follows:

```kotlin
CryptoPrice(val base: String, val currency: String, val amount: Double)
```
The parameter names match the [Coinbase API](https://developers.coinbase.com/api/v2#get-spot-price).

### Extending

A generic `apiCall()` function is available to access other [API data endpoints](https://developers.coinbase.com/api/v2#data-endpoints). For example to retried the current [buy price](https://developers.coinbase.com/api/v2#get-buy-price) of a cryptocurrency:

```kotlin
apiCall(paths = listOf("prices", "BTC-USD", "buy"), params = emptyMap())
```
will return something like:

```json
{"data":{"base":"BTC","currency":"USD","amount":"58977.17"}}
```

See the [examples](https://github.com/ethauvin/cryptoprice/blob/master/examples/) for more details.

### Gradle, Maven, etc.

To use with [Gradle](https://gradle.org/), include the following dependency in your [build](https://github.com/ethauvin/cryptoprice/blob/master/examples/build.gradle.kts) file:

```gradle
dependencies {
    implementation("net.thauvin.erik:cryptoprice:0.9.0")
}
```

Instructions for using with Maven, Ivy, etc. can be found on [Maven Central](https://search.maven.org/artifact/net.thauvin.erik/cryptoprice/0.9.0/jar).
