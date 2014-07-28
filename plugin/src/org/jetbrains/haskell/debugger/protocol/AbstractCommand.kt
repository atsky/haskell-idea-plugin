package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult

/**
 * Base class for any command. Commands are used to communicate with ghci. Type parameter R is a type of parsing result
 * that is performed when command handles ghci output. This result of type R than passed to callback for handling
 *
 * Created by vlad on 7/10/14.
 */

public abstract class AbstractCommand<R : ParseResult?>(private val callback: CommandCallback<R>?) {

    public abstract fun getBytes(): ByteArray

    protected abstract fun parseOutput(output: Deque<String?>): R

    public fun handleOutput(output: Deque<String?>) {
        val result = parseOutput(output)
        callback?.execAfterParsing(result)
    }

}