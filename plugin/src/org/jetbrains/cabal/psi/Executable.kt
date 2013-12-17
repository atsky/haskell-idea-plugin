package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

/**
 * @author Evgeny.Kurbatsky
 */
public class Executable(node: ASTNode) : ASTWrapperPsiElement(node) {
    public fun getExecutableName() : String {
        return getChildren()[0].getText()!!
    }
}