package org.jetbrains.yesod.hamlet.parser

/**
 * @author Leyla H
 */

import com.intellij.psi.tree.IElementType

import org.jetbrains.yesod.hamlet.HamletLanguage


class HamletToken(debugName: String) : IElementType(debugName, HamletLanguage.INSTANCE)
