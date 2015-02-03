package org.jetbrains.yesod.cassius.parser;

/**
 * @author Leyla H
 */

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.yesod.cassius.CassiusLanguage;


public class CassiusToken extends IElementType {

    public CassiusToken(@NotNull @NonNls String debugName) {
        super(debugName, CassiusLanguage.INSTANCE);
    }
}
