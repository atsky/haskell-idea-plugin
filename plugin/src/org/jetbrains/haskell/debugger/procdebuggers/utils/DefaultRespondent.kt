package org.jetbrains.haskell.debugger.procdebuggers.utils

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.utils.HaskellUtils
import com.intellij.openapi.vfs.LocalFileSystem
import org.jetbrains.haskell.debugger.frames.HsHistoryFrame
import org.jetbrains.haskell.debugger.parser.HistoryResult
import org.jetbrains.haskell.debugger.parser.HsHistoryFrameInfo
import org.jetbrains.haskell.debugger.breakpoints.HaskellLineBreakpointDescription

class DefaultRespondent(val debugProcess: HaskellDebugProcess) : DebugRespondent {

    private val session = debugProcess.session!!

    override fun traceFinished() = debugProcess.traceFinished()

    override fun positionReached(context: HsSuspendContext) = session.positionReached(context)

    override fun breakpointReached(breakpoint: HaskellLineBreakpointDescription,
                                   context: HsSuspendContext) {
        val realBreakpoint = debugProcess.getBreakpointAtPosition(breakpoint.module, breakpoint.line)
        if (realBreakpoint == null) {
            session.positionReached(context)
        } else {
            session.breakpointReached(realBreakpoint, realBreakpoint.logExpression, context)
        }
    }

    override fun exceptionReached(context: HsSuspendContext) {
        val breakpoint = debugProcess.exceptionBreakpoint
        if (breakpoint == null) {
            session.positionReached(context)
        } else {
            session.breakpointReached(breakpoint, breakpoint.logExpression, context)
        }
    }

    override fun breakpointRemoved() { }

    override fun getBreakpointAt(module: String, line: Int): HaskellLineBreakpointDescription? {
        val breakpoint = debugProcess.getBreakpointAtPosition(module, line)
        if (breakpoint == null) {
            return null
        } else {
            return HaskellLineBreakpointDescription(module, line, breakpoint.condition)
        }
    }

    override fun setBreakpointNumberAt(breakpointNumber: Int, module: String, line: Int) =
            debugProcess.setBreakpointNumberAtLine(breakpointNumber, module, line)

    override fun resetHistoryStack() {}//= debugProcess.historyManager.resetHistoryStack()

    override fun historyChange(currentFrame: HsHistoryFrame, history: HistoryResult?) {
        //debugProcess.historyManager.historyFrameAppeared(currentFrame)
        //if (history != null) {
        //    debugProcess.historyManager.setHistoryFramesInfo(
        //            HsHistoryFrameInfo(0, currentFrame.stackFrameInfo.functionName,
        //                    currentFrame.stackFrameInfo.filePosition), history.frames, history.full)
        //}
        //debugProcess.historyManager.historyChanged(false, true, currentFrame)
    }

    override fun getModuleByFile(filename: String): String =
            HaskellUtils.getModuleName(session.project, LocalFileSystem.getInstance()!!.findFileByPath(filename)!!)
}