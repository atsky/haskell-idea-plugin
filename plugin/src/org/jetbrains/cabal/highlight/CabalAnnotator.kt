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

        fun handle(errMsg: ErrorMessage?) {
            if (errMsg == null) return
            if (errMsg.severity == "error")   holder.createErrorAnnotation(errMsg.place, errMsg.text)
            if (errMsg.severity == "warning") holder.createWarningAnnotation(errMsg.place, errMsg.text)
        }

        if (element is PropertyField)       handle(element.checkUniqueness())
        if (element is InvalidProperty)     handle(ErrorMessage(element, "invalid property", "error"))
        if (element is Checkable)           element.checkValue()           forEach { handle(it) }
        if (element is Section)             element.checkFieldsPresence()  forEach { handle(it) }

        if (element is BuildDependsField)   element.checkPackageVersions() forEach { handle(it) }
        if (element is Path)                handle(element.checkPath())

        if ((element is PropertyKey) || (element is SectionType)) {
            keyword(element)
        }
    }
}
