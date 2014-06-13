package org.jetbrains.cabal.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.parser.*
import java.util.*


public open class CabalCompletionContributor() : CompletionContributor() {
    public override fun fillCompletionVariants(parameters: CompletionParameters?, result: CompletionResultSet?): Unit {
        if (parameters?.getCompletionType() == CompletionType.BASIC) {
            val values = ArrayList<String>()
            values.addAll(PKG_DESCR_FIELD_DESCRS)
            values.addAll(BUILD_INFO)
            for (value in values) {
                result?.addElement(LookupElementBuilder.create(value + ": ")!!)
            }
        }
    }


}
