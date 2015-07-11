package org.jetbrains.yesod.julius.highlight;

/**
 * @author Leyla H
 */

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiElement;
import org.jetbrains.yesod.julius.psi.*;
import org.jetbrains.yesod.julius.psi.String;

public class JuliusAnnotator implements Annotator {
    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {

        if(psiElement instanceof Operator) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(JuliusColors.OPERATOR);
        }
        if(psiElement instanceof Comments) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(JuliusColors.COMMENTS);
        }
        if(psiElement instanceof Curly) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(JuliusColors.TEXT);
        }
        if(psiElement instanceof Sign) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(JuliusColors.SIGN);
        }
        if(psiElement instanceof String) {
            annotationHolder.createInfoAnnotation(psiElement, null).setTextAttributes(JuliusColors.STRING);
        }
    }
}