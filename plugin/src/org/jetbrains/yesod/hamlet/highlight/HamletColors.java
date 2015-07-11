package org.jetbrains.yesod.hamlet.highlight;

/**
 * @author Leyla H
 */

import com.intellij.openapi.editor.colors.TextAttributesKey;

public interface HamletColors {
    TextAttributesKey OPERATOR = TextAttributesKey.createTextAttributesKey("HAMLET_OPERATORS");
    TextAttributesKey COMMENTS = TextAttributesKey.createTextAttributesKey("HAMLET_COMMENTS");
    TextAttributesKey ERRORS = TextAttributesKey.createTextAttributesKey("HAMLET_ERRORS");
    TextAttributesKey TEXT = TextAttributesKey.createTextAttributesKey("HAMLET_TEXT");
    TextAttributesKey SIGN = TextAttributesKey.createTextAttributesKey("HAMLET_OPERATORS");
}
