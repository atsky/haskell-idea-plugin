package org.jetbrains.haskell.debugger.config

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent
import com.intellij.openapi.ui.ComboBox
import javax.swing.DefaultComboBoxModel
import com.intellij.ui.DocumentAdapter
import javax.swing.event.DocumentEvent
import java.awt.event.ItemListener
import java.awt.event.ItemEvent
import javax.swing.JPanel
import java.awt.GridBagLayout
import org.jetbrains.haskell.util.gridBagConstraints
import java.awt.Insets
import javax.swing.JLabel
import org.jetbrains.haskell.util.setConstraints
import java.awt.GridBagConstraints
import javax.swing.Box
import javax.swing.JCheckBox
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import org.jetbrains.haskell.debugger.utils.UIUtils

/**
 * Manages debugger settings. Creates additional section in IDEA Settings and tracks changes appeared there to obtain
 * debugger settings. The settings are as follows:
 * 1) user can select what debugger he would like to use
 * 2) user can switch ':trace' command off
 *
 * @author Habibullin Marat
 */
public class DebuggerConfigurable() : Configurable {
    class object {
        private val ITEM_GHCI = "GHCi"
        private val ITEM_REMOTE = "Remote"

        private val TRACE_CHECKBOX_LABEL = "Switch off ':trace' command"
    }
    private val selectDebuggerComboBox: ComboBox = ComboBox(DefaultComboBoxModel(array(ITEM_GHCI, ITEM_REMOTE)))
    private val remoteDebuggerPathField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()
    private val traceSwitchOffCheckBox: JCheckBox = JCheckBox(TRACE_CHECKBOX_LABEL, false)

    private var isModified = false

    override fun getDisplayName(): String? = "Haskell debugger"

    override fun getHelpTopic(): String? = null

    /**
     * Creates UI for settings page
     */
    override fun createComponent(): JComponent? {
        remoteDebuggerPathField.addBrowseFolderListener(
                "Select remote debugger executable",
                null,
                null,
                FileChooserDescriptorFactory.createSingleLocalFileDescriptor())
        val itemListener = object : ItemListener {
            override fun itemStateChanged(e: ItemEvent) {
                isModified = true
            }
        }
        val docListener : DocumentAdapter = object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent?) {
                isModified = true;
            }
        };
        selectDebuggerComboBox.addItemListener(itemListener)
        remoteDebuggerPathField.getTextField()!!.getDocument()!!.addDocumentListener(docListener)
        traceSwitchOffCheckBox.addItemListener(itemListener)

        val result = JPanel(GridBagLayout())
        UIUtils.addLabeledControl(result, 0, "Prefered debugger:     ", selectDebuggerComboBox)
        UIUtils.addLabeledControl(result, 1, "Remote debugger path:     ", remoteDebuggerPathField)
        UIUtils.addLabeledControl(result, 2, "Additional options:     ", traceSwitchOffCheckBox)
        result.add(JPanel(), gridBagConstraints { gridx = 0; gridy = 5; weighty = 10.0 })
        return result
    }

    override fun isModified(): Boolean = isModified

    /**
     * Actions performed when user press "Apply" button. Here we obtain settings and need to set them in some global
     * debug settings object
     */
    override fun apply() {
        val ghciSelected = selectDebuggerComboBox.getSelectedIndex() == 0
        val remotePath = remoteDebuggerPathField.getTextField()!!.getText()
        val traceSwitchedOff = traceSwitchOffCheckBox.isSelected()

        val state = HaskellDebugSettings.getInstance().getState()
        state.debuggerType = if (ghciSelected) HaskellDebugSettings.DebuggerType.GHCI else HaskellDebugSettings.DebuggerType.REMOTE
        state.remoteDebuggerPath = remotePath
        state.traceOff = traceSwitchedOff

        isModified = false
    }

    /**
     * Actions performed when user press "Reset" button. Here we need to reset appropriate properties in global
     * debug settings object
     */
    override fun reset() {
        val state = HaskellDebugSettings.getInstance().getState()
        selectDebuggerComboBox.setSelectedIndex(if (state.debuggerType == HaskellDebugSettings.DebuggerType.GHCI) 0 else 1)
        traceSwitchOffCheckBox.setSelected(state.traceOff)
        remoteDebuggerPathField.getTextField()!!.setText(state.remoteDebuggerPath)

        isModified = false
    }

    override fun disposeUIResources() {}
}