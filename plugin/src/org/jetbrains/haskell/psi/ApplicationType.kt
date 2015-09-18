package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 11/21/14.
 */
public class ApplicationType(node : ASTNode) : HaskellType(node) {
    fun getChildrenTypes() : List<HaskellType> =
            PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellType::class.java)


    override fun getLeftTypeVariable() : TypeVariable? {
        return getChildrenTypes().firstOrNull()?.getLeftTypeVariable();
    }

}