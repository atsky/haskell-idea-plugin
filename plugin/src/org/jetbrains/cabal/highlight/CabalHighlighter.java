package org.jetbrains.cabal.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.cabal.parser.CabalLexer;
import org.jetbrains.cabal.parser.CabalTokelTypes;
import org.jetbrains.haskell.highlight.HaskellHighlighter;
import com.intellij.openapi.editor.HighlighterColors;

import java.util.Map;

public class CabalHighlighter extends SyntaxHighlighterBase {
  private static final Map<IElementType, TextAttributesKey> keys1;

  @NotNull
  public Lexer getHighlightingLexer() {
    return new CabalLexer();
  }


  static {
    keys1 = new THashMap<IElementType, TextAttributesKey>();

    keys1.put(CabalTokelTypes.STRING, HaskellHighlighter.STRING_LITERAL);
    keys1.put(CabalTokelTypes.END_OF_LINE_COMMENT, HaskellHighlighter.COMMENT);
    keys1.put(CabalTokelTypes.COMMENT, HaskellHighlighter.COMMENT);
    keys1.put(CabalTokelTypes.TAB, HighlighterColors.BAD_CHARACTER);


  }

  @NotNull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(keys1.get(tokenType));
  }

}