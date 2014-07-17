package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.GHCiDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque

/**
 * @author Habibullin Marat
 */

public class SetBreakpointCommand(val lineNumber: Int) : AbstractCommand() {
    override fun getBytes(): ByteArray {
        return ":break $lineNumber\n".toByteArray()
    }

    override fun handleOutput(output: Deque<String?>, debugProcess: GHCiDebugProcess) {
        val result = Parser.parseSetBreakpointCommandResult(output)
        debugProcess.setBreakpointNumberAtLine(result.breakpointNumber, result.position.startLine + 1)
        debugProcess.debugger.lastCommand = null
    }
}
