package org.jetbrains.haskell.debugger.history

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import com.intellij.openapi.Disposable
import com.intellij.debugger.impl.DebuggerStateManager
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.debugger.ui.impl.VariablesPanel
import javax.swing.JComponent
import org.jetbrains.haskell.debugger.frames.HsStackFrame
import com.intellij.ui.components.JBList
import javax.swing.event.ListSelectionEvent
import com.intellij.debugger.impl.DebuggerContextImpl
import javax.swing.DefaultListModel
import com.intellij.execution.ui.layout.PlaceInGrid
import com.intellij.icons.AllIcons
import javax.swing.ListSelectionModel
import com.intellij.ui.components.JBScrollPane

public class HistoryTab(private val process: HaskellDebugProcess,
                        private val manager: HistoryManager) : Disposable {

    private val debugSession = process.getSession()!!
    private val debuggerStateManager: DebuggerStateManager = MyDebuggerStateManager()
    private val myUi: RunnerLayoutUi = RunnerLayoutUi.Factory.getInstance(process.getSession()!!.getProject())!!
            .create("History", "Debugger History", process.getSession()!!.getSessionName(), this)

    private val framesPanel = FramesPanel()
    private val variablesPanel: VariablesPanel = VariablesPanel(debugSession.getProject(), debuggerStateManager, null);

    {
        val framesContext = myUi.createContent("HistoryFramesContent", JBScrollPane(framesPanel), "History frames", AllIcons.Debugger.Frame, null)
        val variablesContext = myUi.createContent("HistoryVariablesContent", variablesPanel, "Current variables", AllIcons.Debugger.Value, null)
        framesContext.setCloseable(false)
        variablesContext.setCloseable(false)
        myUi.addContent(framesContext, 0, PlaceInGrid.left, false)
        myUi.addContent(variablesContext, 0, PlaceInGrid.right, false)
    }

    public fun getComponent(): JComponent {
        return myUi.getComponent()
    }

    override fun dispose() {
    }

    public fun stackChanged(stackFrame: HsStackFrame?) {
        if (stackFrame == null) {
            framesPanel.clear()
        }
        variablesPanel.stackChanged(stackFrame)
    }

    public fun addHistoryLine(line: String) {
        framesPanel.addElement(line)
    }

    public fun shiftBack() {
        val index = framesPanel.getSelectedIndex()
        if (index != -1 && index + 1 < framesPanel.getIndexCount()) {
            framesPanel.setSelectedIndex(index + 1)
        } else {
            manager.indexSelected(index)
        }
    }

    public fun shiftForward() {
        val index = framesPanel.getSelectedIndex()
        if (index > 0) {
            framesPanel.setSelectedIndex(index - 1)
        } else {
            manager.indexSelected(index)
        }
    }

    private inner class FramesPanel : JBList() {
        private val listModel = DefaultListModel();

        {
            setModel(listModel)
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
            setValueIsAdjusting(true)
            addListSelectionListener {(event: ListSelectionEvent) ->
                manager.indexSelected(getSelectedIndex())
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

        public fun isFrameUnknown(): Boolean {
            if (getSelectedIndex() < 0) {
                return true
            }
            return listModel.get(getSelectedIndex()).equals("...")
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