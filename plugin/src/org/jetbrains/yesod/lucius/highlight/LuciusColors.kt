package org.jetbrains.yesod.lucius.highlight

/**
 * @author Leyla H
 */

import com.intellij.openapi.editor.colors.TextAttributesKey

interface LuciusColors {
    companion object {

        val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_COMMENT")
        val AT_RULE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_ATRULE")
        val ATTRIBUTE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_ATTRIBUTE")
        val DOT_IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_DOTIDENTIFIER")
        val SHARP_IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_SHARPIDENTIFIER")
        val COLON_IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_COLONIDENTIFIER")
        val CC_IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_CCIDENTIFIER")
        val INTERPOLATION: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_INTERPOLATION")
        val NUMBER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_NUMBER")
        val STRING: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_STRING")
    }

}