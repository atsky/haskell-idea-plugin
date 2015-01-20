package org.jetbrains.yesod.hamlet.parser;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.FlexLexer;

import java.io.Reader;

public class HamletLexer extends FlexAdapter {
    public HamletLexer() {
        super(new _HamletLexer((Reader)null));
    }

}