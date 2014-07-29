package org.jetbrains.cabal.psi

public trait Checkable {
    fun isValidValue(): String? = null
}