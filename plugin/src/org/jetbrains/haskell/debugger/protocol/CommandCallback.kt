package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.ParseResult

/**
 * Created by vlad on 7/23/14.
 */

abstract class CommandCallback<R: ParseResult?> {
    abstract fun execAfterParsing(result: R)

    open fun execBeforeSending() {
    }
}