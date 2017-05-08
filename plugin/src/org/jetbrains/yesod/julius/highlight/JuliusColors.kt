package org.jetbrains.yesod.julius.highlight

/**
 * @author Leyla H
 */

import com.intellij.openapi.editor.colors.TextAttributesKey

interface JuliusColors {
    companion object {
        val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("JULIUS_COMMENT")
        val STRING: TextAttributesKey = TextAttributesKey.createTextAttributesKey("JULIUS_STRING")
        val DOT_IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("JULIUS_DOTIDENTIFIER")
        val NUMBER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("JULIUS_NUMBER")
        val INTERPOLATION: TextAttributesKey = TextAttributesKey.createTextAttributesKey("JULIUS_INTERPOLATION")
    }
}
