package org.jetbrains.yesod.hamlet.parser;

/**
 * @author Leyla H
 */

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class HamletLexer extends FlexAdapter {
    public HamletLexer() {
        super(new _HamletLexer((Reader)null));
    }

}