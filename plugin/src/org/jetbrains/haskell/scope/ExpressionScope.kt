package org.jetbrains.haskell.scope

import org.jetbrains.haskell.psi.Expression
import org.jetbrains.haskell.psi.SignatureDeclaration
import java.util.ArrayList
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.psi.RightHandSide

/**
 * Created by atsky on 11/21/14.
 */
public class ExpressionScope(val expression : Expression) {
    fun getVisibleSignatureDeclarations() : List<SignatureDeclaration> {
        val parent = expression.getParent()
        if (parent is Expression) {
            return ExpressionScope(parent).getVisibleSignatureDeclarations()
        } else {
            val result = ArrayList<SignatureDeclaration>()
            if (parent is RightHandSide) {
                val list = parent.getWhereBindings()?.getSignatureDeclarationsList()
                if (list != null) {
                    result.addAll(list)
                }
            }
            val module = Module.findModule(expression)
            if (module == null) {
                return listOf();
            }
            result.addAll(ModuleScope(module).getVisibleSignatureDeclaration())
            return result;
        }
    }
}