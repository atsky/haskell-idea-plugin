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
import org.jetbrains.haskell.debugger.utils.UIUtils
import com.intellij.openapi.vfs.VirtualFile

class HaskellLineBreakpointHandler(val project: Project,
                                          breakpointTypeClass: Class<out XBreakpointType<XLineBreakpoint<XBreakpointProperties<*>>, *>>,
                                          val debugProcess: HaskellDebugProcess)
: XBreakpointHandler<XLineBreakpoint<XBreakpointProperties<*>>>(breakpointTypeClass) {
    companion object {
        val PROJECT_KEY: Key<Project> = Key("org.jetbrains.haskell.debugger.breakpoints.ProjectForBreakpoint")
        val BREAKS_LIST_KEY: Key<ArrayList<BreakInfo>> =
                                               Key("org.jetbrains.haskell.debugger.breakpoints.BreakListForBreakpoint")
        val INDEX_IN_BREAKS_LIST_KEY: Key<Int> =
                Key("org.jetbrains.haskell.debugger.breakpoints.BreakListIndexForBreakpoint")
    }

    private val GET_MODULE_ERR_TITLE = "Debug execution error"
    private fun GET_MODULE_ERR_MSG(file: VirtualFile) =
                                       "Module name is not spesified in file: ${file.canonicalPath}"
    private val REMOVE_WARN_TITLE = "Remove breakpoint warning"
    private val REMOVE_WARN_MSG =   "Attempt to remove breakpoint while debugger is busy." +
                                    "Removing action will take effect only after next command"
    /**
     * Called when new breakpoint is added
     *
     * @param breakpoint added breakpoint
     */
    override fun registerBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>) {
        val breakpointLineNumber: Int? = getHaskellBreakpointLineNumber(breakpoint)
        if (breakpointLineNumber != null) {
            val moduleName = getModuleName(breakpoint)
            val breaksList = debugProcess.syncBreakListForLine(moduleName, breakpointLineNumber)
            addUserData(breakpoint, breaksList)
            debugProcess.addBreakpoint(moduleName, breakpointLineNumber, breakpoint)
        }
    }

    /**
     * Called when breakpoint is removed. If debugger is busy when this method is called, remove action will be
     * performed any way and warning will be shown to user. Actually, removing should be forbidden but it requires
     * control over UI part of breakpoint removing and I don't know how to do it for now
     *
     * @param breakpoint breakpoint to remove
     */
    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>, isTemporary: Boolean) {
        if(!debugProcess.isReadyForNextCommand()) {
            Notifications.Bus.notify(Notification("", REMOVE_WARN_TITLE, REMOVE_WARN_MSG, NotificationType.WARNING))
        }
        val breakpointLineNumber: Int? = getHaskellBreakpointLineNumber(breakpoint)
        if (breakpointLineNumber != null) {
            debugProcess.removeBreakpoint(HaskellUtils.getModuleName(project, breakpoint.sourcePosition!!.file),
                    breakpointLineNumber)
        }
    }

    private fun getModuleName(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>): String {
        val file = breakpoint.sourcePosition!!.file
        try {
            return HaskellUtils.getModuleName(project, file)
        } catch (e: Exception) {
            Notifications.Bus.notify(Notification("", GET_MODULE_ERR_TITLE, GET_MODULE_ERR_MSG(file), NotificationType.ERROR))
            throw e
        }
    }

    private fun getHaskellBreakpointLineNumber(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>): Int? {
        val lineNum = breakpoint.sourcePosition?.line
        if (lineNum != null) {
            return HaskellUtils.zeroBasedToHaskellLineNumber(lineNum)
        }
        return null
    }

    /**
     * Adds some data that will be used in select breakpoint mechanism
     */
    private fun addUserData(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>, breaksList: ArrayList<BreakInfo>) {
        breakpoint.putUserData(PROJECT_KEY, project)
        breakpoint.putUserData(BREAKS_LIST_KEY, breaksList)
        breakpoint.putUserData(INDEX_IN_BREAKS_LIST_KEY, 0)
    }
}