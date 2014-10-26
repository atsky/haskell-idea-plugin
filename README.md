haskell-idea-plugin
===================

IntelliJ IDEA plugin for Haskell, based on [ideah](https://code.google.com/p/ideah/).

http://plugins.jetbrains.com/plugin/7453?pr=idea

You can download latest build manually from BuildServer:
(http://teamcity.jetbrains.com/viewType.html?buildTypeId=Haskell_Build&guest=1)

For now provides basic features:
* Haskell syntax highlight
* Cabal syntax highlight
* Error checking with [ghc-modi](http://hackage.haskell.org/package/ghc-mod).
* Simple completion based on [ghc-modi](http://hackage.haskell.org/package/ghc-mod)
* Show type of symbol (Ctrl + I, or ⌘ + I on mac)
* Build of cabal projects
* Installation of cabal packages
* Graphical debugger
* REPL

### Plans

* add ability jump to hackage libraries source code.
* support of another products based on IntelliJ platform.
* GHCi support


## Installation

You can install plugin from idea by going to `Preferences` -> `Plugins` and pressing `Browse repositories...`.

To normal work this plugin need:
* [Haskell platform](http://www.haskell.org/platform/)
* cabal must be in `PATH`
* cabal package `ghc-mod`
* cabal package `remote-debugger` - if you want to use debugger

## Source compilation

To normal compilation you will need:
* IDEA 13.1
* Kotlin plugin
* JDK 6
* IntelliJ IDEA Plugin SDK

## Acknowledgments
I thank the following people for their help:
* Vladislav Polyansky, Marat Habibullin for development of debugger support.
* Anna Yaveyn for development of cabal support. 