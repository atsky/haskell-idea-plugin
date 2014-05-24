haskell-idea-plugin
===================

IntelliJ IDEA plugin for Haskell, based on [ideah](https://code.google.com/p/ideah/).

For now provides basic features:
* Haskell syntax highlight
* Error checking with [buidwrapper](https://github.com/JPMoresmau/BuildWrapper) to have a lot of cool features for free.
* Simple completion based on [ghc-mod](http://hackage.haskell.org/package/ghc-mod)
* Cabal syntax highlight
* Build of cabal projects
* Installation of cabal packages

### Problems

If you installed new version of cabal through `cabal install cabal-install`
then buildwrapper fails ([issue](https://github.com/JPMoresmau/BuildWrapper/issues/15)).

### Plans

* Add ability jump to hackage libraries source code.

## Installation

You can install plugin from idea by going to `Preferences` -> `Plugins` and pressing `Browse repositories...`.

To normal work this plugin need:
* [Haskell platform](http://www.haskell.org/platform/)
* cabal must be in `PATH`
* ghc-mod (install by `cabal install ghc-mod`)

## Source compilation

To normal compilation you will need:
* IDEA 13
* Kotlin plugin
* JDK 6
* IntelliJ IDEA Plugin SDK