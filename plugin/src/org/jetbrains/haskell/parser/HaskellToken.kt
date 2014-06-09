package org.jetbrains.haskell.parser

import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.HaskellLanguage
import org.jetbrains.annotations.NonNls
import com.intellij.lang.PsiBuilder
import org.jetbrains.haskell.parser.rules.Rule

public class HaskellToken(debugName: String) : IElementType(debugName, HaskellLanguage.INSTANCE), Rule {
    public val myName: String = debugName

    override fun parse(builder: PsiBuilder): Boolean {
        val elementType = builder.getTokenType()
        if (elementType == this) {
            builder.advanceLexer()
            return true;
        }
        return false;
    }
}