package org.jetbrains.cabal.psi

import org.jetbrains.cabal.highlight.ErrorMessage

public trait Checkable {
    fun check(): List<ErrorMessage> = listOf()
}