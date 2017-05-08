package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.ParseResult
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import java.util.Deque
import org.jetbrains.haskell.debugger.utils.SyncObject
import org.jetbrains.haskell.debugger.parser.GHCiParser
import org.jetbrains.haskell.debugger.parser.JSONConverter

/**
 * Command that is used to make synchronous requests to debugger. Contains SyncObject. Before parsed result is handled
 * (execAfterParsing method of callback is called) SyncObject is locked and after handling SyncObject sends notification
 * and unlocks itself. So command caller can wait a signal on passed SyncObject and understand that command has been
 * finished by getting the signal
 *
 * @see org.jetbrains.haskell.debugger.utils.SyncObject
 *
 * @author Habibullin Marat
 */
abstract class SyncCommand<T : ParseResult?>(private val syncObject: SyncObject, callback: CommandCallback<T>)
: AbstractCommand<T>(callback) {
    override fun handleGHCiOutput(output: Deque<String?>) {
        val result = parseGHCiOutput(output)
        syncExecAfterParsing(result)
    }

    override fun handleJSONOutput(output: String) {
        val result = parseJSONOutput(JSONConverter.parseJSONObject(output).json)
        syncExecAfterParsing(result)
    }

    private fun syncExecAfterParsing(result: T) {
        syncObject.lock()
        try {
            callback?.execAfterParsing(result)
        } finally {
            syncObject.signal()
            syncObject.unlock()
        }
    }
}