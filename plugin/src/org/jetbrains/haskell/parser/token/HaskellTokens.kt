package org.jetbrains.haskell.parser.token

import org.jetbrains.haskell.parser.HaskellCompositeElementType
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.psi.Import
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.TokenType
import org.jetbrains.haskell.psi.*

/**
 * Created by atsky on 3/14/14.
 */
val ARROW_TYPE              = HaskellCompositeElementType("Arrow type", { ArrowType(it) })
val APPLICATION_TYPE        = HaskellCompositeElementType("APPLICATION_TYPE", { ArrowType(it) })
val CLASS_DECLARATION       = HaskellCompositeElementType("Class declaration")
val CONSTRUCTOR             = HaskellCompositeElementType("Cunstructor", { Constructor(it) })
val CONSTRUCTOR_DECLARATION = HaskellCompositeElementType("Cunstructor declaration", { ConstructorDeclaration(it) })
val DATA_DECLARATION        = HaskellCompositeElementType("Data declaration", { DataDeclaration(it) })
val IMPORT                  = HaskellCompositeElementType("Import", { Import(it) })
val IMPORT_AS_PART          = HaskellCompositeElementType("Import as part")
val SYMBOL_EXPORT           = HaskellCompositeElementType("Symbol export", { SymbolExport(it) })
val INSTANCE_DECLARATION    = HaskellCompositeElementType("Instance declaration")
val FUNCTION_BODY           = HaskellCompositeElementType("Function body")
val FUNCTION_DECLARATION    = HaskellCompositeElementType("Function declaration", { FunctionDeclaration(it) })
val FQ_NAME                 = HaskellCompositeElementType("fqName", { FqName(it) })
val MODULE_NAME             = HaskellCompositeElementType("Module name", { ModuleName(it) })
val MODULE_EXPORTS          = HaskellCompositeElementType("Module exports", { ModuleExports(it) })
val MODULE                  = HaskellCompositeElementType("Module", { Module(it) })
val MODULE_HEADER           = HaskellCompositeElementType("Module header")
val NAME                    = HaskellCompositeElementType("Name", { Name(it) })
val TYPE                    = HaskellCompositeElementType("Type")
val SOME_ID                 = HaskellCompositeElementType("Some id", { SomeId(it) })
val HASKELL_TOKEN           = HaskellCompositeElementType("TOKEN")
