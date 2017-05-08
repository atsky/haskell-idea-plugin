package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

/**
 * Created by atsky on 13/02/15.
 */
class Context(node : ASTNode) : ASTWrapperPsiElement(node)