[![License (3-Clause BSD)](https://img.shields.io/badge/license-BSD%203--Clause-blue.svg?style=flat-square)](https://opensource.org/licenses/BSD-3-Clause)
[![Kotlin](https://img.shields.io/badge/kotlin-2.1.0-7f52ff)](https://kotlinlang.org/)
[![bld](https://img.shields.io/badge/2.1.0-FA9052?label=bld&labelColor=2392FF)](https://rife2.com/bld)
[![Release](https://img.shields.io/github/release/ethauvin/cryptoprice.svg)](https://github.com/ethauvin/cryptoprice/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/net.thauvin.erik/cryptoprice)](https://central.sonatype.com/artifact/net.thauvin.erik/cryptoprice)
[![Nexus Snapshot](https://img.shields.io/nexus/s/net.thauvin.erik/cryptoprice?label=snapshot&server=https%3A%2F%2Foss.sonatype.org%2F)](https://oss.sonatype.org/content/repositories/snapshots/net/thauvin/erik/cryptoprice/)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ethauvin_cryptoprice&metric=alert_status)](https://sonarcloud.io/dashboard?id=ethauvin_cryptoprice)
[![GitHub CI](https://github.com/ethauvin/cryptoprice/actions/workflows/bld.yml/badge.svg)](https://github.com/ethauvin/cryptoprice/actions/workflows/bld.yml)
[![CircleCI](https://circleci.com/gh/ethauvin/cryptoprice/tree/master.svg?style=shield)](https://circleci.com/gh/ethauvin/cryptoprice/tree/master)

# Retrieve cryptocurrencies current (buy, sell or spot) prices

A simple implementation of the prices [Coinbase Public API](https://docs.cdp.coinbase.com/coinbase-app/docs/api-prices).

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
 - View [bld](https://github.com/ethauvin/cryptoprice/blob/master/examples/bld) or [Gradle](https://github.com/ethauvin/cryptoprice/blob/master/examples/gradle) Examples.

### bld

To use with [bld](https://rife2.com/bld), include the following dependency in your [build](https://github.com/ethauvin/cryptoprice/blob/master/examples/bld/src/bld/java/com/example/CryptoPriceExampleBuild.java) file:

```java
repositories = List.of(MAVEN_CENTRAL);

scope(compile)
    .include(dependency("net.thauvin.erik:cryptoprice:1.0.2"));
```
Be sure to use the [bld Kotlin extension](https://github.com/rife2/bld-kotlin) in your project.

### Gradle, Maven, etc.

To use with [Gradle](https://gradle.org/), include the following dependency in your [build](https://github.com/ethauvin/cryptoprice/blob/master/examples/gradle/build.gradle.kts) file:

```gradle
repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") } // only needed for SNAPSHOT
}

dependencies {
    implementation("net.thauvin.erik:cryptoprice:1.0.2")
}
```

Instructions for using with Maven, Ivy, etc. can be found on [Maven Central](https://central.sonatype.com/artifact/net.thauvin.erik/cryptoprice).

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
The parameter names match the [Coinbase API](https://docs.cdp.coinbase.com/coinbase-app/docs/api-prices).

#### Format

To display the amount as a formatted currency, use the `toCurrency` function:

```kotlin
val euro = CryptoPrice("BTC", "EUR", 23456.78.toBigDecimal())
println(euro.toCurrency()) // €23,456.78

val krone = CryptoPrice("BTC", "DKK", 123456.78.toBigDecimal())
println(krone.toCurrency(Locale.Builder().setLanguage("da").setRegion("DK").build())) // 123.456,78 kr.
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

The `data` object matches the [Coinbase API](https://docs.cdp.coinbase.com/coinbase-app/docs/api-prices). To specify a different (or no) key, use:

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

A generic `apiCall()` function is available to access other [data API endpoints](https://docs.cdp.coinbase.com/coinbase-app/docs/api-currencies). For example to retrieve the [exchange rates](https://docs.cdp.coinbase.com/coinbase-app/docs/api-exchange-rates):

```kotlin
apiCall(listOf("exchange-rates"), mapOf("currency" to "usd"))
```
will return something like:

```json
{"data":{"currency":"BTC","rates":{"AED":"36.73","AFN":"589.50","...":"..."}}}
```

See the [examples](https://github.com/ethauvin/cryptoprice/blob/master/examples/) for more details.

## Contributing

If you want to contribute to this project, all you have to do is clone the GitHub
repository:

```console
git clone git@github.com:ethauvin/cryptoprice.git
```

Then use [bld](https://rife2.com/bld) to build:

```console
cd cryptoprice
./bld compile
```

The project has an [IntelliJ IDEA](https://www.jetbrains.com/idea/) project structure. You can just open it after all
the dependencies were downloaded and peruse the code.
