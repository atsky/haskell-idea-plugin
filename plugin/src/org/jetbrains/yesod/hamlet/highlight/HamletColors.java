package org.jetbrains.yesod.hamlet.highlight;

/**
 * @author Leyla H
 */

import com.intellij.openapi.editor.colors.TextAttributesKey;

public interface HamletColors {
    TextAttributesKey OPERATOR = TextAttributesKey.createTextAttributesKey("HAMLET_OPERATOR");
    TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("HAMLET_COMMENT");
    TextAttributesKey ATTRIBUTE = TextAttributesKey.createTextAttributesKey("HAMLET_ATTRIBUTE");
    TextAttributesKey ATTRIBUTE_VALUE = TextAttributesKey.createTextAttributesKey("HAMLET_ATTRIBUTE_VALUE");
    TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("HAMLET_STRING");
    TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey("HAMLET_IDENTIFIER");
}
