package org.jetbrains.haskell.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.haskell.scope.ModuleScope

/**
 * Created by atsky on 4/25/14.
 */
public open class Expression(node : ASTNode) : ASTWrapperPsiElement(node) {

    public fun getVisibleValues() : List<ValueName> {
        var current : PsiElement = this.getParent()!!;
        if (current is Expression) {
            return (current as Expression).getVisibleValues()
        } else {
            val module = Module.findModule(current)
            if (module == null) {
                return listOf()
            }
            return ModuleScope(module).getVisibleValues().flatMap { it.getNames() }
        }
    }
}
