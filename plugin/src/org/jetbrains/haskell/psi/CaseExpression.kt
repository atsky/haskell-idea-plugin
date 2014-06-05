package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiElement


/**
 * Created by atsky on 10/04/14.
 */
public class CaseExpression(node : ASTNode) : ASTWrapperPsiElement(node)