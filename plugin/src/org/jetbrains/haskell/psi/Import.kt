package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.tree.TokenSet
import org.jetbrains.haskell.parser.token.*
import org.jetbrains.grammar.HaskellLexerTokens


class Import(node : ASTNode) : ASTWrapperPsiElement(node) {

    fun hasHiding() : Boolean {
        return node.getChildren(TokenSet.create(HaskellLexerTokens.HIDING)).size > 0
    }

    fun getModuleName() : ModuleName? =
        findChildByClass(ModuleName::class.java)


    fun getModuleExports() : ModuleExports? =
        findChildByClass(ModuleExports::class.java)

    fun getImportAsPart() : ImportAsPart? =
            findChildByClass(ImportAsPart::class.java)

    fun findModule() : Module? = getModuleName()?.findModuleFile()?.getModule()
}