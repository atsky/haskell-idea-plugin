package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.cabal.parser.CabalTokelTypes
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType

public class FullCondition(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    private fun checkBrackets(): String? {
        var currElement: PsiElement? = getFirstChild()

        if (currElement?.getNode()?.getElementType() == TokenType.NEW_LINE_INDENT) return null

        fun toNext() {
            currElement = currElement?.getNextSibling()
        }

        fun getCurrType() = currElement?.getNode()?.getElementType()

        fun skipWhites() {
            while (getCurrType() == TokenType.WHITE_SPACE) { toNext() }
        }

        fun checkFromCurr(): Boolean {
            while (currElement != null) {
                skipWhites()
                when (getCurrType()) {
                    CabalTokelTypes.OPEN_PAREN -> {
                        toNext()
                        if (!checkFromCurr()) return false
                        skipWhites()
                        if (getCurrType() != CabalTokelTypes.CLOSE_PAREN) return false
                        toNext()
                    }
                    CabalTokelTypes.NEGATION         -> toNext()
                    CabalTokelTypes.SIMPLE_CONDITION -> {
                        toNext()
                        skipWhites()
                        when (getCurrType()) {
                            CabalTokelTypes.CLOSE_PAREN -> return true
                            TokenType.NEW_LINE_INDENT   -> return true
                            null                        -> return true
                            CabalTokelTypes.LOGIC       -> toNext()
                            else                        -> return false
                        }
                    }
                    TokenType.NEW_LINE_INDENT -> return false
                    else -> return false
                }
            }
            return true
        }


        return if (checkFromCurr()) null else "invalid bracket structure"

    }

    public override fun isValidValue(): String? {
        val invalidParts = PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass<InvalidConditionPart>())
        for (part in invalidParts) {
            if (part.isValidValue() == null) return "invalid condition expression"
        }
        if (invalidParts.size == 0) {
            return checkBrackets()
        }
        return null
    }
}
