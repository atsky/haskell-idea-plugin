package org.jetbrains.haskell;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import org.jetbrains.haskell.highlight.HaskellHighlighter;
import org.jetbrains.annotations.NotNull;

public class HaskellLanguage extends Language {
    public static final HaskellLanguage INSTANCE = new HaskellLanguage();

    public HaskellLanguage() {
        super("Haskell", "text/haskell");
        SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(this, new SingleLazyInstanceSyntaxHighlighterFactory() {
            @NotNull
            protected SyntaxHighlighter createHighlighter() {
                return new HaskellHighlighter();
            }
        });
    }
}