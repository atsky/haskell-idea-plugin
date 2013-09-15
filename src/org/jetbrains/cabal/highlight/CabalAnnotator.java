package org.jetbrains.cabal.highlight;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.cabal.psi.PropertyKey;
import org.jetbrains.cabal.psi.SectionType;
import org.jetbrains.haskell.highlight.HaskellHighlighter;

public class CabalAnnotator implements Annotator {

  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
      if (element instanceof PropertyKey || element instanceof SectionType  ) {
        holder.createInfoAnnotation(element, null).setTextAttributes(HaskellHighlighter.KEYWORD_VALUE);
      }
  }


}