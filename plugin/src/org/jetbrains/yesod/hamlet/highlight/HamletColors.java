package org.jetbrains.yesod.hamlet.highlight;

/**
 * @author Leyla H
 */

import com.intellij.openapi.editor.colors.TextAttributesKey;

public interface HamletColors {
    public static TextAttributesKey OPERATORS = TextAttributesKey.createTextAttributesKey("HAMLET_OPERATORS");
    public static TextAttributesKey COMMENTS = TextAttributesKey.createTextAttributesKey("HAMLET_COMMENTS");
    public static TextAttributesKey ERRORS = TextAttributesKey.createTextAttributesKey("HAMLET_ERRORS");
    public static TextAttributesKey TEXT = TextAttributesKey.createTextAttributesKey("HAMLET_TEXT");
    public static TextAttributesKey SIGN = TextAttributesKey.createTextAttributesKey("HAMLET_SIGN");
}
