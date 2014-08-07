package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.DisallowedableField
import org.jetbrains.cabal.parser.CabalTokelTypes
import org.jetbrains.cabal.psi.PropertyField

public class TestModuleField(node: ASTNode) : PropertyField(node), DisallowedableField {

    public override fun isEnabled(): String? {
        val parent = getParent()
        if (parent is TestSuite) {
            val sectType = parent.getField(javaClass<TypeField>())
            if ((sectType == null) || (sectType.getValue().getText() == "detailed-1.0")) return null
            return "test-module field disallowed with such test suit type"
        }
        return null
    }

}
