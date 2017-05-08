package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil

/**
 * @author Evgeny.Kurbatsky
 * @since 10.09.15.
 */
class ForeignDeclaration(node : ASTNode) : Declaration(node) {

    override fun getDeclarationName(): String? {
        return getQVar()?.text
    }

    fun getQVar(): QVar? =
            findChildByClass(QVar::class.java)
}