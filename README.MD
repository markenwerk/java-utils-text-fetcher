# A text stream fetching helper for Java

[![Build Status](https://travis-ci.org/markenwerk/java-utils-text-fetcher.svg?branch=master)](https://travis-ci.org/markenwerk/java-utils-text-fetcher)
[![Coverage Status](https://coveralls.io/repos/markenwerk/java-utils-text-fetcher/badge.svg?branch=master&service=github)](https://coveralls.io/github/markenwerk/java-utils-text-fetcher?branch=master)
[![Dependency Status](https://www.versioneye.com/user/projects/573f0f7ece8d0e00360bd096/badge.svg)](https://www.versioneye.com/user/projects/573f0f7ece8d0e00360bd096)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.markenwerk/utils-text-fetcher/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.markenwerk/utils-text-fetcher)
[![Issues](https://img.shields.io/github/issues/markenwerk/java-utils-text-fetcher.svg)](https://github.com/markenwerk/java-utils-text-fetcher/issues)
[![MIT License](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/markenwerk/java-utils-text-fetcher/blob/master/LICENSE)

## Overview

This is a simple helper to fetch the complete content of an readable.

Consult the [documentation](http://markenwerk.github.io/java-utils-text-fetcher/index.html) and  the [usage description](#usage) for further information:

- [Fetching text streams](#fetching-text-streams)
- [Automatic closing of supplied streams](#automatic-closing-of-supplied-streams)
- [Listen to fetch progress](#listen-to-fetch-progress)
- [Handling of `null` arguments](#handling-of-null-arguments)
- [Customizability](#customizability)

## Maven

This library is hosted in the [Maven Central Repository](https://maven-badges.herokuapp.com/maven-central/net.markenwerk/utils-text-fetcher). You can use it with the following coordinates:

```xml
<dependency>
	<groupId>net.markenwerk</groupId>
	<artifactId>utils-text-fetcher</artifactId>
	<version>1.0.1</version>
</dependency>
```

## Motivation

Copying the complete content of an [`Readable`][Readable] into a `char[]`, [`String`][String] or into an [`Appendable`][Appendable] is a menial task, that has to be dealt with very commonly. While this is certainly not a difficult challenge for any programmer, it is still boring and annoying to do it over and over again, and – as pretty much everything – prone to be erroneously, if done in a hurry.

There are common solutions, beside coding it over and over again. A solution to accomplishing this simple task, could be to bundle a major library like [Commons IO](http://commons.apache.org/proper/commons-io/). But doing so may appear like overkill, especially if no other functionality of the library is likely to be used.

This library provides a simple and lightweight alternative.
 
## Usage

### Fetching text streams

This library provides the [`TextFetcher`][TextFetcher] which features three main functionalities to handle `char` streams:

```java
// create a simple TextFetcher
TextFetcher fetcher = new BufferedTextFetcher();

// copy the content of an Readable into a byte[]
char[] characters = fetcher.fetch(inputStream);

// copy the content of an Readable into a String
String string = fetcher.read(inputStream);

// copy the content of an Readable into an Appendable
fetcher.fetch(reader, writer);
```

### Automatic closing of supplied streams

All methods have optional `boolean` parameters, that can be used to instruct the [`TextFetcher`][TextFetcher] to close the supplied streams. Doing so allows to write compact code like:

```java
foo.setContent(new BufferedTextFetcher().fetch(new FileReader(file), true));
```

Otherwise, it would be necessary to write unnecessarily verbose code like:

```java
Reader in = new FileReader(file);
foo.setContent(new BuffereddataFetcher().fetch(in));
in.close();
```

### Listen to fetch progress

All methods optionally take a [`TextFetchProgressListener`][TextFetchProgressListener] that gets notified on various points in the lifecycle (`started`, `progressed`, `succeeded` or `failed`, `finished`) of a fetch operation. The [`IdleTextFetchProgressListener`][IdleTextFetchProgressListener] is a convenient base implementation with empty methods.

```java
foo.setContent(new BufferedTextFetcher().fetch(
	new FileInputStream(file),
	new IdleTextFetchProgressListener() {
		public void onProgress(long charactersFetched) {
			System.out.println(charactersFetched + " characters fetched so far.");
		}
	},
	true
);
```

### Handling of `null` arguments

Missing or invalid arguments are handled gracefully with the following behaviour:

 - If `null` is given as the [`Readable`][Readable], it is simply ignored and handled as if there was nothing to read. That means, nothing is written to the [`Appendable`][Appendable] and, if requested, the [`Appendable`][Appendable] will be closed. 
 - If `null` is given as the [`Appendable`][Appendable], it is simply ignored, but the content of the given [`Readable`][Readable] is fetched anyway. If requested, the [`Readable`][Readable] be closed. 
 
### Customizability
 
This library provides [`BufferedTextFetcher`][BufferedTextFetcher] as the simplest possible implementations of [`TextFetcher`][TextFetcher]. It eagerly allocates a `char[]` during construction and uses it as a buffer to perform fetch operations by sequentially reading from the [`Readable`][Readable] into the buffer and then writing from the buffer to the [`Appendable`][Appendable]. It is not threadsafe.

Variations of the buffered copying strategy (e.g. lazy buffer creation, threadsafeness) or completely other copying strategies can easily be implemented by extending [`AbstractTextFetcher`][AbstractTextFetcher] or [`AbstractBufferedTextFetcher`][AbstractBufferedTextFetcher] respectively.

[`AbstractTextFetcher`][AbstractTextFetcher] reduces all methods from the [`TextFetcher`][TextFetcher] interface to the single method `doCopy(Reader, Writer, TextFetchProgressListener)` where every parameter is guaranteed to be not `null`, and therefore greatly simplifies the implementation of new fetch strategies. [`AbstractBufferedTextFetcher`][AbstractBufferedTextFetcher] is such an implementation of the aforementioned buffering fetch strategy that handles the sequentially write-read cycles and notifies the [`TextFetchProgressListener`][TextFetchProgressListener] accordingly, but leaves the buffer allocation strategy to be specified.


[AbstractBufferedTextFetcher]:  http://markenwerk.github.io/java-utils-text-fetcher/index.html?net/markenwerk/utils/text/fetcher/AbstractBufferedTextFetcher.html
[AbstractTextFetcher]: http://markenwerk.github.io/java-utils-text-fetcher/index.html?net/markenwerk/utils/text/fetcher/AbstractTextFetcher.html
[BufferedTextFetcher]: http://markenwerk.github.io/java-utils-text-fetcher/index.html?net/markenwerk/utils/text/fetcher/BufferedTextFetcher.html
[TextFetcher]: http://markenwerk.github.io/java-utils-text-fetcher/index.html?net/markenwerk/utils/text/fetcher/TextFetcher.html
[TextFetchProgressListener]: http://markenwerk.github.io/java-utils-text-fetcher/index.html?net/markenwerk/utils/text/fetcher/TextFetchProgressListener.html
[IdleTextFetchProgressListener]: http://markenwerk.github.io/java-utils-text-fetcher/index.html?net/markenwerk/utils/text/fetcher/IdleTextFetchProgressListener.html

[Appendable]: http://docs.oracle.com/javase/8/docs/api/index.html?java/lang/Appendable.html
[Readable]: http://docs.oracle.com/javase/8/docs/api/index.html?java/lang/Readable.html
[String]: http://docs.oracle.com/javase/8/docs/api/index.html?java/lang/String.html
