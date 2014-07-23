package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.parser.Section

public class Flag(node: ASTNode) : ASTWrapperPsiElement(node), Section {
}
