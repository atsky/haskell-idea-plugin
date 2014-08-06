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

/**
 * Created by vlad on 8/6/14.
 */

public class HaskellExceptionBreakpointPropertiesPanel :
        XBreakpointCustomPropertiesPanel<XBreakpoint<HaskellExceptionBreakpointProperties>>() {

    class object {
        private val ITEM_EXCEPTION = "Any thrown exceptions"
        private val ITEM_ERROR = "Uncaught exceptions"
    }

    private val selectDebuggerComboBox: ComboBox = ComboBox(DefaultComboBoxModel(array(ITEM_EXCEPTION, ITEM_ERROR)))

    override fun getComponent(): JComponent {
        val panel = JPanel(GridBagLayout())
        addLabeledControl(panel, 0, "Breakpoint type:     ", selectDebuggerComboBox)
        return panel
    }

    override fun saveTo(breakpoint: XBreakpoint<HaskellExceptionBreakpointProperties>) {
        val old = breakpoint.getProperties()!!.getState().exceptionType
        val new =
                if (selectDebuggerComboBox.getSelectedIndex() == 0) HaskellExceptionBreakpointProperties.ExceptionType.EXCEPTION
                else HaskellExceptionBreakpointProperties.ExceptionType.ERROR
        breakpoint.getProperties()!!.getState().exceptionType = new
        if (old != new) {
            breakpoint.setEnabled(false)
            breakpoint.setEnabled(true)
        }
    }

    override fun loadFrom(breakpoint: XBreakpoint<HaskellExceptionBreakpointProperties>) {
        selectDebuggerComboBox.setSelectedIndex(if (breakpoint.getProperties()!!.getState().exceptionType ==
                HaskellExceptionBreakpointProperties.ExceptionType.EXCEPTION) 0 else 1
        )
    }

    private fun addLabeledControl(panel: JPanel, row: Int, label: String, component: JComponent) {
        val base = gridBagConstraints { insets = Insets(2, 0, 2, 3) }
        panel.add(JLabel(label), base.setConstraints {
            anchor = GridBagConstraints.LINE_START
            gridx = 0;
            gridy = row;
        })
        panel.add(component, base.setConstraints {
            gridx = 1;
            gridy = row;
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        })
        panel.add(Box.createHorizontalStrut(1), base.setConstraints {
            gridx = 2;
            gridy = row;
            weightx = 0.1
        })
    }

}