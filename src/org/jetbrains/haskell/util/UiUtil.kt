package org.jetbrains.haskell.util

import java.awt.GridBagConstraints

/**
 * @author Evgeny.Kurbatsky
 */
public inline fun gridBagConstraints(inline init : GridBagConstraints.() -> Unit) : GridBagConstraints {
    val result = GridBagConstraints();
    result.init();
    return result;
}

public inline fun GridBagConstraints.setConstraints(inline init : GridBagConstraints.() -> Unit) : GridBagConstraints {
    this.init();
    return this;
}