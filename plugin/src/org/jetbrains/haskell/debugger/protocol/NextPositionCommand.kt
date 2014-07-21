package org.jetbrains.haskell.debugger.protocol

import java.util.Deque
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.frames.HaskellStackFrameInfo
import org.jetbrains.haskell.debugger.frames.HaskellSuspendContext
import java.util.ArrayList
import org.jetbrains.haskell.debugger.frames.ProgramThreadInfo

/**
 * Created by marat-x on 7/18/14.
 */
public abstract class NextPositionCommand : AbstractCommand() {
    protected fun getCurrentFrame(output: Deque<String?>): HaskellStackFrameInfo? {
        val filePosition = Parser.tryParseStoppedAt(output)
        if (filePosition != null) {
            return HaskellStackFrameInfo(filePosition)
        }
        return null
    }
}