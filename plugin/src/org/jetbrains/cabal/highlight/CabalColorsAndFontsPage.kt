package org.jetbrains.cabal.highlight

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.jetbrains.annotations.NonNls
import org.jetbrains.haskell.icons.HaskellIcons

import javax.swing.*
import java.util.HashMap


public class CabalColorsAndFontsPage : ColorSettingsPage {
    override fun getDisplayName(): String {
        return "Cabal"
    }

    override fun getIcon(): Icon? {
        return HaskellIcons.CABAL
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return ATTRS
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return arrayOf()
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return CabalHighlighter()
    }

    NonNls
    override fun getDemoText(): String {
        return "<prop>name</prop><colon>:</colon>                package-name\n" +
                "<prop>version</prop><colon>:</colon>             0.1.0.0\n" +
                "<prop>license</prop><colon>:</colon>             GPL\n" +
                "<prop>license-file</prop><colon>:</colon>        LICENSE <comment>--Comment</comment>\n" +
                "\n" +
                "\n" +
                "<prop>executable</prop> exec\n" +
                "       <prop>main-is</prop><colon>:</colon>           Main.hs\n"

    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? {
        val map = HashMap<String, TextAttributesKey>()

        map.put("colon", CabalHighlighter.CABAL_COLON)
        map.put("comment", CabalHighlighter.CABAL_COMMENT)
        map.put("prop", CabalHighlighter.CABAL_PROPERTY)
        return map
    }

    companion object {
        private val ATTRS = arrayOf(AttributesDescriptor("Colon", CabalHighlighter.CABAL_COLON),
                AttributesDescriptor("Comment", CabalHighlighter.CABAL_COMMENT),
                AttributesDescriptor("Property", CabalHighlighter.CABAL_PROPERTY))
    }
}
