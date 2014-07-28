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
                                  callback: CommandCallback<BreakpointCommandResult?>)
: RealTimeCommand<BreakpointCommandResult?>(callback) {

    override fun getBytes(): ByteArray = ":break $module $lineNumber\n".toByteArray()

    override fun parseOutput(output: Deque<String?>): BreakpointCommandResult? = Parser.parseSetBreakpointCommandResult(output)

    class object {
        public class StandardSetBreakpointCallback(val module: String,
                                                   val debugProcess: HaskellDebugProcess)
                                                   : CommandCallback<BreakpointCommandResult?>() {
            override fun execAfterParsing(result: BreakpointCommandResult?) {
                if (result != null) {
                    debugProcess.setBreakpointNumberAtLine(result.breakpointNumber, module, result.position.startLine)
                }
            }
        }
    }
}
