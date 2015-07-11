package org.jetbrains.yesod.julius.parser;

/**
 * @author Leyla H
 */

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.yesod.julius.JuliusLanguage;


public class JuliusCompositeElementType extends IElementType {

    String myDebugName;

    public JuliusCompositeElementType(@NotNull @NonNls String debugName) {
        super(debugName, JuliusLanguage.INSTANCE);
    }

    public String getDebugName() {
        return myDebugName;
    }
}