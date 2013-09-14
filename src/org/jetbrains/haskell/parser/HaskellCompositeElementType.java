package org.jetbrains.haskell.parser;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.haskell.HaskellLanguage;
import org.jetbrains.annotations.NonNls;

/**
 * @author Evgeny.Kurbatsky
 */
public class HaskellCompositeElementType extends IElementType {
    private final String myDebugName;

    public HaskellCompositeElementType(@NonNls String debugName) {
        super(debugName, HaskellLanguage.INSTANCE);
        myDebugName = debugName;
    }

    public String getDebugName() {
        return myDebugName;
    }
}
