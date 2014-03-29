package org.jetbrains.haskell.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil

/**
 * @author Evgeny.Kurbatsky
 */
public class Module(node : ASTNode) : ASTWrapperPsiElement(node) {

    public fun getImportList(): List<Import> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass<Import>())
    }

}