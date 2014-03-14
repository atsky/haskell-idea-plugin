package org.jetbrains.haskell.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

/**
 * @author Evgeny.Kurbatsky
 */
public class Module(node : ASTNode) : ASTWrapperPsiElement(node) {

    public fun getImportDecl() : Array<Import> {
        return findChildrenByClass(javaClass<Import>())
    }

}