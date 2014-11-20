package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

/**
 * Created by atsky on 20/11/14.
 */
public class StringLiteral(node : ASTNode) : ASTWrapperPsiElement(node)