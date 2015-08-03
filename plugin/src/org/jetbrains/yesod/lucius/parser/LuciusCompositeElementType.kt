package org.jetbrains.yesod.lucius.parser

/**
 * @author Leyla H
 */

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.jetbrains.yesod.lucius.LuciusLanguage


public class LuciusCompositeElementType(val debugName: String) :
        IElementType(debugName, LuciusLanguage.INSTANCE) {

}