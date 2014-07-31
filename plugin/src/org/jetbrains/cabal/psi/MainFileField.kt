package org.jetbrains.cabal.psi

import org.jetbrains.cabal.psi.DisallowedableField
import org.jetbrains.cabal.parser.CabalTokelTypes
import com.intellij.lang.ASTNode

public class MainFileField(node: ASTNode) : DisallowedableField(node) {

    public override fun isEnabled(): String? {
        val parent = getParent()
        if (parent is TestSuite) {
            val sectType = parent.getField(javaClass<TypeField>())
            if ((sectType == null) || (sectType.getValue().getText() == "exitcode-stdio-1.0")) return null
            return "main-is field disallowed with such test suit type"
        }
        return null
    }
}