package org.jetbrains.haskell.highlight

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.parser.lexer.*

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

    class object {
        val PAIRS: Array<BracePair> = array<BracePair>(
                BracePair(org.jetbrains.haskell.parser.token.LEFT_PAREN, org.jetbrains.haskell.parser.token.RIGHT_PAREN, true),
                BracePair(org.jetbrains.haskell.parser.token.LEFT_BRACE, org.jetbrains.haskell.parser.token.RIGHT_BRACE, true),
                BracePair(org.jetbrains.haskell.parser.token.LEFT_BRACKET, org.jetbrains.haskell.parser.token.RIGHT_BRACKET, true))
    }
}
