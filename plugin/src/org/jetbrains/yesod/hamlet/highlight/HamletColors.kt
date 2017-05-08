package org.jetbrains.yesod.hamlet.highlight

/**
 * @author Leyla H
 */

import com.intellij.openapi.editor.colors.TextAttributesKey

interface HamletColors {
    companion object {
        val OPERATOR: TextAttributesKey = TextAttributesKey.createTextAttributesKey("HAMLET_OPERATOR")
        val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("HAMLET_COMMENT")
        val ATTRIBUTE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("HAMLET_ATTRIBUTE")
        val ATTRIBUTE_VALUE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("HAMLET_ATTRIBUTE_VALUE")
        val STRING: TextAttributesKey = TextAttributesKey.createTextAttributesKey("HAMLET_STRING")
        val IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("HAMLET_IDENTIFIER")
    }
}
