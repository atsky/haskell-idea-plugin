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

public open class CabalCompletionContributor() : CompletionContributor() {

    public override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet): Unit {
        if (parameters.getCompletionType() == CompletionType.BASIC) {

            val values = ArrayList<String>()
            val current = parameters.getPosition()
            val parent = current.getParent()
            if (parent == null) { return }
            var caseSensitivity = true

            when (parent) {
                is CabalFile -> {
                    values.addAll(PKG_DESCR_FIELDS.keySet() map {it.concat(":")})
                    values.addAll(TOP_SECTION_NAMES)
                    caseSensitivity = false
                }
                is RangedValue -> {
                    if ((parent is Name)) {
                        if (parent.isFlagNameInCondition()) {
                            values.addAll(parent.getAvailableValues() map { it + ")" })
                            caseSensitivity = false
                        }
                        else if (parent.getParent() is InvalidField) {
                            values.addAll(parent.getAvailableValues() map {
                                if (it in SECTIONS.keySet()) it else it.concat(":")
                            })
                            caseSensitivity = false
                        }
                    }
                    else values.addAll(parent.getAvailableValues())
                }
                is InvalidValue -> {
                    if (parent.getParent() is BoolField) {
                        values.addAll(BOOL_VALS)
                    }
                }
                is Path -> {
                    val grandParent = parent.getParent()
                    if (grandParent is PathsField) {
                        val originalRootDir = parameters!!.getOriginalFile().getVirtualFile()!!.getParent()!!
                        values.addAll(grandParent.getNextAvailableFile(parent, originalRootDir))
                    }
                }
                is Identifier -> {
                    var parentField = parent
                    while ((parentField !is Field) && (parentField !is CabalFile) && (parentField != null)) {
                        // TODO Look like a bug in Kotlin.
                        val parentFieldVal = parentField
                        parentField = parentFieldVal.getParent()
                    }
                    if (parentField is BuildDependsField) {
                        val project = current!!.getProject()
                        values.addAll(CabalInterface(project).getInstalledPackagesList() map { it.name })
                    }
                }
            }

            for (value in values) {
                val lookupElemBuilder = LookupElementBuilder.create(value)!!.withCaseSensitivity(caseSensitivity)!!
                result?.addElement(lookupElemBuilder)
            }
        }
    }
}
