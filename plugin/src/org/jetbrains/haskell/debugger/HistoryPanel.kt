package org.jetbrains.haskell.debugger

import javax.swing.JPanel
import com.intellij.debugger.ui.impl.VariablesPanel
import javax.swing.JLabel
import javax.swing.JTextField
import com.intellij.debugger.impl.DebuggerStateManager
import com.intellij.debugger.impl.DebuggerContextImpl
import javax.swing.SpringLayout
import com.intellij.ui.AppUIUtil
import com.intellij.debugger.DebuggerManagerEx
import org.jetbrains.haskell.debugger.frames.HsStackFrame
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.debugger.ui.FramesPanel

/**
 * Created by vlad on 8/4/14.
 */

public class HistoryPanel(private val process: HaskellDebugProcess) : JPanel() {

    private val debugSession = process.getSession()!!
    private val debuggerStateManager: DebuggerStateManager = MyDebuggerStateManager()

    private val currentSpanLabel: JLabel = JLabel("Current source span")
    private val currentSpanTextField: JTextField = JTextField()
    private val framesPanel: FramesPanel = FramesPanel(debugSession.getProject(), debuggerStateManager)
    private val variablesPanel: VariablesPanel = VariablesPanel(debugSession.getProject(), debuggerStateManager, null);

    {
        val layout = SpringLayout()
        this.setLayout(layout)

        currentSpanTextField.setEditable(false)

        this.add(currentSpanLabel)
        this.add(currentSpanTextField)
        this.add(framesPanel)
        this.add(variablesPanel)

        layout.putConstraint(SpringLayout.WEST, currentSpanLabel,
                5,
                SpringLayout.WEST, this)
        layout.putConstraint(SpringLayout.SOUTH, currentSpanLabel,
                0,
                SpringLayout.SOUTH, currentSpanTextField)

        layout.putConstraint(SpringLayout.NORTH, currentSpanTextField,
                5,
                SpringLayout.NORTH, this)
        layout.putConstraint(SpringLayout.WEST, currentSpanTextField,
                5,
                SpringLayout.EAST, currentSpanLabel)
        layout.putConstraint(SpringLayout.EAST, currentSpanTextField,
                -5,
                SpringLayout.EAST, this)

        layout.putConstraint(SpringLayout.NORTH, framesPanel,
                5,
                SpringLayout.SOUTH, currentSpanTextField)
        layout.putConstraint(SpringLayout.SOUTH, framesPanel,
                -5,
                SpringLayout.SOUTH, this)
        layout.putConstraint(SpringLayout.WEST, framesPanel,
                5,
                SpringLayout.WEST, this)

        layout.putConstraint(SpringLayout.NORTH, variablesPanel,
                5,
                SpringLayout.SOUTH, currentSpanTextField)
        layout.putConstraint(SpringLayout.SOUTH, variablesPanel,
                -5,
                SpringLayout.SOUTH, this)
        layout.putConstraint(SpringLayout.WEST, variablesPanel,
                5,
                SpringLayout.EAST, framesPanel)
        layout.putConstraint(SpringLayout.EAST, variablesPanel,
                -5,
                SpringLayout.EAST, this)
    }

    public fun stackChanged(stackFrame: HsStackFrame?) {
        currentSpanTextField.setText(if (stackFrame != null) stackFrame.stackFrameInfo.filePosition.toString() else "")
        variablesPanel.stackChanged(stackFrame)

    }

    private inner class MyDebuggerStateManager() : DebuggerStateManager() {
        override fun setState(context: DebuggerContextImpl?, state: Int, event: Int, description: String?) {
        }
        override fun getContext(): DebuggerContextImpl {
            return DebuggerContextImpl.EMPTY_CONTEXT
        }
    }
}