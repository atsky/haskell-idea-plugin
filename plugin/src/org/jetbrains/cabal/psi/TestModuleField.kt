package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.DisallowedableField
import org.jetbrains.cabal.parser.CabalTokelTypes

public class TestModuleField(node: ASTNode) : DisallowedableField(node) {

    public override fun isEnabled(): String? {
        val parent = getParent()
        if (parent is TestSuite) {
            val sectType = parent.getField(CabalTokelTypes.TEST_SUITE_TYPE)
            if ((sectType == null) || (sectType.getLastValue() == "detailed-1.0")) return null
            return "test-module field disallowed with such test suit type"
        }
        return null
    }

//    public override fun isEnabled(): String? {
//        val parent = (this : PsiElement).getParent()!! as SourceRepo
//        val repoType = parent.getRepoType()
//        if ((repoType == null) || (repoType == "cvs")) return null
//        return "module field disallowed with cvs repository type"
//    }
}
