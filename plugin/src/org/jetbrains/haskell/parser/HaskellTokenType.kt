package org.jetbrains.haskell.parser

import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.HaskellLanguage
import org.jetbrains.annotations.NonNls
import com.intellij.lang.PsiBuilder

public class HaskellTokenType(debugName: String) : IElementType(debugName, HaskellLanguage.INSTANCE) {
    public val myName: String = debugName

}