package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import org.jetbrains.haskell.debugger.frames.HaskellStackFrameInfo
import java.util.ArrayList
import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import org.jetbrains.haskell.debugger.frames.HaskellSuspendContext

/**
 * @author Habibullin Marat
 */
public class SequenceOfBacksCommand(val breakpoint: XLineBreakpoint<XBreakpointProperties<*>>?,
                                    val collectedFrames: ArrayList<HaskellStackFrameInfo>,
                                    var backStepsCounter: Int) : SuspendContextSetterCommand() {
    override fun getBytes(): ByteArray = ":back\n".toByteArray()

    override fun handleOutput(output: Deque<String?>, debugProcess: HaskellDebugProcess) {
        val histEntryFrame = Parser.tryParseLoggedBreakpointAt(output)
        if(histEntryFrame != null) {
            collectedFrames.add(histEntryFrame)
        } else {
            println("SequenceOfBacksCommand WARNING: Parser.tryParseLoggedBreakpointAt(output) returned null !")
        }
        if(backStepsCounter != 0) {
            backStepsCounter -= 1
            debugProcess.debugger.back(this)
        } else {
            setSuspendContext(breakpoint, collectedFrames, debugProcess)
        }
    }

}