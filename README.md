haskell-idea-plugin
===================

IntelliJ IDEA plugin for Haskell, based on [ideah](https://code.google.com/p/ideah/).

<div>
  <a href="http://teamcity.jetbrains.com/viewType.html?buildTypeId=Haskell_Build&guest=1">
    <img src="https://teamcity.jetbrains.com/app/rest/builds/buildType:(id:Haskell_Build)/statusIcon"/>
  </a>
  <span>Build</span>
</div>


http://plugins.jetbrains.com/plugin/7453?pr=idea

You can download the latest build manually from BuildServer:
(http://teamcity.jetbrains.com/viewType.html?buildTypeId=Haskell_Build&guest=1)

For now the plugin provides these basic features:
* Haskell syntax highlight
* Cabal syntax highlight
* Error checking with [ghc-modi](http://hackage.haskell.org/package/ghc-mod).
* Simple completion based on [ghc-modi](http://hackage.haskell.org/package/ghc-mod)
* Show type of symbol (Ctrl + Shift + T, or ⌘ + Shift + T on mac)
* Build of cabal projects
* Installation of cabal packages
* Graphical debugger
* REPL

### Plans

* add ability to jump to hackage libraries source code.
* support of other products based on the IntelliJ platform.
* GHCi support


## Installation

You can install the plugin from idea by going to `Preferences` -> `Plugins` and pressing `Browse repositories...`.

For it to work normally you will need these plugins:
* [Haskell platform](http://www.haskell.org/platform/)
* cabal must be in `PATH`
* cabal package `ghc-mod`
* cabal package `remote-debugger` - if you want to use debugger

## Source compilation

To compile you will need:
* IDEA 13.1
* Kotlin plugin
* JDK 6
* IntelliJ IDEA Plugin SDK

## Acknowledgments
I thank the following people for their help:
* Vladislav Polyansky, Marat Habibullin for development of debugger support.
* Anna Yaveyn for development of cabal support. 
