package org.jetbrains.haskell.parser

import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.ASTNode
import org.jetbrains.haskell.parser.token.HaskellTokenTypes;

import org.jetbrains.haskell.parser.token.*
import org.jetbrains.haskell.parser.token.HaskellTokenTypes
import org.jetbrains.haskell.util.ProcessRunner
import org.jetbrains.haskell.util.lisp.LispParser
import org.jetbrains.haskell.util.lisp.SList
import org.jetbrains.haskell.util.lisp.SExpression
import java.util.ArrayList
import org.jaxen.expr.Expr
import java.util.LinkedList
import java.util.HashMap
import org.jetbrains.haskell.util.lisp.SAtom
import org.jetbrains.haskell.compiler.GHCInterface


public class DummyHaskellParser(p0: IElementType, p1: PsiBuilder) : BaseParser(p0, p1) {


    public fun parse(): ASTNode {
        return parseInternal(root)
    }


    fun parseInternal(root: IElementType): ASTNode {
        val rootMarker = mark()

        while (!builder.eof()) {
            start(HaskellTokenTypes.HASKELL_TOKEN) {
                builder.advanceLexer()
                true
            }
        }

        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }

}