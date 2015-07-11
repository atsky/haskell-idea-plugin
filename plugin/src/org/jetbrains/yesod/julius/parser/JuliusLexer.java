package org.jetbrains.yesod.julius.parser;

/**
 * @author Leyla H
 */

import com.intellij.lexer.FlexAdapter;
import java.io.Reader;

public class JuliusLexer extends FlexAdapter {
    public JuliusLexer() {
        super(new _JuliusLexer((Reader)null));
    }

}
