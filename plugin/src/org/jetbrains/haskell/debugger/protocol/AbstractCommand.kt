package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.GHCiParser
import org.json.simple.JSONObject
import org.jetbrains.haskell.debugger.parser.JSONConverter

/**
 * Base class for any command. Commands are used to communicate with ghci. Type parameter R is a type of parsing result
 * that is performed when command handles ghci output. This result of type R than passed to callback for handling
 *
 * Created by vlad on 7/10/14.
 */

public abstract class AbstractCommand<R : ParseResult?>(public val callback: CommandCallback<R>?) {

    public abstract fun getText(): String

    protected abstract fun parseGHCiOutput(output: Deque<String?>): R

    protected abstract fun parseJSONOutput(output: JSONObject): R

//    public abstract fun clone(callback: CommandCallback<R>):

    public open fun handleGHCiOutput(output: Deque<String?>) {
        val result = parseGHCiOutput(output)
        callback?.execAfterParsing(result)
    }

    public open fun handleJSONOutput(output: String) {
        val result = parseJSONOutput(JSONConverter.parseJSONObject(output).json)
        callback?.execAfterParsing(result)
    }
}