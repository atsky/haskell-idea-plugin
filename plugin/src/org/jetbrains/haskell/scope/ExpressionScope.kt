package org.jetbrains.haskell.scope

import org.jetbrains.haskell.psi.Expression
import org.jetbrains.haskell.psi.SignatureDeclaration
import java.util.ArrayList
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.psi.RightHandSide
import org.jetbrains.haskell.psi.QVar

/**
 * Created by atsky on 11/21/14.
 */
public class ExpressionScope(val expression : Expression) {
    fun getVisibleVariables() : List<QVar> {
        val parent = expression.getParent()
        if (parent is Expression) {
            return ExpressionScope(parent).getVisibleVariables()
        } else {
            val result = ArrayList<QVar>()
            if (parent is RightHandSide) {
                val where = parent.getWhereBindings()
                if (where != null) {
                    val list1 = where.getSignatureDeclarationsList()
                    result.addAll(list1.flatMap { it.getValuesList() })

                    val list2 = where.getValueDefinitionList()
                    result.addAll(list2.map({ it.getQNameExpression()?.getQVar() }).filterNotNull())

                }
            }
            val module = Module.findModule(expression)
            if (module == null) {
                return listOf();
            }
            val signatureDeclaration = ModuleScope(module).getVisibleSignatureDeclaration()
            result.addAll(signatureDeclaration.map({ it.getQNameExpression()?.getQVar() }).filterNotNull())
            return result;
        }
    }
}