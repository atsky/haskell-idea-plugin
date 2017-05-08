package org.jetbrains.haskell.parser

import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.HaskellLanguage
import org.jetbrains.annotations.NonNls
import com.intellij.lang.PsiBuilder

class HaskellTokenType(debugName: String) : IElementType(debugName, HaskellLanguage.INSTANCE) {
    val myName: String = debugName

}