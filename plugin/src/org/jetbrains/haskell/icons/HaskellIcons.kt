package org.jetbrains.haskell.icons

import com.intellij.openapi.util.IconLoader

import javax.swing.*

/**
 * @author Evgeny.Kurbatsky
 */
public object HaskellIcons {
    public val HASKELL_BIG: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/haskell24.png")
    public val HASKELL: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/haskell16.png")
    public val DEFAULT: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/haskell16.png")
    public val APPLICATION: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/application.png")
    @JvmField
    public val CABAL: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/cabal.png")
    public val BIG: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/haskell.png")
    public val UPDATE: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/update.png")
    @JvmField
    public val REPL: Icon = IconLoader.getIcon("/org/jetbrains/haskell/icons/repl.png")
    public val HAMLET: Icon = IconLoader.findIcon("/org/jetbrains/haskell/icons/hamlet16.png")
}
