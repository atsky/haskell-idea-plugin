package org.jetbrains.cabal.highlight

import com.intellij.psi.PsiElement

public class ErrorMessage(errorText: String, errorPlace: PsiElement) {
    val text: String = errorText
    val place: PsiElement = errorPlace
}