package org.jetbrains.yesod.hamlet.highlight

/**
 * @author Leyla H
 */

import com.intellij.openapi.editor.colors.TextAttributesKey

public interface HamletColors {
    companion object {
        public val OPERATOR: TextAttributesKey = TextAttributesKey.createTextAttributesKey("HAMLET_OPERATOR")
        public val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("HAMLET_COMMENT")
        public val ATTRIBUTE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("HAMLET_ATTRIBUTE")
        public val ATTRIBUTE_VALUE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("HAMLET_ATTRIBUTE_VALUE")
        public val STRING: TextAttributesKey = TextAttributesKey.createTextAttributesKey("HAMLET_STRING")
        public val IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("HAMLET_IDENTIFIER")
    }
}
