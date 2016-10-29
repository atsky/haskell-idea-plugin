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
import org.jetbrains.haskell.debugger.utils.HaskellUtils
import org.jetbrains.haskell.debugger.parser.BreakInfo
import org.jetbrains.haskell.debugger.utils.UIUtils

/**
 * Panel with additional breakpoint settings (make right click on breakpoint to see it)
 *
 * @author Habibullin Marat
 */
public class SelectBreakPropertiesPanel : XBreakpointCustomPropertiesPanel<XLineBreakpoint<XBreakpointProperties<out Any?>>>() {
    private val PANEL_LABEL: String = "Select breakpoint:"
    private val DEBUG_NOT_STARTED_ITEM: String = "start debug process to enable"
    private val breaksComboBox: ComboBox<String> = ComboBox(DefaultComboBoxModel(arrayOf(DEBUG_NOT_STARTED_ITEM)))
    private val mainPanel: JPanel = JPanel(GridLayout(1, 0))

    init {
        UIUtils.addLabeledControl(mainPanel, 0, PANEL_LABEL, breaksComboBox)
        breaksComboBox.setEnabled(false)
    }

    private var debugManager: XDebuggerManager? = null
    private var debugProcess: HaskellDebugProcess? = null
    private var breaksList: ArrayList<BreakInfo>? = ArrayList()
    private var lastSelectedIndex: Int? = null

    override fun getComponent(): JComponent = mainPanel

    /**
     * Called when one press 'Done' button in breakpoint's context menu. Saves user selection and resets breakpoint if needed
     */
    override fun saveTo(breakpoint: XLineBreakpoint<XBreakpointProperties<out Any?>>) {
        if(debuggingInProgress()) {
            val selectedIndex = breaksComboBox.getSelectedIndex()
            if (selectedIndex != lastSelectedIndex && debugProcess != null) {
                breakpoint.putUserData(HaskellLineBreakpointHandler.INDEX_IN_BREAKS_LIST_KEY, selectedIndex)
                val moduleName = HaskellUtils.getModuleName(debugManager!!.getCurrentSession()!!.getProject(), breakpoint.getSourcePosition()!!.getFile())
                debugProcess?.removeBreakpoint(moduleName, HaskellUtils.zeroBasedToHaskellLineNumber(breakpoint.getLine()))
                debugProcess?.addBreakpointByIndex(moduleName, breaksList!!.get(selectedIndex).breakIndex, breakpoint)
            }
        }
    }

    /**
     * Called on every right click on breakpoint. Fills combo box with available breaks info
     */
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
        if(debuggingInProgress() && (breaksList as ArrayList<BreakInfo>).isNotEmpty()) {
            for (breakEntry in breaksList as ArrayList<BreakInfo>) {
                breaksComboBox.addItem(breakEntry.srcSpan.spanToString())
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