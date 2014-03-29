package org.jetbrains.haskell.parser.token

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.psi.Import
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.parser.HaskellCompositeElementType

/**
 * Created by atsky on 3/14/14.
 */


public fun createPsiElement(node: ASTNode): PsiElement {
    val elementType = node.getElementType()
    if (elementType is HaskellCompositeElementType) {
        val constructor = (node as HaskellCompositeElementType).constructor
        if (constructor != null) {
            return constructor(node)
        }
    }

    return ASTWrapperPsiElement(node)
}

