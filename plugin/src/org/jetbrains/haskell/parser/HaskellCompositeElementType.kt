package org.jetbrains.haskell.parser

import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.HaskellLanguage
import org.jetbrains.annotations.NonNls

public open class HaskellCompositeElementType(debugName: String) : IElementType(debugName, HaskellLanguage.INSTANCE) {
    private val myDebugName: String
    public open fun getDebugName(): String {
        return myDebugName
    }

    {
        myDebugName = debugName
    }

}
