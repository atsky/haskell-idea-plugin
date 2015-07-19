package org.jetbrains.yesod.julius.highlight

/**
 * @author Leyla H
 */

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.PsiElement
import org.jetbrains.yesod.julius.psi.*
import org.jetbrains.yesod.julius.psi.String
import org.jetbrains.yesod.julius.psi.Number

public class JuliusAnnotator : Annotator {
    override fun annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder) {

        if (psiElement is Keyword) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(DefaultLanguageHighlighterColors.KEYWORD)
        }
        if (psiElement is String) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(JuliusColors.STRING)
        }
        if (psiElement is Comment) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(JuliusColors.COMMENT)
        }
        if (psiElement is DotIdentifier) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(JuliusColors.DOT_IDENTIFIER)
        }
        if (psiElement is Number) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(JuliusColors.NUMBER)
        }
        if (psiElement is Interpolation) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(JuliusColors.INTERPOLATION)
        }
    }
}