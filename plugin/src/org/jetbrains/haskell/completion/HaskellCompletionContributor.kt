package org.jetbrains.haskell.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.haskell.psi.Module
import java.util.HashSet
import org.jetbrains.haskell.external.GHC_MOD
import org.jetbrains.haskell.psi.ModuleName
import org.jetbrains.haskell.parser.token.KEYWORDS
import org.jetbrains.haskell.external.BuildWrapper
import org.jetbrains.haskell.util.getRelativePath


public class HaskellCompletionContributor() : CompletionContributor() {

    override fun fillCompletionVariants(parameters: CompletionParameters?, result: CompletionResultSet?) {
        if (parameters!!.getCompletionType() == CompletionType.BASIC) {
            val psiElement = parameters.getPosition()

            /*
            val moduleContent = BuildWrapper.getModuleContentDir(psiElement)
            val path = getRelativePath(moduleContent.getPath(),
                    parameters.getOriginalFile().getVirtualFile()!!.getPath())
            val names = BuildWrapper.init(moduleContent).namesinscope(path)
            if (names != null) {
                for (value in names) {
                    val text = value as String
                    val indexOf = text.lastIndexOf(".")
                    result!!.addElement(LookupElementBuilder.create(text.substring(indexOf + 1))!!)
                }
            }
            */

            for (value in KEYWORDS) {
                result!!.addElement(LookupElementBuilder.create(value.myName)!!)
            }

            if (psiElement.getParent() is ModuleName) {
                for (value in GHC_MOD.getModulesList()) {
                    result!!.addElement(LookupElementBuilder.create(value)!!)
                }
            } else {
                for (value in findCompletion(psiElement)) {
                    result!!.addElement(LookupElementBuilder.create(value)!!)
                }
            }
        }
    }

    private fun findCompletion(element: PsiElement) : Set<String> {
        val names = HashSet<String>()
        val module = Module.findModule(element)

        if (module != null) {
            val list = (module as Module).getImportList()
            for (import in list) {
                val moduleExports = import.getModuleExports()
                if (moduleExports != null) {
                    for (export in moduleExports.getSymbolExportList()) {
                        names.add(export.getText()!!)
                    }
                } else {
                    val moduleName = import.getModuleName()!!.getText()
                    for (name in GHC_MOD.getModuleContent(moduleName!!)) {
                        names.add(name)
                    }
                }
            }
        }
        for (name in GHC_MOD.getModuleContent("Prelude")) {
            names.add(name)
        }
        return names
    }
}
