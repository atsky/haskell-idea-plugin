package org.jetbrains.haskell.debugger.breakpoints

import com.intellij.xdebugger.breakpoints.ui.XBreakpointCustomPropertiesPanel
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import javax.swing.JComponent
import com.intellij.openapi.ui.ComboBox
import javax.swing.DefaultComboBoxModel
import javax.swing.JPanel
import javax.swing.JLabel
import javax.   swing.SpringLayout.Constraints
import java.awt.GridLayout
import com.intellij.openapi.util.Key
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import org.jetbrains.haskell.debugger.parser.HsFilePosition
import java.util.ArrayList
//import org.jetbrains.haskell.debugger.protocol.BreakListForLineCommand
import org.jetbrains.haskell.debugger.utils.SyncObject
import com.intellij.xdebugger.XDebuggerManager

/**
 * Panel with additional breakpoint settings (make right click on breakpoint to see it)
 *
 * @author Habibullin Marat
 */
public class HsBreakpointPropertiesPanel: XBreakpointCustomPropertiesPanel<XLineBreakpoint<XBreakpointProperties<out Any?>>>() {
    private val PANEL_LABEL: String = "Select breakpoint:"
    private val DEBUG_NOT_STARTED_ITEM: String = "select remote debugger and start debug process to enable"
    private val breaksComboBox: ComboBox = ComboBox(DefaultComboBoxModel(array(DEBUG_NOT_STARTED_ITEM)))
    private val mainPanel: JPanel = JPanel(GridLayout(1, 0));
    {
        mainPanel.add(JLabel(PANEL_LABEL))
        mainPanel.add(breaksComboBox)
        breaksComboBox.setEnabled(false)
    }

    private var debugManager: XDebuggerManager? = null
    private var debugProcess: HaskellDebugProcess? = null
    private var breaksList: ArrayList<HsFilePosition>? = ArrayList()
    private var lastSelectedIndex: Int? = null

    override fun getComponent(): JComponent = mainPanel

    override fun saveTo(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>) {
        if(debuggingInProgress()) {
            val selectedIndex = breaksComboBox.getSelectedIndex()
            if (selectedIndex != lastSelectedIndex) {
                breakpoint.putUserData(HaskellLineBreakpointHandler.INDEX_IN_BREAKS_LIST_KEY, selectedIndex)
                // temporary
                println("remove and set new break called")
            }
        }
    }

    override fun loadFrom(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>) {
        getUserData(breakpoint)
        fillComboBox()
    }

    private fun getUserData(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>) {
        val project = breakpoint.getUserData(HaskellLineBreakpointHandler.PROJECT_KEY)
        if(project != null) {
            debugManager = XDebuggerManager.getInstance(project)
            val justDebugProcess = debugManager?.getCurrentSession()?.getDebugProcess()
            if(justDebugProcess != null) {
                debugProcess = justDebugProcess as HaskellDebugProcess
            } else {
                debugProcess = null
            }
        }
        breaksList = breakpoint.getUserData(HaskellLineBreakpointHandler.BREAKS_LIST_KEY)
        lastSelectedIndex = breakpoint.getUserData(HaskellLineBreakpointHandler.INDEX_IN_BREAKS_LIST_KEY)
    }

    private fun fillComboBox() {
        breaksComboBox.removeAllItems()
        if(debuggingInProgress() && (breaksList as ArrayList<HsFilePosition>).notEmpty) {
            for (breakEntry in breaksList as ArrayList<HsFilePosition>) {
                breaksComboBox.addItem(breakEntry.toString())
            }
            breaksComboBox.setSelectedIndex(lastSelectedIndex as Int)
            breaksComboBox.setEnabled(true)
        } else {
            breaksComboBox.addItem(DEBUG_NOT_STARTED_ITEM)
            breaksComboBox.setEnabled(false)
        }
    }

    private fun debuggingInProgress(): Boolean {
        return debugManager?.getCurrentSession() != null
    }
}