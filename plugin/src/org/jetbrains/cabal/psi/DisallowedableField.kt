package org.jetbrains.cabal.psi

import org.jetbrains.cabal.psi.PropertyField
import com.intellij.lang.ASTNode

public open class DisallowedableField(node: ASTNode) : PropertyField(node) {
    public open fun isEnabled(): String? { return null }
}