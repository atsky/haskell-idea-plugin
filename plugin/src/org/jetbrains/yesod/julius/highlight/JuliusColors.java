package org.jetbrains.yesod.julius.highlight;

/**
 * @author Leyla H
 */

import com.intellij.openapi.editor.colors.TextAttributesKey;

public interface JuliusColors {
    TextAttributesKey OPERATOR = TextAttributesKey.createTextAttributesKey("JULIUS_OPERATORS");
    TextAttributesKey COMMENTS = TextAttributesKey.createTextAttributesKey("JULIUS_COMMENTS");
    TextAttributesKey ERRORS = TextAttributesKey.createTextAttributesKey("JULIUS_ERRORS");
    TextAttributesKey TEXT = TextAttributesKey.createTextAttributesKey("JULIUS_TEXT");
    TextAttributesKey SIGN = TextAttributesKey.createTextAttributesKey("JULIUS_OPERATORS");
    TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("JULIUS_STRING");
}
