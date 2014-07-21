package org.jetbrains.cabal.parser

public trait Checkable {
    fun isValidPSIElem(): Boolean = true
    fun isValidValue(): String? = null
}