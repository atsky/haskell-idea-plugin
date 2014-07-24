package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.Section

/**
 * @author Evgeny.Kurbatsky
 */
public class Executable(node: ASTNode) : ASTWrapperPsiElement(node), Section {
    public fun getExecutableName() : String {
        return getChildren()[0].getText()!!
    }

    public override val REQUIRED_FIELD_NAMES = listOf ("main-is")
}