package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class Identifier extends ASTWrapperPsiElement{
    public Identifier (ASTNode node) {
        super(node);
    }
}
