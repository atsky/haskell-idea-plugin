package org.jetbrains.yesod.hamlet.psi;

/**
 * @author Leyla H
 */

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class AttributeValue extends ASTWrapperPsiElement{
    public AttributeValue(ASTNode node) {
        super(node);
    }
}