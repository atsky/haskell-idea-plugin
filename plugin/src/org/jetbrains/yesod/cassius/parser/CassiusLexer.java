package org.jetbrains.yesod.cassius.parser;

/**
 * @author Leyla H
 */

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class CassiusLexer extends FlexAdapter {
    public CassiusLexer() {
        super(new _CassiusLexer((Reader)null));
    }

}