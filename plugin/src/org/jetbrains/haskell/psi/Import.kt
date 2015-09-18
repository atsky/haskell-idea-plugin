package org.jetbrains.haskell.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.tree.TokenSet
import org.jetbrains.haskell.parser.token.*
import org.jetbrains.grammar.HaskellLexerTokens


public class Import(node : ASTNode) : ASTWrapperPsiElement(node) {

    public fun hasHiding() : Boolean {
        return getNode().getChildren(TokenSet.create(HaskellLexerTokens.HIDING)).size() > 0
    }

    public fun getModuleName() : ModuleName? =
        findChildByClass(ModuleName::class.java)


    public fun getModuleExports() : ModuleExports? =
        findChildByClass(ModuleExports::class.java)

    public fun getImportAsPart() : ImportAsPart? =
            findChildByClass(ImportAsPart::class.java)

    public fun findModule() : Module? = getModuleName()?.findModuleFile()?.getModule()
}