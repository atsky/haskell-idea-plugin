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
import com.intellij.openapi.util.Key
import java.util.ArrayList
import org.jetbrains.haskell.debugger.parser.HsFilePosition
import org.jetbrains.haskell.debugger.parser.BreakInfo

public class HaskellLineBreakpointHandler(val project: Project,
                                          breakpointTypeClass: Class<out XBreakpointType<XLineBreakpoint<XBreakpointProperties<*>>, *>>,
                                          val debugProcess: HaskellDebugProcess)
: XBreakpointHandler<XLineBreakpoint<XBreakpointProperties<*>>>(breakpointTypeClass) {
    class object {
        public val PROJECT_KEY: Key<Project> = Key("org.jetbrains.haskell.debugger.breakpoints.ProjectForBreakpoint")
        public val BREAKS_LIST_KEY: Key<ArrayList<BreakInfo>> = Key("org.jetbrains.haskell.debugger.breakpoints.BreakListForBreakpoint")
        public val INDEX_IN_BREAKS_LIST_KEY: Key<Int> = Key("org.jetbrains.haskell.debugger.breakpoints.BreakListIndexForBreakpoint")
    }
    /**
     * Called when new breakpoint is added
     *
     * @param breakpoint added breakpoint
     */
    override fun registerBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>) {
        val breakpointLineNumber: Int? = getHaskellBreakpointLineNumber(breakpoint)
        if (breakpointLineNumber != null) {
            val moduleName = getModuleName(breakpoint)
            val breaksList = debugProcess.breakListForLine(moduleName, breakpointLineNumber)
            addUserData(breakpoint, breaksList)
            debugProcess.addBreakpoint(moduleName, breakpointLineNumber, breakpoint)
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

    private fun getModuleName(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>): String {
        val file = breakpoint.getSourcePosition()!!.getFile()
        try {
            return HaskellUtils.getModuleName(project, file)
        } catch (e: Exception) {
            val msg = "Module name is not spesified in file: ${file.getCanonicalPath()}"
            Notifications.Bus.notify(Notification("", "Debug execution error", msg, NotificationType.ERROR))
            throw e
        }
    }

    private fun getHaskellBreakpointLineNumber(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>): Int? {
        val lineNum = breakpoint.getSourcePosition()?.getLine()
        if (lineNum != null) {
            return HaskellUtils.zeroBasedToHaskellLineNumber(lineNum)
        }
        return null
    }

    private fun addUserData(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>, breaksList: ArrayList<BreakInfo>) {
        breakpoint.putUserData(PROJECT_KEY, project)
        breakpoint.putUserData(BREAKS_LIST_KEY, breaksList)
        breakpoint.putUserData(INDEX_IN_BREAKS_LIST_KEY, 0)
    }
}