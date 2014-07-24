package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.Section

public class Library(node: ASTNode) : ASTWrapperPsiElement(node), Section {


    override public val REQUIRED_FIELD_NAMES = listOf ("exposed-modules")
}
