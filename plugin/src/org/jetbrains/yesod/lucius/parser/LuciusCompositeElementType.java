package org.jetbrains.yesod.lucius.parser;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yesod.lucius.LuciusLanguage;

/**
 * @author Leyla H
 */

public class LuciusCompositeElementType extends IElementType {

    String myDebugName;

    public LuciusCompositeElementType(@NotNull @NonNls String debugName) {
        super(debugName, LuciusLanguage.INSTANCE);
    }

    public String getDebugName() {
        return myDebugName;
    }
}
