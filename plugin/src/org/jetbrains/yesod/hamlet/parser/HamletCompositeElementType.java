package org.jetbrains.yesod.hamlet.parser;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.yesod.hamlet.HamletLanguage;


public class HamletCompositeElementType extends IElementType {

    String myDebugName;

    public HamletCompositeElementType(@NotNull @NonNls String debugName) {
        super(debugName, HamletLanguage.INSTANCE);
    }

    public String getDebugName() {
        return myDebugName;
    }
}

