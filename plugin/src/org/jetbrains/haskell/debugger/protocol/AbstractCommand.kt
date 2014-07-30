package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.RemoteDebugger

/**
 * Base class for any command. Commands are used to communicate with ghci. Type parameter R is a type of parsing result
 * that is performed when command handles ghci output. This result of type R than passed to callback for handling
 *
 * Created by vlad on 7/10/14.
 */

public abstract class AbstractCommand<R : ParseResult?>(private val callback: CommandCallback<R>?) {

    public abstract fun getBytes(): ByteArray

    protected abstract fun parseGHCiOutput(output: Deque<String?>): R

    /**
     * @param jsonHandler temporary decision for handling to types of output
     */
    public fun handleOutput(output: Deque<String?>, jsonHandler: RemoteDebugger.JSONHandler? = null) {
        if (jsonHandler == null) {
            val result = parseGHCiOutput(output)
            callback?.execAfterParsing(result)
        } else {
            assert(output.size == 1)
            val result = Parser.parseJSONObject(output.getFirst()!!)
            jsonHandler.handle(result.json)
        }
    }
}