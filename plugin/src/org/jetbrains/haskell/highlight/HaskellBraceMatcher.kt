package org.jetbrains.haskell.highlight

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.parser.lexer.*
import org.jetbrains.grammar.HaskellLexerTokens

public class HaskellBraceMatcher() : PairedBraceMatcher {

    public override fun getPairs(): Array<BracePair> {
        return PAIRS
    }

    public override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean {
        return true
    }

    public override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int {
        return openingBraceOffset
    }

    companion object {
        val PAIRS: Array<BracePair> = arrayOf(BracePair(HaskellLexerTokens.OPAREN, HaskellLexerTokens.CPAREN, true),
                BracePair(HaskellLexerTokens.OCURLY, HaskellLexerTokens.CCURLY, true),
                BracePair(HaskellLexerTokens.OBRACK, HaskellLexerTokens.CBRACK, true))
    }
}
