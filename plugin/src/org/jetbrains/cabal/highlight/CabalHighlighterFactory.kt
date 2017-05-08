package org.jetbrains.cabal.highlight

import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory

class CabalHighlighterFactory : SingleLazyInstanceSyntaxHighlighterFactory() {

    override fun createHighlighter(): CabalHighlighter {
        return CabalHighlighter()
    }

}