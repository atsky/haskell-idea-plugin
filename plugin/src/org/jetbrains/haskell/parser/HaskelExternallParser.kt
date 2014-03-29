package org.jetbrains.haskell.parser

import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import com.intellij.lang.PsiBuilder
import com.intellij.lang.ASTNode


import org.jetbrains.haskell.parser.token.*
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
import java.io.File


public class HaskelExternallParser(val p0: IElementType, val p1: PsiBuilder) {
    val builder: PsiBuilder = p1;
    val myStartsOfLines: List<Int>;
    var tree: SExpression? = null;
    val tokenTypes = HashMap<String, IElementType>();

    {

        val programm = builder.getOriginalText().toString()
        myStartsOfLines = getLineStarts(programm)
        val file = if (System.getProperty("os.name")?.toLowerCase()?.contains("win")!!) {
            "C:\\Users\\Евгений\\Dropbox\\haskell-plugin\\haskell\\src\\parserApi.exe"
        } else {
            "/home/atsky/Dropbox/haskell-plugin/haskell/src/parserApi"
        }
        if (File(file).exists()) {
            val result = ProcessRunner(null).execute(listOf(file), programm)
            val expression = LispParser(result).parseExpression()
            if (expression.isListStarting("ParseOk")) {
                tree = expression.get(1)
            }
            System.out.println(expression);

            for (token in TOKENS) {
                addToken(token)
            }
        }
    }

    fun addToken(aType: HaskellCompositeElementType) {
        tokenTypes[aType.getDebugName()!!] = aType
    }

    fun getLineStarts(text: String): List<Int> {
        val startsOfLines = ArrayList<Int>()
        startsOfLines.add(0)
        var index = 0;
        for (char in text.toCharArray()) {
            index++;
            if (char == '\n') {
                startsOfLines.add(index)
            }
        }
        return startsOfLines
    }

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

        val markers = LinkedList<Pair<SExpression, PsiBuilder.Marker>>();

        while (!builder.eof()) {


            val currentOffset = builder.getCurrentOffset()
            val length = builder.getTokenText()!!.length
            if (tree != null) {
                for (ast in findInAst(tree!!, currentOffset, true)) {
                    //System.out.println("start " + ast.getValue(0)!! + " " + currentOffset)
                    val elementType = tokenTypes[ast.getValue(0)!!]
                    if (elementType != null) {
                        val marker = mark()
                        markers.addLast(Pair(ast, marker));
                    } else {
                        System.out.println("Unknown ast element " + ast.getValue(0)!!)
                    }
                }
            }

            builder.advanceLexer()

            if (tree != null) {
                for (ast in findInAst(tree!!, currentOffset + length, false).reverse()) {
                    //System.out.println("end " + ast.getValue(0)!! + " " + currentOffset + length)
                    val elementType = tokenTypes[ast.getValue(0)!!]
                    if (elementType != null) {
                        if (markers.getLast().first == ast) {
                            markers.removeLast().second.done(elementType)
                        }
                    }
                }
            }

        }

        while (!markers.isEmpty()) {
            val last = markers.removeLast()
            val elementType = tokenTypes[last.first.getValue(0)!!]
            last.second.done(elementType);
        }

        rootMarker.done(root)
        return builder.getTreeBuilt()!!
    }

    fun findInAst(expr: SExpression, offset: Int, starts: Boolean): List<SExpression> {
        val firstChild = expr.get(0)
        val result = ArrayList<SExpression>()

        if (firstChild is SList) {
            for (child in (expr as SList).children) {
                result.addAll(findInAst(child, offset, starts));
            }
        }

        if (firstChild is SAtom) {
            val posChild = expr.get(1)
            if (posChild == null) {
                return ArrayList()
            }

            if (posChild.isListStarting("SrcSpan")) {
                val lineStart = Integer.parseInt(posChild.getValue(2)!!)
                val colStart = Integer.parseInt(posChild.getValue(3)!!)
                val startOffset = myStartsOfLines[lineStart - 1] + colStart - 1
                if (startOffset == offset && starts) {
                    result.add(expr);
                }

                val lineEnd = Integer.parseInt(posChild.getValue(4)!!)
                val colEnd = Integer.parseInt(posChild.getValue(5)!!)
                val endOffset = myStartsOfLines[lineEnd - 1] + colEnd - 1
                if (endOffset == offset && !starts) {
                    result.add(expr);
                }
                val children = (expr as SList).children
                for (child in children.subList(2, children.size())) {
                    result.addAll(findInAst(child, offset, starts));
                }
            }
        }

        return result;
    }

}