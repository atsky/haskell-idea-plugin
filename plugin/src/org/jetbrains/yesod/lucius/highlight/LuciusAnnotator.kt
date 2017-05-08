package org.jetbrains.yesod.lucius.highlight

/**
 * @author Leyla H
 */

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.PsiElement
import org.jetbrains.yesod.lucius.psi.*
import org.jetbrains.yesod.lucius.psi.Function
import org.jetbrains.yesod.lucius.psi.Number
import org.jetbrains.yesod.lucius.psi.String


class LuciusAnnotator : Annotator {

        override fun annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder) {
            if (psiElement is Function) {
                annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = LuciusColors.ATTRIBUTE
            }
            if (psiElement is String) {
                annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = LuciusColors.STRING
            }
            if (psiElement is AtRule) {
                annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = LuciusColors.AT_RULE
            }
            if (psiElement is Comment) {
                annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = LuciusColors.COMMENT
            }
            if (psiElement is Attribute) {
                annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = DefaultLanguageHighlighterColors.KEYWORD
            }
            if (psiElement is ColonIdentifier) {
                annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = LuciusColors.COLON_IDENTIFIER
            }
            if (psiElement is SharpIdentifier) {
                annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = LuciusColors.SHARP_IDENTIFIER
            }
            if (psiElement is CCIdentifier) {
                annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = LuciusColors.CC_IDENTIFIER
            }
            if (psiElement is DotIdentifier) {
                annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = LuciusColors.DOT_IDENTIFIER
            }
            if (psiElement is Number) {
                annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = LuciusColors.NUMBER
            }
            if (psiElement is Interpolation) {
                annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = LuciusColors.INTERPOLATION
            }
            if (psiElement is Hyperlink) {
                annotationHolder.createInfoAnnotation(psiElement, null).textAttributes = DefaultLanguageHighlighterColors.METADATA
            }
        }
}