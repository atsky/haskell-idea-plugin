package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * Created by atsky on 4/25/14.
 */
public open class Expression(node : ASTNode) : ASTWrapperPsiElement(node) {

    public fun getVisibleValues() : List<Name> {
        var current : PsiElement = this.getParent()!!;
        if (current is Expression) {
            return (current as Expression).getVisibleValues()
        } else {
            while (!(current is Module) && !(current is PsiFile)) {
                current = current.getParent()!!
            }
            val declarationList = (current as Module).getValueDeclarationList()
            return declarationList.map { it.getNames().head!! };
        }
    }
}
