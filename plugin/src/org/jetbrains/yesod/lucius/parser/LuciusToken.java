package org.jetbrains.yesod.lucius.parser;

/**
 * @author Leyla H
 */

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.yesod.lucius.LuciusLanguage;


public class LuciusToken extends IElementType {

    public LuciusToken(@NotNull @NonNls String debugName) {
        super(debugName, LuciusLanguage.INSTANCE);
    }
}
