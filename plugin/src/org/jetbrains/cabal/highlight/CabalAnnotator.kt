package org.jetbrains.cabal.highlight

import com.intellij.lang.annotation.*
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.psi.*
import org.jetbrains.haskell.highlight.HaskellHighlighter
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.highlight.ErrorMessage

public class CabalAnnotator() : Annotator {

    public override fun annotate(element: PsiElement, holder: AnnotationHolder): Unit {
        fun keyword(e : PsiElement) {
            holder.createInfoAnnotation(e, null)?.setTextAttributes(HaskellHighlighter.KEYWORD_VALUE)
        }

        fun maybeError(elem: PsiElement, msg: String?) {
            if (msg != null) {
                holder.createErrorAnnotation(elem, msg)
            }
        }

        fun error(errMsg: ErrorMessage) = holder.createErrorAnnotation(errMsg.place, errMsg.text)

        if ((element is PropertyField) && !(element.isUniqueOnThisLevel()))  maybeError(element.getKeyNode(), "duplicated field")
        if (element is DisallowedableField)                                  maybeError(element.getKeyNode(), element.isEnabled())
        if (element is InvalidProperty)                                      maybeError(element, "invalid property")
        if (element is Checkable)                                            maybeError(element, element.isValidValue())
        if (element is Section)                                              maybeError(element.getSectTypeNode(), element.allRequiredFieldsExist())
        if (element is BuildDependsField)   element.checkPackageVersions() forEach { error(it) }

        if (element is Path) {
            val warningMsg = element.isValidPath()
            if (warningMsg != null) {
                holder.createWarningAnnotation(element,  warningMsg)
            }
        }

        if ((element is PropertyKey) || (element is SectionType)) {
            keyword(element)
        }
    }
}
