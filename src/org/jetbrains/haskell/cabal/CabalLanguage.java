package org.jetbrains.haskell.cabal;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.highlight.HaskellHighlighter;

public class CabalLanguage extends Language {
    public static final CabalLanguage INSTANCE = new CabalLanguage();

    public CabalLanguage() {
        super("Cabal", "text/cabal");
    }
}