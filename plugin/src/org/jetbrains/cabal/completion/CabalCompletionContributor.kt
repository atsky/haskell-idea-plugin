package org.jetbrains.cabal.completion

//import com.intellij.codeInsight.completion.CompletionContributor
//import com.intellij.codeInsight.completion.CompletionParameters
//import com.intellij.codeInsight.completion.CompletionResultSet
//import com.intellij.codeInsight.completion.CompletionType
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

    public override fun fillCompletionVariants(parameters: CompletionParameters?, result: CompletionResultSet?): Unit {
        if (parameters?.getCompletionType() == CompletionType.BASIC) {

            val values = ArrayList<String>()
            val current = parameters?.getPosition()
            val parent = current?.getParent()
            if (parent == null) { return }
            var caseSensitivity = true

            when (parent) {
                is CabalFile -> {
                    values.addAll(PKG_DESCR_FIELD_DESCRS map {it.concat(":")})
                    values.addAll(TOP_SECTIONS)
                    caseSensitivity = false
                }
                is RangedValue -> {
                    if ((parent is Name)) {
                        if (parent.isFlagNameInCondition()) {
                            values.addAll(parent.getAvailableValues() map { it + ")" })
                            caseSensitivity = false
                        }
                        else if (parent.getParent() is InvalidField) {
                            values.addAll(parent.getAvailableValues() map { if ((it in TOP_SECTIONS) || (it in IF_ELSE)) it else it.concat(":") })
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
                        values.addAll(grandParent.getNextAvailableFile(parent, parameters!!.getOriginalFile().getVirtualFile()!!.getParent()!!))
                    }
                }
                is Identifier -> {
                    var parentField = parent
                    while ((parentField !is Field) && (parentField !is CabalFile) && (parentField != null)) {
                        parentField = parentField?.getParent()
                    }
                    if (parentField is BuildDependsField) {
                        values.addAll(CabalInterface(current!!.getProject()).getInstalledPackagesList() map { it.name })
                    }
                }
            }

            for (value in values) {
                result?.addElement(LookupElementBuilder.create(value)!!.withCaseSensitivity(caseSensitivity)!!)
            }
        }
    }
}
