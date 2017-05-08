package org.jetbrains.haskell.findUsages

import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.findUsages.HaskellWordsScanner
import org.jetbrains.haskell.psi.QVar
import org.jetbrains.haskell.psi.TypeVariable

/**
 * Created by atsky on 13/02/15.
 */
class HaskellFindUsagesProvider : FindUsagesProvider {
    override fun getWordsScanner() = HaskellWordsScanner()

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        if (psiElement is QVar) {
            return true
        }
        if (psiElement is TypeVariable) {
            return true
        }
        return false
    }

    override fun getHelpId(psiElement: PsiElement) = com.intellij.lang.HelpID.FIND_OTHER_USAGES

    override fun getType(element: PsiElement): String = "value"

    override fun getDescriptiveName(element: PsiElement): String {
        return element.text
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        return element.text
    }

}