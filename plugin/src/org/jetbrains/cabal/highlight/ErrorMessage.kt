package org.jetbrains.cabal.highlight

import com.intellij.psi.PsiElement

public class ErrorMessage(errorPlace: PsiElement, errorText: String) {
    val text: String = errorText
    val place: PsiElement = errorPlace
}