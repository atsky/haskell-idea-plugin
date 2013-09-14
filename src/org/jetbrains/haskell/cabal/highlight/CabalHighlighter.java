package org.jetbrains.haskell.cabal.highlight;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.options.OptionsBundle;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.StringEscapesTokenTypes;
import com.intellij.psi.tree.IElementType;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.cabal.CabalLexer;
import org.jetbrains.haskell.cabal.CabalTokelTypes;
import org.jetbrains.haskell.highlight.HaskellHighlighter;
import org.jetbrains.haskell.parser.HaskellLexer;
import org.jetbrains.haskell.parser.token.HaskellTokenTypes;

import java.awt.*;
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
    keys1.put(HaskellTokenTypes.END_OF_LINE_COMMENT, HaskellHighlighter.COMMENT);
    keys1.put(HaskellTokenTypes.COMMENT, HaskellHighlighter.COMMENT);


  }

  @NotNull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(keys1.get(tokenType));
  }

}