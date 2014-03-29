package org.jetbrains.haskell.parser

import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.HaskellLanguage
import org.jetbrains.annotations.NonNls

public class HaskellToken(debugName: String) : IElementType(debugName, HaskellLanguage.INSTANCE) {
    public val myName: String = debugName

    override fun toString(): String {
        return "Haskell Token:" + super.toString()
    }
}