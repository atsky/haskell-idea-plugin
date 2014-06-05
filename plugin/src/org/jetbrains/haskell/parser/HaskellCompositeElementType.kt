package org.jetbrains.haskell.parser

import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.HaskellLanguage
import org.jetbrains.annotations.NonNls
import com.intellij.psi.PsiElement
import com.intellij.lang.ASTNode


public open class HaskellCompositeElementType(
        debugName: String,
        public val constructor : ((ASTNode)->PsiElement)? = null) :
                                IElementType(debugName, HaskellLanguage.INSTANCE) {

    private val myDebugName: String = debugName


    public open fun getDebugName(): String {
        return myDebugName
    }
}
