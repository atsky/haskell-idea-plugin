package org.jetbrains.haskell.debugger.breakpoints

import com.intellij.xdebugger.breakpoints.ui.XBreakpointCustomPropertiesPanel
import com.intellij.xdebugger.breakpoints.XBreakpoint
import javax.swing.JComponent
import com.intellij.openapi.ui.ComboBox
import javax.swing.DefaultComboBoxModel
import javax.swing.JPanel
import org.jetbrains.haskell.util.gridBagConstraints
import java.awt.Insets
import javax.swing.JLabel
import org.jetbrains.haskell.util.setConstraints
import java.awt.GridBagConstraints
import javax.swing.Box
import java.awt.GridBagLayout
import org.jetbrains.haskell.debugger.utils.UIUtils

/**
 * Created by vlad on 8/6/14.
 */

class HaskellExceptionBreakpointPropertiesPanel :
        XBreakpointCustomPropertiesPanel<XBreakpoint<HaskellExceptionBreakpointProperties>>() {

    companion object {
        private val ITEM_EXCEPTION = "Any thrown exceptions"
        private val ITEM_ERROR = "Uncaught exceptions"
    }

    private val selectDebuggerComboBox: ComboBox<*> = ComboBox(DefaultComboBoxModel(arrayOf(ITEM_EXCEPTION, ITEM_ERROR)))

    override fun getComponent(): JComponent {
        val panel = JPanel(GridBagLayout())
        UIUtils.addLabeledControl(panel, 0, "Breakpoint type:     ", selectDebuggerComboBox)
        return panel
    }

    override fun saveTo(breakpoint: XBreakpoint<HaskellExceptionBreakpointProperties>) {
        val old = breakpoint.properties!!.state.exceptionType
        val new =
                if (selectDebuggerComboBox.selectedIndex == 0) HaskellExceptionBreakpointProperties.ExceptionType.EXCEPTION
                else HaskellExceptionBreakpointProperties.ExceptionType.ERROR
        breakpoint.properties!!.state.exceptionType = new
        if (old != new) {
            breakpoint.isEnabled = false
            breakpoint.isEnabled = true
        }
    }

    override fun loadFrom(breakpoint: XBreakpoint<HaskellExceptionBreakpointProperties>) {
        selectDebuggerComboBox.selectedIndex = if (breakpoint.properties!!.state.exceptionType ==
                HaskellExceptionBreakpointProperties.ExceptionType.EXCEPTION) 0 else 1
    }
}