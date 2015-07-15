package org.jetbrains.yesod.hamlet.highlight;

/**
 * @author Leyla H
 */

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiElement;
import org.jetbrains.yesod.hamlet.psi.*;
import org.jetbrains.yesod.hamlet.psi.String;

public class HamletAnnotator implements Annotator {
    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if(psiElement instanceof Tag) {
            annotationHolder.createInfoAnnotation(psiElement, null)
                    .setTextAttributes(DefaultLanguageHighlighterColors.KEYWORD);
        }
        if(psiElement instanceof Backslash) {
            annotationHolder.createInfoAnnotation(psiElement, null)
                    .setTextAttributes(DefaultLanguageHighlighterColors.KEYWORD);
        }
        if(psiElement instanceof Doctype) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATOR);
        }

        if(psiElement instanceof Operator) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATOR);
        }
        if(psiElement instanceof Comment) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.COMMENT);
        }
        if(psiElement instanceof Attribute) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.ATTRIBUTE);
        }
        if(psiElement instanceof AttributeValue) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.ATTRIBUTE_VALUE);
        }
        if(psiElement instanceof Interpolation) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.ATTRIBUTE_VALUE);
        }
        if(psiElement instanceof EndInterpolation) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.ATTRIBUTE_VALUE);
        }
        if(psiElement instanceof Dollar) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.ATTRIBUTE_VALUE);
        }
        if(psiElement instanceof String) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.STRING);
        }
        if(psiElement instanceof ColonIdentifier) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.IDENTIFIER);
        }
        if(psiElement instanceof DotIdentifier) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.IDENTIFIER);
        }
        if(psiElement instanceof SharpIdentifier) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.IDENTIFIER);
        }
    }
}


