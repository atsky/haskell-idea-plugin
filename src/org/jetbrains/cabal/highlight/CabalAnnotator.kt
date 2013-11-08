package org.jetbrains.cabal.highlight

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.psi.Executable
import org.jetbrains.cabal.psi.PropertyKey
import org.jetbrains.cabal.psi.SectionType
import org.jetbrains.haskell.highlight.HaskellHighlighter

public class CabalAnnotator() : Annotator {

    public override fun annotate(element: PsiElement, holder: AnnotationHolder): Unit {
        fun keyword(e : PsiElement) {
            holder.createInfoAnnotation(e, null)?.setTextAttributes(HaskellHighlighter.KEYWORD_VALUE)
        }

        if ((element is PropertyKey) || (element is SectionType)) {
            keyword(element)
        }

        if (element is Executable) {
           keyword(element.getFirstChild()!!)
        }

    }

}
