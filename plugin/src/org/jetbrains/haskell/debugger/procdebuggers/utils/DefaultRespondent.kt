package org.jetbrains.haskell.debugger.procdebuggers.utils

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.frames.HsSuspendContext
import org.jetbrains.haskell.debugger.history.HistoryManager
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import org.jetbrains.haskell.debugger.utils.HaskellUtils
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.xdebugger.breakpoints.XBreakpoint

public class DefaultRespondent(val debugProcess: HaskellDebugProcess) : DebugRespondent {

    private val session = debugProcess.getSession()!!

    override fun traceFinished() = debugProcess.traceFinished()

    override fun positionReached(context: HsSuspendContext) = session.positionReached(context)

    override fun breakpointReached(breakpoint: XBreakpoint<*>,
                                   evaluatedLogExpression: String?,
                                   context: HsSuspendContext) {
        session.breakpointReached(breakpoint, evaluatedLogExpression, context)
    }

    override fun exceptionReached(context: HsSuspendContext) {
        val breakpoint = debugProcess.exceptionBreakpoint
        if (breakpoint == null) {
            session.positionReached(context)
        } else {
            session.breakpointReached(breakpoint, breakpoint.getLogExpression(), context)
        }
    }

    override fun getBreakpointAt(module: String, line: Int): XLineBreakpoint<XBreakpointProperties<*>>? =
            debugProcess.getBreakpointAtPosition(module, line)

    override fun setBreakpointNumberAt(breakpointNumber: Int, module: String, line: Int) =
            debugProcess.setBreakpointNumberAtLine(breakpointNumber, module, line)

    override fun getHistoryManager(): HistoryManager = debugProcess.historyManager

    override fun getModuleByFile(filename: String): String =
            HaskellUtils.getModuleName(session.getProject(), LocalFileSystem.getInstance()!!.findFileByPath(filename)!!)
}