package org.jetbrains.haskell.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.PsiElement
import java.util.HashSet
import com.intellij.psi.PsiFile
import java.util.ArrayList

/**
 * @author Evgeny.Kurbatsky
 */
public class Module(node : ASTNode) : ASTWrapperPsiElement(node) {

    companion object {

        fun findModule(element: PsiElement) : Module? {
            var topLevel = element

            while (!(topLevel is PsiFile || topLevel is Module)) {
                topLevel = topLevel.getParent()!!
            }
            if (topLevel is Module) {
                return topLevel
            } else {
                return null
            }
        }
    }

    public fun getModuleName() : ModuleName? =
            findChildByClass(javaClass<ModuleName>())

    public fun getImportList(): List<Import> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())
    }

    fun getSignatureDeclarationsList() : List<SignatureDeclaration> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())
    }

    fun getForeignDeclarationList() : List<ForeignDeclaration> {
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

    fun getTypeSynonymList() : List<TypeSynonym> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass())
    }

}