package org.jetbrains.cabal.parser

import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.HaskellLanguage
import org.jetbrains.annotations.NonNls
import org.jetbrains.cabal.CabalLanguage
import com.intellij.psi.PsiElement
import com.intellij.lang.ASTNode

public class CabalCompositeElementType(val myDebugName: String, val contructor : (ASTNode) -> PsiElement) : IElementType(myDebugName, CabalLanguage.INSTANCE) {

    public fun getDebugName(): String {
        return myDebugName
    }

}
