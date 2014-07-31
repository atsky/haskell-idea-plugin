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
    private val traceSwitchOffCheckBox: JCheckBox = JCheckBox(TRACE_CHECKBOX_LABEL, false)

    private var isModified = false

    override fun getDisplayName(): String? = "Haskell debugger"

    override fun getHelpTopic(): String? = null

    /**
     * Creates UI for settings page
     */
    override fun createComponent(): JComponent? {
        val listener = object : ItemListener {
            override fun itemStateChanged(e: ItemEvent) {
                println("DEBUG: item selected")
                isModified = true
            }
        }
        selectDebuggerComboBox.addItemListener(listener)
        traceSwitchOffCheckBox.addItemListener(listener)

        val result = JPanel(GridBagLayout())
        addLabeledControl(result, 0, "Prefered debugger:     ", selectDebuggerComboBox)
        addLabeledControl(result, 1, "Additional options:     ", traceSwitchOffCheckBox)
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
        val traceSwitchedOff = traceSwitchOffCheckBox.isSelected()
        isModified = false
    }

    /**
     * Actions performed when user press "Reset" button. Here we need to reset appropriate properties in global
     * debug settings object
     */
    override fun reset() {
        isModified = false
    }

    override fun disposeUIResources() {}

    private fun addLabeledControl(panel: JPanel, row : Int, label : String, component : JComponent) {
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