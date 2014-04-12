package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.haskell.parser.ElementFactory

/**
 * Created by atsky on 4/11/14.
 */
public class DataDeclaration(node : ASTNode) : Declaration(node) {

    class object : ElementFactory {
        override fun create(node: ASTNode) = DataDeclaration(node)
    }

    override fun getDeclarationName(): String? {
        return findChildByClass(javaClass<Name>())?.getText()
    }

    fun getConstructorDeclarationList() : List<ConstructorDeclaration> =
        PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())


}