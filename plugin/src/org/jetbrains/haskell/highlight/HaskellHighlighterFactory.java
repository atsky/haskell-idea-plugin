package org.jetbrains.haskell.highlight;

import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import org.jetbrains.annotations.NotNull;

public class HaskellHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory {

  @NotNull
  protected HaskellHighlighter createHighlighter() {
    return new HaskellHighlighter();
  }

}