package org.jetbrains.cabal.parser;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class CabalLexer extends FlexAdapter {
    public CabalLexer() {
        super(new _CabalLexer((Reader)null));
    }
}
