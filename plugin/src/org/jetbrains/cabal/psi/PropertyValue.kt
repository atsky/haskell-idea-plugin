package org.jetbrains.cabal.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement

public trait PropertyValue: ASTWrapperPsiElement {

    public override fun getText(): String = getNode().getText()!!

}
