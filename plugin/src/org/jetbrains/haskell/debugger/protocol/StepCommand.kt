package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.GHCiDebugProcess
import com.intellij.xdebugger.frame.XSuspendContext
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque
import org.jetbrains.haskell.debugger.HaskellSuspendContext
import org.jetbrains.haskell.debugger.ProgramThreadInfo
import java.util.ArrayList
import org.jetbrains.haskell.debugger.HaskellStackFrameInfo

/**
 * Created by vlad on 7/17/14.
 */

public abstract class StepCommand : AbstractCommand() {


    override fun handleOutput(output: Deque<String?>, debugProcess: GHCiDebugProcess) {
        val filePosition = Parser.tryParseStoppedAt(output)
        if (filePosition != null) {
            val frames = ArrayList<HaskellStackFrameInfo>()
            frames.add(HaskellStackFrameInfo(filePosition))
            // "Main" is temporary name
            val context = HaskellSuspendContext(ProgramThreadInfo(null, "Main", frames))
            debugProcess.getSession()!!.positionReached(context)
        }
    }
}
