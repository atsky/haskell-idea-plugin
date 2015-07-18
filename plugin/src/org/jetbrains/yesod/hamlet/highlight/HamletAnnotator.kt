package org.jetbrains.yesod.hamlet.highlight

/**
 * @author Leyla H
 */

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.PsiElement
import org.jetbrains.yesod.hamlet.psi.*
import org.jetbrains.yesod.hamlet.psi.String

public class HamletAnnotator : Annotator {
    override fun annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder) {
        if (psiElement is Tag) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(DefaultLanguageHighlighterColors.KEYWORD)
        }
        if (psiElement is Escape) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(DefaultLanguageHighlighterColors.KEYWORD)
        }
        if (psiElement is Doctype) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATOR)
        }
        if (psiElement is Operator) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATOR)
        }
        if (psiElement is Comment) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.COMMENT)
        }
        if (psiElement is Attribute) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.ATTRIBUTE)
        }
        if (psiElement is AttributeValue) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.STRING)
        }
        if (psiElement is Interpolation) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.ATTRIBUTE_VALUE)
        }
        if (psiElement is EndInterpolation) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.ATTRIBUTE_VALUE)
        }
        if (psiElement is String) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.STRING)
        }
        if (psiElement is ColonIdentifier) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.IDENTIFIER)
        }
        if (psiElement is DotIdentifier) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.ATTRIBUTE)
        }
        if (psiElement is SharpIdentifier) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.ATTRIBUTE)
        }
    }
}


