package org.jetbrains.yesod.lucius.highlight

/**
 * @author Leyla H
 */

import com.intellij.openapi.editor.colors.TextAttributesKey

public interface LuciusColors {
    companion object {

        public val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_COMMENT")
        public val AT_RULE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_ATRULE")
        public val ATTRIBUTE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_ATTRIBUTE")
        public val DOT_IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_DOTIDENTIFIER")
        public val SHARP_IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_SHARPIDENTIFIER")
        public val COLON_IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_COLONIDENTIFIER")
        public val CC_IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_CCIDENTIFIER")
        public val INTERPOLATION: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_INTERPOLATION")
        public val NUMBER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_NUMBER")
        public val STRING: TextAttributesKey = TextAttributesKey.createTextAttributesKey("LUCIUS_STRING")
    }

}