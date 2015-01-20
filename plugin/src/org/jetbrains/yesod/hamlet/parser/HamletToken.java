package org.jetbrains.yesod.hamlet.parser;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yesod.hamlet.HamletLanguage;


public class HamletToken extends IElementType {

    public HamletToken(@NotNull @NonNls String debugName) {
        super(debugName, HamletLanguage.INSTANCE);
    }
}
