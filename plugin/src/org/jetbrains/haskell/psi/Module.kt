package org.jetbrains.haskell.psi

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
class Module(node : ASTNode) : ASTWrapperPsiElement(node) {

    companion object {

        fun findModule(element: PsiElement) : Module? {
            var topLevel = element

            while (!(topLevel is PsiFile || topLevel is Module)) {
                topLevel = topLevel.parent!!
            }
            if (topLevel is Module) {
                return topLevel
            } else {
                return null
            }
        }
    }

    fun getModuleName() : ModuleName? =
            findChildByClass(ModuleName::class.java)

    fun getImportList(): List<Import> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, Import::class.java)
    }

    fun getSignatureDeclarationsList() : List<SignatureDeclaration> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, SignatureDeclaration::class.java)
    }

    fun getForeignDeclarationList() : List<ForeignDeclaration> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ForeignDeclaration::class.java)
    }

    fun getInstanceDeclarationList() : List<InstanceDeclaration> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, InstanceDeclaration::class.java)
    }

    fun getClassDeclarationList() : List<ClassDeclaration> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ClassDeclaration::class.java)
    }

    fun getDataDeclarationList() : List<DataDeclaration> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, DataDeclaration::class.java)
    }

    fun getTypeSynonymList() : List<TypeSynonym> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, TypeSynonym::class.java)
    }

}