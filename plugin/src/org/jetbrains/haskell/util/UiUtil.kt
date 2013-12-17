package org.jetbrains.haskell.util

import java.awt.GridBagConstraints

/**
 * @author Evgeny.Kurbatsky
 */
public inline fun gridBagConstraints(init : GridBagConstraints.() -> Unit) : GridBagConstraints {
    val result = GridBagConstraints();
    result.init();
    return result;
}

public inline fun GridBagConstraints.setConstraints(init : GridBagConstraints.() -> Unit) : GridBagConstraints {
    this.init();
    return this;
}