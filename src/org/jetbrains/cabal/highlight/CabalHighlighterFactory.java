package org.jetbrains.cabal.highlight;

import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import org.jetbrains.annotations.NotNull;

public class CabalHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory {

  @NotNull
  protected CabalHighlighter createHighlighter() {
    return new CabalHighlighter();
  }

}