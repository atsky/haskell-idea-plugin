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
val ARROW_TYPE              = HaskellCompositeElementType("ArrowType", { ArrowType(it) })
val APPLICATION_TYPE        = HaskellCompositeElementType("APPLICATION_TYPE", { ArrowType(it) })
val CLASS_DECL              = HaskellCompositeElementType("ClassDecl")
val CONSTRUCTOR_DECLARATION = HaskellCompositeElementType("CunstructorDeclaration")
val DATA_DECLARATION        = HaskellCompositeElementType("DataDecl")
val IMPORT                  = HaskellCompositeElementType("Import", { Import(it) })
val IMPORT_AS_PART          = HaskellCompositeElementType("Import as part")
val IMPORT_ELEMENT          = HaskellCompositeElementType("Import element")
val INST_DECL               = HaskellCompositeElementType("InstDecl")
val FUNCTION_DECLARATION    = HaskellCompositeElementType("FunctionDeclaration")
val FQ_NAME : HaskellCompositeElementType               = HaskellCompositeElementType("fqName", { FqName(it) })
val MODULE_NAME : HaskellCompositeElementType           = HaskellCompositeElementType("ModuleName", { ModuleName(it) })
val MODULE_EXPORTS : HaskellCompositeElementType        = HaskellCompositeElementType("Module exports", { ModuleName(it) })
val FUN_BIND : HaskellCompositeElementType              = HaskellCompositeElementType("FunBind")
val MATCH : HaskellCompositeElementType                 = HaskellCompositeElementType("Match")
val MODULE : HaskellCompositeElementType                = HaskellCompositeElementType("Module", { Module(it) })
val PAT_BIND : HaskellCompositeElementType              = HaskellCompositeElementType("PatBind")
val TYPE : HaskellCompositeElementType              = HaskellCompositeElementType("Type")
val TYPE_SIG : HaskellCompositeElementType              = HaskellCompositeElementType("TypeSig")
val UN_GUARDED_RHD : HaskellCompositeElementType        = HaskellCompositeElementType("UnGuardedRhs")
val VAR : HaskellCompositeElementType                   = HaskellCompositeElementType("Var")
val HASKELL_TOKEN : HaskellCompositeElementType         = HaskellCompositeElementType("TOKEN")

val TOKENS : Array<HaskellCompositeElementType> = array<HaskellCompositeElementType>(
        CLASS_DECL,
        DATA_DECLARATION,
        IMPORT,
        INST_DECL,
        FUN_BIND, MATCH,
        PAT_BIND,
        MODULE,
        MODULE_NAME,
        TYPE_SIG,
        UN_GUARDED_RHD,
        VAR)
