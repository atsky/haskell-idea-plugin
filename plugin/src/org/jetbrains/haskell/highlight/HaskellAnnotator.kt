package org.jetbrains.haskell.highlight

import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.lang.annotation.AnnotationHolder
import org.jetbrains.haskell.psi.Import
import com.intellij.psi.tree.TokenSet
import org.jetbrains.haskell.parser.token.*
import com.intellij.lang.ASTNode
import org.jetbrains.grammar.HaskellLexerTokens.*
import org.jetbrains.haskell.psi.TupleType
import org.jetbrains.haskell.psi.ListType
import org.jetbrains.haskell.psi.FunctionType
import org.jetbrains.haskell.psi.TypeVariable
import org.jetbrains.haskell.psi.SignatureDeclaration


/**
 * Created by atsky on 6/6/14.
 */
public class HaskellAnnotator() : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is Import) {
            for (node in element.getNode().getChildren(TokenSet.create(HIDING, QUALIFIED))!!) {
                holder.createInfoAnnotation(node, null)?.setTextAttributes(HaskellHighlighter.HASKELL_KEYWORD)
            }
            for (node in element.getImportAsPart()?.getNode()?.getChildren(TokenSet.create(AS)) ?: array<ASTNode>()) {
                holder.createInfoAnnotation(node, null)?.setTextAttributes(HaskellHighlighter.HASKELL_KEYWORD)
            }
        }
        if (element is FunctionType) {
            for (node in element.getNode().getChildren(TokenSet.create(RARROW))!!) {
                holder.createInfoAnnotation(node, null)?.setTextAttributes(HaskellHighlighter.HASKELL_TYPE)
            }
        }
        if (element is ListType) {
            for (node in element.getNode().getChildren(TokenSet.create(OBRACK, CBRACK))!!) {
                holder.createInfoAnnotation(node, null)?.setTextAttributes(HaskellHighlighter.HASKELL_TYPE)
            }
        }
        if (element is TypeVariable && !element.isConstructor()) {
            for (node in element.getNode().getChildren(TokenSet.create(CONID, VARID))!!) {
                holder.createInfoAnnotation(node, null)?.setTextAttributes(HaskellHighlighter.HASKELL_TYPE)
            }
        }
        if (element is SignatureDeclaration) {
            val qVar = element.getQNameExpression()?.getQVar()
            val node = qVar?.getNode()?.getFirstChildNode()
            holder.createInfoAnnotation(node, null)?.setTextAttributes(HaskellHighlighter.HASKELL_SIGNATURE)
        }
        if (element is TupleType) {
            for (node in element.getNode().getChildren(TokenSet.create(OPAREN, CPAREN, COMMA))!!) {
                holder.createInfoAnnotation(node, null)?.setTextAttributes(HaskellHighlighter.HASKELL_TYPE)
            }
        }
    }

}