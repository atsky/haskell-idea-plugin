package org.jetbrains.cabal.highlight

import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory

public class CabalHighlighterFactory : SingleLazyInstanceSyntaxHighlighterFactory() {

    override fun createHighlighter(): CabalHighlighter {
        return CabalHighlighter()
    }

}