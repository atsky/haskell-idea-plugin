package org.jetbrains.haskell.parser.lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

/**
 * Created with IntelliJ IDEA.
 * User: Евгений
 * Date: 28.08.13
 * Time: 22:06
 * To change this template use File | Settings | File Templates.
 */
public class HaskellLexer extends FlexAdapter {
    public HaskellLexer() {
        super(new _HaskellLexer((Reader)null));
    }
}
