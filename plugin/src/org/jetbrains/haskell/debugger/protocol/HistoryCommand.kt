package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.GHCiDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque

/**
 * Created by vlad on 7/16/14.
 */

public class HistoryCommand : AbstractCommand() {

    override fun getBytes(): ByteArray {
        return ":hist\n".toByteArray()
    }

    override fun handleOutput(output: Deque<String?>, debugProcess: GHCiDebugProcess) {
        val history = Parser.parseHistory(output)
    }
}
