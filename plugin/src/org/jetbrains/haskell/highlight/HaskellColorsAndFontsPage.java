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
      new AttributesDescriptor("Comment", HaskellHighlighter.COMMENT),
      new AttributesDescriptor("Constructor or Type", HaskellHighlighter.HASKELL_CONSTRUCTOR),
      new AttributesDescriptor("Type", HaskellHighlighter.HASKELL_TYPE),
      new AttributesDescriptor("Number", DefaultLanguageHighlighterColors.NUMBER),
      new AttributesDescriptor("Operator", HaskellHighlighter.HASKELL_OPERATOR),
      new AttributesDescriptor("Pragma", HaskellHighlighter.HASKELL_PRAGMA),
      new AttributesDescriptor("String", HaskellHighlighter.STRING_LITERAL),
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
            "<pragma>{-# LANGUAGE CPP #-}</pragma>\n" +
            "<comment>-- Comment</comment>\n" +
            "\n" +
            "data Maybe a = Nothing | Just a\n" +
            "\n" +
            "main :: <type>IO ()</type>\n" +
            "main = <keyword>do</keyword>\n" +
            "    putStrLn <string>\"Hello\"</string> <operator>++</operator> <string>\" world!!\"</string>\n"+
            "t = <number>5</number>\n";
  }

  @Override
  @Nullable
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    Map<String, TextAttributesKey> map = new HashMap<String, TextAttributesKey>();
    map.put("comment", HaskellHighlighter.COMMENT);
    map.put("type", HaskellHighlighter.HASKELL_TYPE);
    map.put("keyword", HaskellHighlighter.HASKELL_KEYWORD);
    map.put("number", DefaultLanguageHighlighterColors.NUMBER);
    map.put("pragma", HaskellHighlighter.HASKELL_PRAGMA);
    map.put("operator", HaskellHighlighter.HASKELL_OPERATOR);
    map.put("string", HaskellHighlighter.STRING_LITERAL);
    return map;
  }
}
