package org.jetbrains.haskell.highlight

import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.lang.annotation.AnnotationHolder
import org.jetbrains.haskell.psi.Import
import com.intellij.psi.tree.TokenSet
import org.jetbrains.haskell.parser.token.*
import com.intellij.lang.ASTNode


/**
 * Created by atsky on 6/6/14.
 */
public class HaskellAnnotator() : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is Import) {
            for (node in element.getNode().getChildren(TokenSet.create(HIDING_KW, QUALIFIED_KW))!!) {
                holder.createInfoAnnotation(node, null)?.setTextAttributes(HaskellHighlighter.KEYWORD_VALUE)
            }
            for (node in element.getImportAsPart()?.getNode()?.getChildren(TokenSet.create(AS_KW)) ?: array<ASTNode>()) {
                holder.createInfoAnnotation(node, null)?.setTextAttributes(HaskellHighlighter.KEYWORD_VALUE)
            }
        }
    }

}