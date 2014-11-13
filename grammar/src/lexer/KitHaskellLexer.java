package lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

/**
 * Created with IntelliJ IDEA.
 * User: Евгений
 * Date: 28.08.13
 * Time: 22:06
 * To change this template use File | Settings | File Templates.
 */
public class KitHaskellLexer extends FlexAdapter {
    public KitHaskellLexer() {
        super(new _KitHaskellLexer((Reader)null));
    }
}
