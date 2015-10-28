package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.Checkable
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.cabal.parser.CabalTokelTypes
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import org.jetbrains.cabal.highlight.ErrorMessage
import java.util.ArrayList

public class FullCondition(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    public override fun check(): List<ErrorMessage> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ConditionPart::class.java).flatMap { it.checkBrackets() }
    }
}
