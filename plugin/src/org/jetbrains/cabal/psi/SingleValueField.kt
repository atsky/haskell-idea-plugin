package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.Field
import org.jetbrains.cabal.highlight.ErrorMessage
import com.intellij.psi.impl.source.tree.SharedImplUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import java.util.ArrayList
import com.intellij.psi.util.PsiTreeUtil

public open class SingleValueField(node: ASTNode) : PropertyField(node) {

    public open fun checkUniqueness(): ErrorMessage? {
        fun isSame(field: PsiElement) = (field is PropertyField) && (field.hasName(getFieldName()))

        if ((getParent()!!.getChildren() filter { isSame(it) }).size() > 1) {
            return ErrorMessage(getKeyNode(), "duplicate field", "error")
        }
        return null
    }

    public fun getValue(): PropertyValue? {
        return PsiTreeUtil.findChildOfType(this, javaClass<PropertyValue>())
    }
}
