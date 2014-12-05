package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement

/**
 * Created by atsky on 05/12/14.
 */
public class UnguardedRHS(node: ASTNode) : ASTWrapperPsiElement(node)