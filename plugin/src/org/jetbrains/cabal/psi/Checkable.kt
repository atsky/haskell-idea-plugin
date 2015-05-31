package org.jetbrains.cabal.psi

import org.jetbrains.cabal.highlight.ErrorMessage

public interface Checkable {
    fun check(): List<ErrorMessage> = listOf()
}