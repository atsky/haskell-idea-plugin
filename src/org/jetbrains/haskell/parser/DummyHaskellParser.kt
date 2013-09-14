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


public class DummyHaskellParser(val p0: IElementType, val p1: PsiBuilder) {
    val builder: PsiBuilder = p1;




    public fun parse(): ASTNode {
        return parseInternal(p0)
    }

    fun done(marker: PsiBuilder.Marker, result: Boolean, elementType: IElementType): Boolean {
        if (result) {
            marker.done(elementType);
        } else {
            marker.rollbackTo()
        }
        return result;
    }

    fun mark(): PsiBuilder.Marker {
        return builder.mark()!!
    }

    fun parseInternal(root: IElementType): ASTNode {
        val rootMarker = mark()

        while (!builder.eof()) {



            builder.advanceLexer()


        }

        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }

}