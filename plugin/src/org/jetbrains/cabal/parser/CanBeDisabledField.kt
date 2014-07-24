package org.jetbrains.cabal.parser

import org.jetbrains.cabal.parser.Field

public trait CanBeDisabledField: Field {
    public fun isEnabled(): String? { return null }
}