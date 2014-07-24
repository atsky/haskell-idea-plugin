package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.BreakpointCommandResult

/**
 * @author Habibullin Marat
 */

public class SetBreakpointCommand(val module: String,
                                  val lineNumber: Int,
                                  callback: CommandCallback) : RealTimeCommand(callback) {

    override fun getBytes(): ByteArray = ":break $module $lineNumber\n".toByteArray()

    override fun parseOutput(output: Deque<String?>): ParseResult? = Parser.parseSetBreakpointCommandResult(output)

    class object {
        public class StandardSetBreakpointCallback(val module: String,
                                                   val debugProcess: HaskellDebugProcess) : CommandCallback() {
            override fun execAfterParsing(result: ParseResult?) {
                if (result != null && result is BreakpointCommandResult) {
                    debugProcess.setBreakpointNumberAtLine(result.breakpointNumber, module, result.position.startLine)
                }
            }
        }
    }
}
