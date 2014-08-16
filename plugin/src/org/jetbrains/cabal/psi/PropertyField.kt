package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.Field
import org.jetbrains.cabal.highlight.ErrorMessage
import com.intellij.psi.impl.source.tree.SharedImplUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import java.util.ArrayList
import com.intellij.psi.util.PsiTreeUtil

public open class PropertyField(node: ASTNode) : Field(node) {

    public fun getKeyNode(): PsiElement = getFirstChild()!!

    public fun getPropertyName(): String = getKeyNode().getText()!!

//    public fun getValue(): PropertyValue? {
//        return PsiTreeUtil.findChildOfType(this, javaClass<PropertyValue>())
//    }

    public fun <T : PsiElement> getValues(valueType: Class<T>): List<T> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, valueType)
    }
}
