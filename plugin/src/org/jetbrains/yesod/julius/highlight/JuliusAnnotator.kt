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

class JuliusAnnotator : Annotator {
    override fun annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder) {

        if (psiElement is Keyword) {
            annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = DefaultLanguageHighlighterColors.KEYWORD
        }
        if (psiElement is String) {
            annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = JuliusColors.STRING
        }
        if (psiElement is Comment) {
            annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = JuliusColors.COMMENT
        }
        if (psiElement is DotIdentifier) {
            annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = JuliusColors.DOT_IDENTIFIER
        }
        if (psiElement is Number) {
            annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = JuliusColors.NUMBER
        }
        if (psiElement is Interpolation) {
            annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = JuliusColors.INTERPOLATION
        }
    }
}