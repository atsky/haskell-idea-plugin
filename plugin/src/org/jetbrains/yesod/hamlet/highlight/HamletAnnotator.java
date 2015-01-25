package org.jetbrains.yesod.hamlet.highlight;

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
        if(psiElement instanceof Forall) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(HamletColors.OPERATORS);
        }
    }
}
