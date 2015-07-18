package org.jetbrains.yesod.hamlet.parser

/**
 * @author Leyla H
 */

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.jetbrains.yesod.hamlet.HamletLanguage


public class HamletCompositeElementType(val debugName: String) :
        IElementType(debugName, HamletLanguage.INSTANCE) {

}

