package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import com.intellij.extapi.psi.ASTWrapperPsiElement
import org.jetbrains.cabal.psi.PropertyValue
import org.jetbrains.cabal.psi.VersionConstraint
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.PsiElement
import org.jetbrains.cabal.parser.CabalTokelTypes
import java.lang.IllegalStateException

class ComplexVersionConstraint(node: ASTNode) : PropertyValue(node)  {

    fun satisfyConstraint(givenVersion: String): Boolean {
        val elements = children.filter { it is VersionConstraint }

        fun findLogicOrNullFromRange(startOffset: Int, endOffset: Int): PsiElement? {
            for (i in startOffset..endOffset - 1) {
                val res = findElementAt(i)
                if ((res?.node?.elementType == CabalTokelTypes.LOGIC)) return res
            }
            return null
        }

        fun checkConstraint(i: Int) = (elements[i] as VersionConstraint).satisfyConstraint(givenVersion)

        if (elements.size == 0) throw IllegalStateException()
        var res = checkConstraint(0)

        for (i in elements.indices) {
            if (i == 0) continue
            val logic = findLogicOrNullFromRange(elements[i - 1].textRange!!.endOffset - textOffset, elements[i].startOffsetInParent)?.text
            if (logic == null) throw IllegalStateException()
            if (logic == "||") {
                res = res || checkConstraint(i)
            }
            else {
                res = res && checkConstraint(i)
            }
        }
        return res
    }
}