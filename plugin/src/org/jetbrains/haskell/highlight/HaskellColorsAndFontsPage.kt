package org.jetbrains.haskell.highlight

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.jetbrains.annotations.NonNls
import org.jetbrains.haskell.icons.HaskellIcons

import javax.swing.*
import java.util.HashMap


public class HaskellColorsAndFontsPage : ColorSettingsPage {
    override fun getDisplayName(): String {
        return "Haskell"
    }

    override fun getIcon(): Icon? {
        return HaskellIcons.HASKELL
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return ATTRS
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return arrayOf()
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return HaskellHighlighter()
    }

    NonNls
    override fun getDemoText(): String {
        return "<keyword>module</keyword> <cons>Main</cons> <keyword>where</keyword>\n" +
                "<pragma>{-# LANGUAGE CPP #-}</pragma>\n" +
                "<comment>-- Comment</comment>\n" +
                "\n" +
                "<keyword>class</keyword> <class>YesNo a</class> <keyword>where</keyword>\n" +
                "    <sig>yesno</sig> <dcolon>::</dcolon> <type>a -> Bool</type>\n" +
                "\n" +
                "\n" + "<keyword>data</keyword> <type>Maybe a</type> <equal>=</equal> <cons>Nothing</cons> | <cons>Just</cons> <type>a</type>\n" +
                "\n" + "<sig>main</sig> <dcolon>::</dcolon> <type>IO ()</type>\n" +
                "<id>main</id> = <keyword>do</keyword>\n" +
                "    <id>putStrLn</id> <string>\"Hello\"</string> <operator>++</operator> <string>\" world!!\"</string>\n" +
                "<id>t</id> <equal>=</equal> <number>5</number>\n" +
                "<par>(</par><id>t</id><par>)</par>\n" +
                "<curly>{}</curly>\n" +
                "<brackets>[]</brackets>\n"
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? {
        val map = HashMap<String, TextAttributesKey>()

        map.put("brackets", HaskellHighlighter.HASKELL_BRACKETS)
        map.put("class", HaskellHighlighter.HASKELL_CLASS)
        map.put("curly", HaskellHighlighter.HASKELL_CURLY)
        map.put("cons", HaskellHighlighter.HASKELL_CONSTRUCTOR)
        map.put("comment", HaskellHighlighter.HASKELL_COMMENT)
        map.put("dcolon", HaskellHighlighter.HASKELL_DOUBLE_COLON)
        map.put("equal", HaskellHighlighter.HASKELL_EQUAL)
        map.put("id", HaskellHighlighter.HASKELL_IDENTIFIER)
        map.put("keyword", HaskellHighlighter.HASKELL_KEYWORD)
        map.put("number", HaskellHighlighter.HASKELL_NUMBER)
        map.put("operator", HaskellHighlighter.HASKELL_OPERATOR)
        map.put("par", HaskellHighlighter.HASKELL_PARENTHESIS)
        map.put("pragma", HaskellHighlighter.HASKELL_PRAGMA)
        map.put("sig", HaskellHighlighter.HASKELL_SIGNATURE)
        map.put("string", HaskellHighlighter.HASKELL_STRING_LITERAL)
        map.put("type", HaskellHighlighter.HASKELL_TYPE)
        return map
    }

    companion object {
        private val ATTRS = arrayOf(AttributesDescriptor("Brackets", HaskellHighlighter.HASKELL_BRACKETS),
                AttributesDescriptor("Class", HaskellHighlighter.HASKELL_CLASS),
                AttributesDescriptor("Comment", HaskellHighlighter.HASKELL_COMMENT),
                AttributesDescriptor("Curly brackets", HaskellHighlighter.HASKELL_CURLY),
                AttributesDescriptor("Constructor or Type", HaskellHighlighter.HASKELL_CONSTRUCTOR),
                AttributesDescriptor("Double color", HaskellHighlighter.HASKELL_DOUBLE_COLON),
                AttributesDescriptor("Equal", HaskellHighlighter.HASKELL_EQUAL),
                AttributesDescriptor("Identifier", HaskellHighlighter.HASKELL_IDENTIFIER),
                AttributesDescriptor("Keyword", HaskellHighlighter.HASKELL_KEYWORD),
                AttributesDescriptor("Number", HaskellHighlighter.HASKELL_NUMBER),
                AttributesDescriptor("Operator", HaskellHighlighter.HASKELL_OPERATOR),
                AttributesDescriptor("Parenthesis", HaskellHighlighter.HASKELL_PARENTHESIS),
                AttributesDescriptor("Pragma", HaskellHighlighter.HASKELL_PRAGMA),
                AttributesDescriptor("Signature", HaskellHighlighter.HASKELL_SIGNATURE),
                AttributesDescriptor("String", HaskellHighlighter.HASKELL_STRING_LITERAL),
                AttributesDescriptor("Type", HaskellHighlighter.HASKELL_TYPE))
    }
}
