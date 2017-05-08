package org.jetbrains.haskell.module

import com.intellij.openapi.roots.ui.configuration.BuildElementsEditor
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState
import com.intellij.openapi.roots.ui.configuration.ModuleElementsEditor
import org.jetbrains.annotations.Nls
import javax.swing.*

class OutputEditor(state: ModuleConfigurationState) : ModuleElementsEditor(state) {
    private val myCompilerOutputEditor: BuildElementsEditor

    override fun createComponentImpl(): JComponent {
        return myCompilerOutputEditor.createComponentImpl()!!
    }

    override fun saveData(): Unit {
        myCompilerOutputEditor.saveData()
    }

    override fun getDisplayName(): String {
        return "Paths"
    }

    override fun getHelpTopic(): String {
        return myCompilerOutputEditor.helpTopic!!
    }

    init {
        myCompilerOutputEditor = object : BuildElementsEditor(state) {


        }
    }

}
