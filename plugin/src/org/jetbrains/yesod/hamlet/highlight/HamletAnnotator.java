package org.jetbrains.yesod.hamlet.highlight;

/**
 * @author Leyla H
 */

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiElement;
import org.jetbrains.yesod.hamlet.psi.*;

public class HamletAnnotator implements Annotator {
    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
         if(psiElement instanceof Tag) {
             annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(DefaultLanguageHighlighterColors.KEYWORD);
         }
         if(psiElement instanceof Doctype) {
             annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATORS);
         }
         if(psiElement instanceof ElseCondition) {
             annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATORS);
         }
         if(psiElement instanceof IfCondition) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATORS);
         }
        if(psiElement instanceof ElseIfCondition) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATORS);
        }
        if(psiElement instanceof Forall) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATORS);
        }
        if(psiElement instanceof ControlCase) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATORS);
        }
        if(psiElement instanceof ControlMaybe) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATORS);
        }
        if(psiElement instanceof ControlNothing) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATORS);
        }
        if(psiElement instanceof ControlOf) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATORS);
        }
        if(psiElement instanceof ControlWith) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATORS);
        }
        if(psiElement instanceof Comments) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.COMMENTS);
        }
        if(psiElement instanceof InvalidDollar) {
            annotationHolder.createErrorAnnotation(psiElement, "Invalid Dollar");
        }
        if(psiElement instanceof Curly) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.TEXT);
        }
        if(psiElement instanceof Sign) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.SIGN);
        }
    }
}
