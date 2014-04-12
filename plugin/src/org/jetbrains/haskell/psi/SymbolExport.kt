package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.haskell.parser.ElementFactory

/**
 * Created by atsky on 4/11/14.
 */

public class SymbolExport(node: ASTNode) : ASTWrapperPsiElement(node) {
    class object : ElementFactory {
        override fun create(node: ASTNode) = SymbolExport(node)
    }
}