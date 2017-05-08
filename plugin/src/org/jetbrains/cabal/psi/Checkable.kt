package org.jetbrains.cabal.psi

import org.jetbrains.cabal.highlight.ErrorMessage

interface Checkable {
    fun check(): List<ErrorMessage> = listOf()
}