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
                BracePair(LEFT_PAREN, RIGHT_PAREN, true),
                BracePair(LEFT_BRACE, RIGHT_BRACE, true),
                BracePair(LEFT_BRACKET, RIGHT_BRACKET, true))
    }
}
