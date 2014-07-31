package org.jetbrains.haskell.debugger.breakpoints

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointType
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import org.jetbrains.haskell.debugger.utils.HaskellUtils
import com.intellij.openapi.project.Project
import com.intellij.notification.Notifications
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType

public class HaskellLineBreakpointHandler(val project: Project,
                                          breakpointTypeClass: Class<out XBreakpointType<XLineBreakpoint<XBreakpointProperties<*>>, *>>,
                                          val debugProcess: HaskellDebugProcess)
: XBreakpointHandler<XLineBreakpoint<XBreakpointProperties<*>>>(breakpointTypeClass) {
    /**
     * Called when new breakpoint is added
     *
     * @param breakpoint added breakpoint
     */
    override fun registerBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>) {
        val breakpointLineNumber: Int? = getHaskellBreakpointLineNumber(breakpoint)
        if (breakpointLineNumber != null) {
            val file = breakpoint.getSourcePosition()!!.getFile()
            val modName: String
            try {
                modName = HaskellUtils.getModuleName(project, file)
            } catch (e: Exception) {
                val msg =  "Module name is not spesified in file: ${file.getCanonicalPath()}"
                Notifications.Bus.notify(Notification("", "Debug execution error", msg, NotificationType.ERROR))
                throw e
            }
            debugProcess.addBreakpoint(modName, breakpointLineNumber, breakpoint)
        }
    }

    /**
     * Called when breakpoint is removed
     *
     * @param breakpoint breakpoint to remove
     */
    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>, isTemporary: Boolean) {
        val breakpointLineNumber: Int? = getHaskellBreakpointLineNumber(breakpoint)
        if (breakpointLineNumber != null) {
            debugProcess.removeBreakpoint(HaskellUtils.getModuleName(project, breakpoint.getSourcePosition()!!.getFile()),
                    breakpointLineNumber)
        }
    }

    private fun getHaskellBreakpointLineNumber(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>): Int? {
        val lineNum = breakpoint.getSourcePosition()?.getLine()
        if (lineNum != null) {
            return HaskellUtils.zeroBasedToHaskellLineNumber(lineNum)
        }
        return null
    }
}