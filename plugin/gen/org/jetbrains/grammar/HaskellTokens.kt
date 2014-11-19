package org.jetbrains.grammar

import com.intellij.psi.tree.IElementType
import org.jetbrains.haskell.parser.HaskellCompositeElementType
import org.jetbrains.haskell.psi.*


public val VALUE_BODY : IElementType = HaskellCompositeElementType("ValueBody", ::ValueBody)
public val CLASS_DECLARATION : IElementType = HaskellCompositeElementType("ClassDeclaration", ::ClassDeclaration)
public val MODULE_NAME : IElementType = HaskellCompositeElementType("ModuleName", ::ModuleName)
public val MODULE : IElementType = HaskellCompositeElementType("Module", ::Module)
