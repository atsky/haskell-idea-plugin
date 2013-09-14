package org.jetbrains.haskell.cabal;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.haskell.parser.HaskellToken;

/**
 * @author Evgeny.Kurbatsky
 */
public interface CabalTokelTypes {
    IElementType COLON = new HaskellToken(":");
    IElementType COMMA = new HaskellToken(",");
    IElementType COMMENT = new HaskellToken("COMMENT");
    IElementType DOT = new HaskellToken(".");
    IElementType END_OF_LINE_COMMENT = new HaskellToken("--");
    IElementType STRING = new HaskellToken("string");
    IElementType NUMBER = new HaskellToken("number");
    IElementType ID = new HaskellToken("id");
}
