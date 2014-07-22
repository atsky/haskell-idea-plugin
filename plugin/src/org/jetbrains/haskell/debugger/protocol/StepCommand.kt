package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import com.intellij.xdebugger.frame.XSuspendContext
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo
import java.util.ArrayList
import org.jetbrains.haskell.debugger.frames.HsStackFrameInfo

/**
 * Created by vlad on 7/17/14.
 */

public abstract class StepCommand : NextPositionCommand() {

    override fun handleOutput(output: Deque<String?>, debugProcess: HaskellDebugProcess) {
        val topFrameInfo = getCurrentFrame(output)
        if (topFrameInfo != null) {
            sendHistCommand(null, topFrameInfo, debugProcess)
        }
    }
}
