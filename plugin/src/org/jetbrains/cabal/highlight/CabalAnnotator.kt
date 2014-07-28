package org.jetbrains.cabal.highlight

import com.intellij.lang.annotation.*
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.psi.*
import org.jetbrains.haskell.highlight.HaskellHighlighter
import org.jetbrains.cabal.parser.*

public class CabalAnnotator() : Annotator {

    public override fun annotate(element: PsiElement, holder: AnnotationHolder): Unit {
        fun keyword(e : PsiElement) {
            holder.createInfoAnnotation(e, null)?.setTextAttributes(HaskellHighlighter.KEYWORD_VALUE)
        }

        if (element is Checkable) {
            val errorMsg = element.isValidValue()
            if (errorMsg != null) {
                holder.createErrorAnnotation(element.getNode()!!, errorMsg)
            }
        }

        if ((element is Field) && !(element.isUniqueOnThisLevel())) {
            holder.createErrorAnnotation(element.getKeyNode(), "duplicated field")
        }

        if ((element is Disallowedable)) {
            val errorMsg = element.isEnabled()
            if (errorMsg != null) {
                holder.createErrorAnnotation((element : Field).getKeyNode(), errorMsg)
            }
        }

        if ((element is InvalidProperty)) {
            holder.createErrorAnnotation(element, "invalid property")
        }

        if ((element is Section)) {
            val errorMsg = element.allRequiredFieldsExist()
            if (errorMsg != null) {
                holder.createErrorAnnotation(element.getSectTypeNode(), errorMsg)
            }
        }

        if ((element is PropertyKey) || (element is SectionType)) {
            keyword(element)
        }
    }

}
