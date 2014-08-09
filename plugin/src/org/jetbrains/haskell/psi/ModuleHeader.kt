package org.jetbrains.haskell.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.PsiElement
import java.util.HashSet
import com.intellij.psi.PsiFile
import java.util.ArrayList
import com.intellij.psi.tree.TokenSet

/**
 * @author Evgeny.Kurbatsky
 */
public class ModuleHeader(node : ASTNode) : ASTWrapperPsiElement(node) {

    public fun getModuleName(): String? {
        return findChildByClass(javaClass<FqName>())?.getText();
    }


}