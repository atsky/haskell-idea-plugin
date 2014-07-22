package org.jetbrains.cabal.parser

public trait Checkable {
    fun isValidValue(): String? = null
}