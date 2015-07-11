package org.jetbrains.yesod.cassius.parser;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yesod.cassius.CassiusLanguage;

/**
 * @author Leyla H
 */

public class CassiusCompositeElementType extends IElementType {

    String myDebugName;

    public CassiusCompositeElementType(@NotNull @NonNls String debugName) {
        super(debugName, CassiusLanguage.INSTANCE);
    }

    public String getDebugName() {
        return myDebugName;
    }
}
