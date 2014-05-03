package org.jetbrains.haskell.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.PsiElement
import java.util.HashSet
import com.intellij.psi.PsiFile
import java.util.ArrayList
import org.jetbrains.haskell.parser.ElementFactory

/**
 * @author Evgeny.Kurbatsky
 */
public class Module(node : ASTNode) : ASTWrapperPsiElement(node) {

    class object : ElementFactory {

        override fun create(node: ASTNode) = Module(node)

        fun findModule(element: PsiElement) : Module? {
            var topLevel = element

            while (!(topLevel is PsiFile || topLevel is Module)) {
                topLevel = topLevel.getParent()!!
            }
            if (topLevel is Module) {
                return topLevel as Module
            } else {
                return null
            }
        }
    }

    public fun getImportList(): List<Import> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())
    }

    fun getValueDeclarationList() : List<ValueDeclaration> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())
    }

    fun getInstanceDeclarationList() : List<InstanceDeclaration> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())
    }

    fun getClassDeclarationList() : List<ClassDeclaration> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())
    }

    fun getDataDeclarationList() : List<DataDeclaration> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())
    }

    fun getConstructorDeclarationList() : List<ConstructorDeclaration> {
        val list = ArrayList<ConstructorDeclaration>()
        for (declaration in getDataDeclarationList()) {
            list.addAll(declaration.getConstructorDeclarationList())
        }
        return list
    }


}