package org.jetbrains.cabal.psi

import org.jetbrains.cabal.psi.PropertyField
import com.intellij.lang.ASTNode

public trait DisallowedableField : PropertyField {
    public fun isEnabled(): String? { return null }
}