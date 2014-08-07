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
import com.intellij.debugger.ui.impl.UpdatableDebuggerView
import javax.swing.JList
import java.awt.Dimension
import javax.swing.DefaultListModel
import javax.swing.JFrame
import javax.swing.JSplitPane
import javax.swing.JScrollPane
import java.awt.BorderLayout
import com.intellij.debugger.impl.DebuggerSession
import com.intellij.debugger.settings.DebuggerSettings
import com.intellij.util.Alarm
import com.intellij.openapi.application.ModalityState
import java.util.Vector
import javax.swing.JComponent
import java.awt.Component
import javax.swing.ListSelectionModel
import groovy.swing.factory.ScrollPaneFactory
import com.intellij.ui.components.JBScrollPane
import javax.swing.event.ListSelectionEvent
import javax.swing.DefaultListSelectionModel

/**
 * Created by vlad on 8/4/14.
 */

public abstract class HistoryPanel(private val process: HaskellDebugProcess) : JSplitPane(JSplitPane.HORIZONTAL_SPLIT) {

    private val debugSession = process.getSession()!!
    private val debuggerStateManager: DebuggerStateManager = MyDebuggerStateManager()

    private val framesPanel = FramesPanel()
    private val selectionModel = DefaultListSelectionModel()
    private val variablesPanel: VariablesPanel = VariablesPanel(debugSession.getProject(), debuggerStateManager, null);

    {
        setLeftComponent(JBScrollPane(framesPanel))
        setRightComponent(variablesPanel)
    }

    public fun stackChanged(stackFrame: HsStackFrame?) {
        if (stackFrame == null) {
            framesPanel.clear()
        }
        variablesPanel.stackChanged(stackFrame)
    }

    public fun addInfo(line: String) {
        framesPanel.addElement(line)
    }

    public fun shiftBack() {
        val index = framesPanel.getSelectedIndex()
        if (index != -1 && index + 1 < framesPanel.getIndexCount()) {
            framesPanel.setSelectedIndex(index + 1)
        }
    }

    public fun shiftForward() {
        val index = framesPanel.getSelectedIndex()
        if (index > 0) {
            framesPanel.setSelectedIndex(index - 1)
        }
    }

    abstract fun indexSelected(index: Int)

    private inner class FramesPanel : JList() {
        private val listModel = DefaultListModel();

        {
            setModel(listModel)
            setPreferredSize(Dimension(150, -1))
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
            setValueIsAdjusting(true)
            addListSelectionListener {(event: ListSelectionEvent) ->
                indexSelected(getSelectedIndex())
            }
        }

        public fun addElement(line: String) {
            listModel.addElement(line)
            if (listModel.size() == 1) {
                setSelectedIndex(0)
            }
        }

        public fun clear() {
            listModel.clear()
        }

        public fun getIndexCount(): Int {
            return listModel.size()
        }
    }

    private inner class MyDebuggerStateManager() : DebuggerStateManager() {
        override fun setState(context: DebuggerContextImpl?, state: Int, event: Int, description: String?) {
            fireStateChanged(context, event)
        }
        override fun getContext(): DebuggerContextImpl {
            return DebuggerContextImpl.EMPTY_CONTEXT
        }
    }
}