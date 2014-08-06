package org.jetbrains.haskell.debugger.breakpoints

import com.intellij.xdebugger.breakpoints.XBreakpointType
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XBreakpoint
import javax.swing.Icon
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import org.jetbrains.haskell.debugger.HaskellDebuggerEditorsProvider
import javax.swing.JComponent
import com.intellij.xdebugger.breakpoints.ui.XBreakpointCustomPropertiesPanel
import com.intellij.debugger.ui.breakpoints.ExceptionBreakpointPropertiesPanel
import javax.swing.JPanel
import javax.swing.JLabel

/**
 * Created by vlad on 8/6/14.
 */

public class HaskellExceptionBreakpointType() :
        XBreakpointType<XBreakpoint<HaskellExceptionBreakpointProperties>, HaskellExceptionBreakpointProperties>(
                HaskellExceptionBreakpointType.ID,
                HaskellExceptionBreakpointType.TITLE) {

    class object {
        public val ID: String = "haskell-exception-breakpoint"
        private val TITLE: String = "Haskell exception breakpoints"
    }


    override fun getEnabledIcon(): Icon {
        return AllIcons.Debugger.Db_exception_breakpoint
    }

    override fun getDisabledIcon(): Icon {
        return AllIcons.Debugger.Db_disabled_exception_breakpoint
    }

    override fun getDisplayText(breakpoint: XBreakpoint<HaskellExceptionBreakpointProperties>?): String? {
        return "Any exception"
    }

    override fun createProperties(): HaskellExceptionBreakpointProperties? {
        return HaskellExceptionBreakpointProperties()
    }

    override fun createCustomPropertiesPanel(): XBreakpointCustomPropertiesPanel<XBreakpoint<HaskellExceptionBreakpointProperties>> {
        return HaskellExceptionBreakpointPropertiesPanel()
    }

    override fun createDefaultBreakpoint(creator: XBreakpointType.XBreakpointCreator<HaskellExceptionBreakpointProperties>):
            XBreakpoint<HaskellExceptionBreakpointProperties> {
        return creator.createBreakpoint(HaskellExceptionBreakpointProperties())
    }
}