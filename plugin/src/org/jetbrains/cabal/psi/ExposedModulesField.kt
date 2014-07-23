
package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.Field

public class ExposedModulesField(node: ASTNode) : ASTWrapperPsiElement(node), Field {
}
