Trident [![Build Status](https://travis-ci.org/TridentSDK/Trident.svg?branch=bleeding-edge)](https://travis-ci.org/TridentSDK/Trident)
=========

The Trident project, the implementation of the new generation of multi-threaded, high-performance, and cleanroom Minecraft servers.

* [Website](https://tridentsdk.net)
* [Chat](https://www.hipchat.com/g20bt22H2?v=2)
* [Issues](https://tridentsdk.atlassian.net/projects/TRD/issues)
* [Tech Document](https://tridentsdk.net/techdoc/)
* [Wiki](https://tridentsdkwiki.atlassian.net/wiki/dashboard.action)
* [Javadoc](https://tridentsdk.github.io/javadocs)
* [Contributing](https://tridentsdkwiki.atlassian.net/wiki/display/DEV/Trident+Development)
* [TridentSDK](https://github.com/TridentSDK/TridentSDK)

## Latest Release ##

- [0.3-alpha DP](https://github.com/TridentSDK/Trident/releases/tag/0.3-alpha-DP)

## Getting the JAR ##

### Method one: Build it yourself ###

If you have decided that our forms of distribution are questionable, or you would like to modify something before getting a JAR file, you want to build from the source directly.

#### Prerequisites ####

1. A [computer](https://en.wikipedia.org/wiki/Computer)
2. [Git](https://git-scm.com/)
3. [Maven](https://maven.apache.org/)
4. [Java 8 JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

#### Directions ####

Type in the following into the command line:

```bash
git clone -b [master|bleeding-edge] https://github.com/TridentSDK/Trident.git
cd Trident
mvn clean install
```

One would use master to ensure compatibility with the latest build of [TridentSDK](https://github.com/TridentSDK/TridentSDK). Otherwise, for the most up-to-date (and potentially breaking) build of Trident, one would use `bleeding-edge`. If you are building the JAR yourself, it is your responsibility to know which one is right for you.

The built JAR will `./Trident/target/trident-*.jar`

### Method two: Download from Sonatype Nexus ###

For a precompiled solution, one which has passed the tests we have wrote, as well as if you are too lazy to download 2 files and install the [Method one](#method-one-build-it-yourself) prerequeisites, you can download one yourself.

#### Prerequisites ####

1. A [mouse](https://en.wikipedia.org/wiki/Mouse_(computing))
2. A [web browser](https://en.wikipedia.org/wiki/Web_browser)
3. A [computer](https://en.wikipedia.org/wiki/Computer)
4. [Java 8 JRE/JDK](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)

#### Directions ####

1. [Click](https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=net.tridentsdk&a=trident&v=0.3-SNAPSHOT&e=jar)
2. Click "Keep"

### Method three: Get one from us ###

If we've released a JAR for you to use, it usually comes with a nice read. So if you like nice reads or the look of our [official website](https://tridentsdk.net), you can go rummage around the releases forum and look for a download link.

#### Prerequisites ####

1. A [mouse](https://en.wikipedia.org/wiki/Mouse_(computing))
2. A [web browser](https://en.wikipedia.org/wiki/Web_browser)
3. A [computer](https://en.wikipedia.org/wiki/Computer)
4. [Java 8 JRE/JDK](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)

#### Directions ####

1. Nagigate to [tridentsdk.net](https://tridentsdk.net)
2. Scroll down the News list to the first release post
3. Find the download link
4. Click the download link
