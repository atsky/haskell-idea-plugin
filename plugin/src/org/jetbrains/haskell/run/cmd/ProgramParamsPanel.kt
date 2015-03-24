package org.jetbrains.haskell.run.cmd

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.module.Module
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import javax.swing.*
import java.awt.GridBagLayout
import java.awt.GridBagConstraints
import org.jetbrains.haskell.util.*
import com.intellij.ui.RawCommandLineEditor
import com.intellij.execution.configuration.EnvironmentVariablesComponent
import java.awt.Insets
import org.jetbrains.haskell.run.ModuleComboBoxRenderer

class ProgramParamsPanel(modules: Array<Module>) : JPanel() {
    private var mainFileComponent: TextFieldWithBrowseButton
    private var moduleComboBox: JComboBox<Module>
    private var programParametersComponent : RawCommandLineEditor
    private var workingDirectoryComponent : TextFieldWithBrowseButton
    private var environmentVariables : EnvironmentVariablesComponent


    public fun applyTo(s: CmdLineRunConfiguration): Unit {
        s.setMainFile(moduleComboBox.getSelectedItem() as Module?, mainFileComponent.getText())
        s.setProgramParameters(programParametersComponent.getText())
        s.setWorkingDirectory(workingDirectoryComponent.getText())
        s.setEnvs(environmentVariables.getEnvs())
    }
    public fun reset(s: CmdLineRunConfiguration): Unit {
        mainFileComponent.setText(s.getExecFile())
        programParametersComponent.setText(s.getProgramParameters())
        workingDirectoryComponent.setText(s.getWorkingDirectory())
        moduleComboBox.setSelectedItem(s.getModule())
        environmentVariables.setEnvs(s.getEnvs())
    }

    init {
        this.setLayout(GridBagLayout())
        mainFileComponent = TextFieldWithBrowseButton();
        mainFileComponent.addBrowseFolderListener("Main file", "Main File", null, FileChooserDescriptor(true, false, false, false, true, false))
        moduleComboBox = JComboBox(DefaultComboBoxModel(modules))
        moduleComboBox.setRenderer(ModuleComboBoxRenderer())

        programParametersComponent = RawCommandLineEditor()
        workingDirectoryComponent = TextFieldWithBrowseButton()
        environmentVariables = EnvironmentVariablesComponent()
        environmentVariables.getLabelLocation()


        val base : () -> GridBagConstraints = {
            val result = GridBagConstraints()
            result.insets = Insets(3, 3, 3, 3)
            result.anchor = GridBagConstraints.LINE_START
            result
        }

        add(JLabel("Main module"), base().setConstraints {
            gridx = 0
            gridy = 0
            weightx = 0.1
        })

        add(mainFileComponent, base().setConstraints {
            gridx = 1
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        })

        add(JLabel("Program arguments"), base().setConstraints {
            weightx = 0.1
            gridy = 1
        })

        add(programParametersComponent, base().setConstraints {
            gridx = 1
            gridy = 1
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        })

        add(JLabel("Working directory"), base().setConstraints {
            gridx = 0
            gridy = 2
            weightx = 0.1
        })

        add(workingDirectoryComponent, base().setConstraints {
            gridx = 1
            gridy = 2
            weightx = 0.1
            fill = GridBagConstraints.HORIZONTAL
        })

        add(JLabel("Environment variables"), base().setConstraints {
            gridx = 0
            gridy = 3
            weightx = 0.1
            fill = GridBagConstraints.HORIZONTAL
        })

        add(environmentVariables, base().setConstraints {
            gridx = 1
            gridy = 3
            weightx = 0.1
        })

        add(JLabel("Module"), base().setConstraints {
            gridy = 4
            weightx = 0.1
        })

        add(moduleComboBox, base().setConstraints {
            gridx = 1
            gridy = 4
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        })

    }

}
