package org.jetbrains.yesod.julius.parser

/**
 * @author Leyla H
 */

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.jetbrains.yesod.julius.JuliusLanguage


class JuliusCompositeElementType(val debugName: String) :
        IElementType(debugName, JuliusLanguage.INSTANCE)