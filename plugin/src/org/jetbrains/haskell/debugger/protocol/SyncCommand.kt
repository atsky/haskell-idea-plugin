package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.ParseResult
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import java.util.Deque
import org.jetbrains.haskell.debugger.utils.SyncObject
import org.jetbrains.haskell.debugger.parser.Parser

/**
 * @author Habibullin Marat
 */
public abstract class SyncCommand<T : ParseResult?>(callback: SyncCommandCallback<T>) : AbstractCommand<T>(callback) {
    override fun handleGHCiOutput(output: Deque<String?>) {
        val result = parseGHCiOutput(output)
        syncExecAfterParsing(result)
    }

    override fun handleJSONOutput(output: String) {
        val result = parseJSONOutput(Parser.parseJSONObject(output).json)
        syncExecAfterParsing(result)
    }

    private fun syncExecAfterParsing(result: T) {
        (callback as SyncCommandCallback).syncObject.lock()
        try {
            callback?.execAfterParsing(result)
        } finally {
            (callback as SyncCommandCallback).syncObject.signal()
            (callback as SyncCommandCallback).syncObject.unlock()
        }
    }
}

public abstract class SyncCommandCallback<R: ParseResult?>(public val syncObject: SyncObject)
: CommandCallback<R>() {}