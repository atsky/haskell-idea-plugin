package org.jetbrains.cabal.completion

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.*
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.psi.*
import org.jetbrains.cabal.CabalInterface
import org.jetbrains.cabal.CabalFile
import java.util.*

open class CabalCompletionContributor : CompletionContributor() {

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet): Unit {
        if (parameters.completionType == CompletionType.BASIC) {

            val values = ArrayList<String>()
            val current = parameters.position
            val parent = current.parent
            if (parent == null) {
                return
            }
            var caseSensitivity = true

            when (parent) {
                is CabalFile -> {
                    values.addAll(PKG_DESCR_FIELDS.keys.map { it + ":" })
                    values.addAll(TOP_SECTION_NAMES)
                    caseSensitivity = false
                }
                is RangedValue -> {
                    if ((parent is Name)) {
                        if (parent.isFlagNameInCondition()) {
                            values.addAll(parent.getAvailableValues().map({ it + ")" }))
                            caseSensitivity = false
                        } else if (parent.getParent() is InvalidField) {
                            values.addAll(parent.getAvailableValues().map({
                                if (it in SECTIONS.keys) it else it + ":"
                            }))
                            caseSensitivity = false
                        }
                    } else values.addAll(parent.getAvailableValues())
                }
                is InvalidValue -> {
                    if (parent.getParent() is BoolField) {
                        values.addAll(BOOL_VALS)
                    }
                }
                is Path -> {
                    val grandParent = parent.getParent()
                    if (grandParent is PathsField) {
                        val originalRootDir = parameters.originalFile.virtualFile!!.parent!!
                        values.addAll(grandParent.getNextAvailableFile(parent, originalRootDir))
                    }
                }
                is Identifier -> {
                    var parentField = parent
                    while ((parentField !is Field) && (parentField !is CabalFile) && (parentField != null)) {
                        parentField = parentField.parent
                    }
                    if (parentField is BuildDependsField) {
                        val project = current.project
                        values.addAll(CabalInterface(project).getInstalledPackagesList().map({ it.name }))
                    }
                }
            }

            for (value in values) {
                val lookupElemBuilder = LookupElementBuilder.create(value).withCaseSensitivity(caseSensitivity)!!
                result.addElement(lookupElemBuilder)
            }
        }
    }
}
