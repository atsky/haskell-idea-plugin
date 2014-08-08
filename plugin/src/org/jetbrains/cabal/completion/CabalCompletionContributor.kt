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
import org.jetbrains.cabal.psi.BoolField
import org.jetbrains.cabal.psi.Path
import org.jetbrains.cabal.psi.PathsField
import org.jetbrains.cabal.psi.InvalidValue
import org.jetbrains.cabal.psi.RangedValue
import org.jetbrains.cabal.psi.Section
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
                is PsiFile -> {
                    values.addAll(PKG_DESCR_FIELD_DESCRS map {it.concat(":")})
                    values.addAll(TOP_SECTIONS map {it.concat(":")})
                    caseSensitivity = false
                }
                is Section -> {
                    values.addAll(parent.getAvailableFieldNames() map {it.concat(":")})
                    caseSensitivity = false
                }
                is RangedValue -> {
                    values.addAll(parent.getAvailableValues())
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
            }

            for (value in values) {
                result?.addElement(LookupElementBuilder.create(value)!!.withCaseSensitivity(caseSensitivity)!!)
            }
        }
    }


}
