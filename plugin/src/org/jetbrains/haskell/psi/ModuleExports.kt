package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Created by atsky on 4/11/14.
 */
class ModuleExports(node: ASTNode) : ASTWrapperPsiElement(node)