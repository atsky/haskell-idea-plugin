package org.jetbrains.haskell.parser

import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.HaskellLanguage
import org.jetbrains.annotations.NonNls
import com.intellij.lang.PsiBuilder
import org.jetbrains.haskell.parser.rules.Rule
import org.jetbrains.haskell.parser.rules.ParserState

public class HaskellToken(debugName: String) : IElementType(debugName, HaskellLanguage.INSTANCE), Rule {
    public val myName: String = debugName

    override fun parse(state: ParserState): Boolean {
        val elementType = state.getTokenType()
        if (elementType == this) {
            state.advanceLexer()
            return true;
        }
        return false;
    }
}