package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.LocalBinding
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.BreakpointCommandResult
import org.jetbrains.haskell.debugger.frames.HsDebugValue
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.ProcessDebugger
import org.jetbrains.haskell.debugger.GHCiDebugger

/**
 * @author Habibullin Marat
 */
public class ForceCommand(private val bindingName: String, callback: CommandCallback<LocalBinding?>)
: RealTimeCommand<LocalBinding?>(callback) {
    override fun getText(): String = ":force $bindingName\n"

    override fun parseGHCiOutput(output: Deque<String?>): LocalBinding? = Parser.tryParseAnyPrintCommandOutput(output)

    class object {
        public class StandardForceCallback(val localBinding: LocalBinding, val syncObject: Lock, val bindingValueIsSet: Condition,
                                           val debugger: ProcessDebugger)
        : CommandCallback<LocalBinding?>() {
            override fun execAfterParsing(result: LocalBinding?) {
                syncObject.lock()
                try {
                    if(result != null && result.name != null && result.name == localBinding.name) {
                        localBinding.value = result.value
                    } else {
                        localBinding.value = ""
                    }
                    if (debugger is GHCiDebugger) {
                        debugger.markFramesAsObsolete()
                    }
                    bindingValueIsSet.signal()
                } finally {
                    syncObject.unlock()
                }
            }
        }
    }
}