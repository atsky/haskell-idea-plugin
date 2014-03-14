package org.jetbrains.haskell.parser;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.haskell.HaskellLanguage;
import org.jetbrains.annotations.NonNls;

public class HaskellToken extends IElementType {
    public final String myName;

    public HaskellToken(@NonNls String debugName) {
        super(debugName, HaskellLanguage.INSTANCE);
        myName = debugName;
    }

    @SuppressWarnings({"HardCodedStringLiteral"})
    public String toString() {
        return "Haskell Token:" + super.toString();
    }
}