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
import org.jetbrains.haskell.psi.ModuleName
import org.jetbrains.haskell.parser.token.KEYWORDS
import org.jetbrains.haskell.util.getRelativePath
import com.intellij.openapi.roots.ProjectRootManager
import org.jetbrains.haskell.external.GhcMod


class HaskellCompletionContributor : CompletionContributor() {

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        if (parameters.completionType == CompletionType.BASIC) {
            val psiElement = parameters.position
            val psiFile = parameters.originalPosition?.containingFile

            /*
            val moduleContent = BuildWrapper.getModuleContentDir(psiElement)
            val path = getRelativePath(moduleContent!!.getGhcModPath(),
                    parameters.getOriginalFile().getVirtualFile()!!.getGhcModPath())
            val names = BuildWrapper.init(psiElement).namesinscope(path)
            if (names != null) {
                for (value in names) {
                    val text = value as String
                    val indexOf = text.lastIndexOf(".")
                    result!!.addElement(LookupElementBuilder.create(text.substring(indexOf + 1))!!)
                }
            }
            */

            for (value in KEYWORDS) {
                result.addElement(LookupElementBuilder.create(value.myName))
            }

            if (psiElement.parent is ModuleName) {
                for (value in GhcMod.getModulesList()) {
                    result.addElement(LookupElementBuilder.create(value))
                }
            } else {
                for (value in findCompletion(psiElement, psiFile)) {
                    result.addElement(LookupElementBuilder.create(value.first)
                                                          .withTypeText(value.second)!!)
                }
            }
        }
    }

    private fun findCompletion(element: PsiElement,
                               psiFile: PsiFile?): Set<Pair<String, String?>> {
        val names = HashSet<Pair<String, String?>>()
        val module = Module.findModule(element)

        if (module != null) {
            val list = module.getImportList()
            for (import in list) {
                val moduleExports = import.getModuleExports()
                if (moduleExports != null) {
                    //for (export in moduleExports.getSymbolExportList()) {
                    //    names.add(Pair(export.getText()!!, null))
                    //}
                } else {
                    val moduleName = import.getModuleName()!!.text
                    for (name in GhcMod.getModuleContent(moduleName!!)) {
                        names.add(name)
                    }
                }
            }
        }
        for (name in GhcMod.getModuleContent("Prelude")) {
            names.add(name)
        }
        return names
    }
}
