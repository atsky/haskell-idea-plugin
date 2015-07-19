package org.jetbrains.yesod.julius.highlight

/**
 * @author Leyla H
 */

import com.intellij.openapi.editor.colors.TextAttributesKey

public interface JuliusColors {
    companion object {
        public val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("JULIUS_COMMENT")
        public val STRING: TextAttributesKey = TextAttributesKey.createTextAttributesKey("JULIUS_STRING")
        public val DOT_IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("JULIUS_DOTIDENTIFIER")
        public val NUMBER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("JULIUS_NUMBER")
        public val INTERPOLATION: TextAttributesKey = TextAttributesKey.createTextAttributesKey("JULIUS_INTERPOLATION")
    }
}
