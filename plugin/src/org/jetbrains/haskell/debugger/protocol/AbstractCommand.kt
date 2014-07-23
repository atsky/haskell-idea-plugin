package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult

/**
 * Created by vlad on 7/10/14.
 */

public abstract class AbstractCommand(private val callback: CommandCallback?) {

    public abstract fun getBytes(): ByteArray

    protected abstract fun parseOutput(output: Deque<String?>): ParseResult?

    public fun handleOutput(output: Deque<String?>) {
        val result = parseOutput(output)
        callback?.execAfterHandling(result)
    }

}