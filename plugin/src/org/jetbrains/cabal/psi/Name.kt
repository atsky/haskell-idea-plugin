package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.Checkable

/**
 * Created by atsky on 13/12/13.
 */
public class Name(node: ASTNode) : ASTWrapperPsiElement(node), Checkable {

    public override fun isValidValue(): String? {
        return if (getNode()!!.getText()!!.matches("^([a-zA-Z0-9]+-)*[a-zA-Z0-9]+$")) null else "invalid name"
    }
}