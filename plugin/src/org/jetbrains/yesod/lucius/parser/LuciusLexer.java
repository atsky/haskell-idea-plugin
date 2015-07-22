package org.jetbrains.yesod.lucius.parser;

/**
 * @author Leyla H
 */

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class LuciusLexer extends FlexAdapter {
    public LuciusLexer() {
        super(new _LuciusLexer((Reader)null));
    }

}