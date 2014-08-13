package org.jetbrains.cabal.highlight

import com.intellij.psi.PsiElement

public class ErrorMessage(errorPlace: PsiElement, errorText: String, errorType: String, isAfterNodeError: Boolean = false) {
    val text: String = errorText
    val place: PsiElement = errorPlace
    val severity: String = errorType
    val isAfterNode: Boolean = isAfterNodeError
}