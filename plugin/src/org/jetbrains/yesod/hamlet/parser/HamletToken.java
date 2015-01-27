package org.jetbrains.yesod.hamlet.parser;

/**
 * @author Leyla H
 */

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.yesod.hamlet.HamletLanguage;


public class HamletToken extends IElementType {

    public HamletToken(@NotNull @NonNls String debugName) {
        super(debugName, HamletLanguage.INSTANCE);
    }
}
