package org.jetbrains.haskell.doc

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import org.jetbrains.annotations.Nullable

/**
 * Created by atsky on 4/25/14.
 */
public class HaskellQuickDocumentationProvider() : DocumentationProvider {

    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        return null;
    }

    override fun getUrlFor(element: PsiElement?, originalElement: PsiElement?): MutableList<String>? {
        return null
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        return null
    }

    override fun getDocumentationElementForLookupItem(psiManager: PsiManager?, `object`: Any?, element: PsiElement?): PsiElement? {
        return null
    }

    override fun getDocumentationElementForLink(psiManager: PsiManager?, link: String?, context: PsiElement?): PsiElement? {
        return null
    }
}
