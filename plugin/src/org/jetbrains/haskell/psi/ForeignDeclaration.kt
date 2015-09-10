package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil

/**
 * @author Evgeny.Kurbatsky
 * @since 10.09.15.
 */
public class ForeignDeclaration(node : ASTNode) : Declaration(node) {

    override fun getDeclarationName(): String? {
        return getQVar()?.getText()
    }

    fun getQVar(): QVar? =
            findChildByClass(javaClass<QVar>())
}