package org.jetbrains.haskell.cabal;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class CabalLexer extends FlexAdapter {
    public CabalLexer() {
        super(new _CabalLexer((Reader)null));
    }
}
