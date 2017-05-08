package org.jetbrains.haskell.scope

import org.jetbrains.haskell.psi.Expression
import org.jetbrains.haskell.psi.SignatureDeclaration
import java.util.ArrayList
import org.jetbrains.haskell.psi.Module
import org.jetbrains.haskell.psi.RightHandSide
import org.jetbrains.haskell.psi.QVar
import org.jetbrains.haskell.psi.ValueDefinition
import org.jetbrains.haskell.psi.QNameExpression
import org.jetbrains.haskell.psi.ExpressionStatement
import org.jetbrains.haskell.psi.UnguardedRHS
import org.jetbrains.haskell.psi.CaseAlternative
import org.jetbrains.haskell.psi.LambdaExpression
import org.jetbrains.haskell.psi.Statement
import org.jetbrains.haskell.psi.Guard
import org.jetbrains.haskell.psi.DoExpression
import org.jetbrains.haskell.psi.LetStatement
import org.jetbrains.haskell.psi.BindStatement

/**
 * Created by atsky on 11/21/14.
 */
class ExpressionScope(val expression: Expression) {
    fun getVisibleVariables(): List<QVar> {
        val parent = expression.parent
        val result = ArrayList<QVar>()

        if (parent is Expression) {
            if (parent is LambdaExpression) {
                for (pattern in parent.getPatterns()) {
                    traverseExpression(pattern.getExpression(), result)
                }
            }
            result.addAll(ExpressionScope(parent).getVisibleVariables())
            return result
        } else if (parent is Statement) {
            return getStatementScopedDeclarations(parent)
        } else if (parent is Guard) {
            addRightHandSide(parent.getParent() as RightHandSide, result)
        } else if (parent is UnguardedRHS) {
            val caseAlternative = parent.getParent() as? CaseAlternative
            if (caseAlternative != null) {
                traverseExpression(caseAlternative.getExpressions().firstOrNull(), result)
                result.addAll(ExpressionScope(caseAlternative.parent as Expression).getVisibleVariables())
                return result
            }
        } else if (parent is RightHandSide) {
            addRightHandSide(parent, result)
            val letStatement = parent.getLetStatement()
            if (letStatement != null) {
                return getStatementScopedDeclarations(letStatement)
            } else {
                return getModuleScopedDeclarations(result)
            }
        }

        return getModuleScopedDeclarations(result)
    }

    fun getStatementScopedDeclarations(statement: Statement): List<QVar> {
        val result = ArrayList<QVar>()
        val parent = statement.parent
        if (parent is DoExpression) {
            val statementList = parent.getStatementList()
            val index = statementList.indexOf(statement)

            for (st in statementList.subList(0, index)) {
                if (st is LetStatement) {
                    val list = st.getValueDefinitions()
                            .map { it.getQNameExpression()?.getQVar() }.filterNotNull()
                    result.addAll(list)
                } else if (st is BindStatement) {
                    val qVar = st.getQVar()
                    if (qVar != null) {
                        result.add(qVar)
                    }
                }
            }
            result.addAll(ExpressionScope(parent).getVisibleVariables())
            return result
        } else if (parent is Guard) {
            addRightHandSide(parent.getParent() as RightHandSide, result)
        }
        return getModuleScopedDeclarations(result)
    }

    fun getModuleScopedDeclarations(result: ArrayList<QVar>): List<QVar> {
        val module = Module.findModule(expression)
        if (module == null) {
            return listOf()
        }
        val signatureDeclaration = ModuleScope(module).getVisibleSignatureDeclaration()
        result.addAll(signatureDeclaration.map({ it.getQNameExpression()?.getQVar() }).filterNotNull())
        val foreignDeclaration = ModuleScope(module).getVisibleForeignDeclarations()
        result.addAll(foreignDeclaration.map({ it.getQVar() }).filterNotNull())
        return result
    }

    private fun addRightHandSide(rhs: RightHandSide, result: ArrayList<QVar>) {
        val where = rhs.getWhereBindings()
        if (where != null) {
            val list1 = where.getSignatureDeclarationsList()
            result.addAll(list1.flatMap { it.getValuesList() })

            val list2 = where.getValueDefinitionList()
            result.addAll(list2.map({ it.getQNameExpression()?.getQVar() }).filterNotNull())
        }
        val parent = rhs.parent
        if (parent is ValueDefinition) {
            val valueDefinition: ValueDefinition = parent
            traverseExpression(valueDefinition.getExpression(), result)
        }
    }

    private fun traverseExpression(expression: Expression?, result: ArrayList<QVar>) {
        expression?.traverse { node ->
            if (node is QNameExpression) {
                val qVar = node.getQVar()
                if (qVar != null) {
                    result.add(qVar)
                }
            }
        }
    }
}