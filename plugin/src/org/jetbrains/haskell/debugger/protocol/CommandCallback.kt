package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.ParseResult

/**
 * Created by vlad on 7/23/14.
 */

public abstract class CommandCallback<R: ParseResult?> {
    public abstract fun execAfterParsing(result: R)
}