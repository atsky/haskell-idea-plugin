package org.jetbrains.haskell.icons

import com.intellij.openapi.util.IconLoader

import javax.swing.*

/**
 * @author Evgeny.Kurbatsky
 */
object HaskellIcons {
    val HASKELL_BIG: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/haskell24.png")
    val HASKELL: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/haskell16.png")
    val DEFAULT: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/haskell16.png")
    val APPLICATION: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/application.png")
    @JvmField
    val CABAL: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/cabal.png")
    val BIG: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/haskell.png")
    val UPDATE: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/update.png")
    @JvmField val REPL: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/repl.png")
    val HAMLET: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/yesod16.png")
}
