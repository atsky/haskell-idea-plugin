package org.jetbrains.haskell.highlight;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.haskell.icons.HaskellIcons;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;


public class HaskellColorsAndFontsPage implements ColorSettingsPage {
  @Override
  @NotNull
  public String getDisplayName() {
    return "Haskell";
  }

  @Override
  @Nullable
  public Icon getIcon() {
    return HaskellIcons.HASKELL;
  }

  @Override
  @NotNull
  public AttributesDescriptor[] getAttributeDescriptors() {
    return ATTRS;
  }

  private static final AttributesDescriptor[] ATTRS =
    new AttributesDescriptor[]{
      new AttributesDescriptor("Keyword", HaskellHighlighter.HASKELL_KEYWORD),
      new AttributesDescriptor("String", HaskellHighlighter.STRING_LITERAL),
      new AttributesDescriptor("Comment", HaskellHighlighter.COMMENT),
      new AttributesDescriptor("Constructor or Type", HaskellHighlighter.CONSTRUCTOR),
      new AttributesDescriptor("Number", DefaultLanguageHighlighterColors.NUMBER)
    };

  @Override
  @NotNull
  public ColorDescriptor[] getColorDescriptors() {
    return new ColorDescriptor[0];
  }

  @Override
  @NotNull
  public SyntaxHighlighter getHighlighter() {
    return new HaskellHighlighter();
  }

  @Override
  @NonNls
  @NotNull
  public String getDemoText() {
    return "<keyword>module</keyword> <cons>Main</cons> <keyword>where</keyword>\n" +
            "<comment>-- Comment</comment>\n" +
            "main :: <cons>IO</cons> ()\n" +
            "main = <keyword>do</keyword>\n" +
            "    putStrLn <string>\"Hello, world!!\"</string>\n"+
            "t = <number>5</number>\n";
  }

  @Override
  @Nullable
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    Map<String, TextAttributesKey> map = new HashMap<String, TextAttributesKey>();
    map.put("keyword", HaskellHighlighter.HASKELL_KEYWORD);
    map.put("string", HaskellHighlighter.STRING_LITERAL);
    map.put("comment", HaskellHighlighter.COMMENT);
    map.put("cons", HaskellHighlighter.CONSTRUCTOR);
    map.put("number", DefaultLanguageHighlighterColors.NUMBER);
    return map;
  }
}
