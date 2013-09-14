package org.jetbrains.haskell.cabal.highlight;

import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.highlight.HaskellHighlighter;

public class CabalHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory {

  @NotNull
  protected CabalHighlighter createHighlighter() {
    return new CabalHighlighter();
  }

}