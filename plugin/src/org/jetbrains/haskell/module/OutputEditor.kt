package org.jetbrains.haskell.module

import com.intellij.openapi.roots.ui.configuration.BuildElementsEditor
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState
import com.intellij.openapi.roots.ui.configuration.ModuleElementsEditor
import org.jetbrains.annotations.Nls
import javax.swing.*

class OutputEditor(state: ModuleConfigurationState) : ModuleElementsEditor(state) {
    private val myCompilerOutputEditor: BuildElementsEditor

    protected override fun createComponentImpl(): JComponent {
        return myCompilerOutputEditor.createComponentImpl()!!
    }

    public override fun saveData(): Unit {
        myCompilerOutputEditor.saveData()
    }

    public override fun getDisplayName(): String {
        return "Paths"
    }

    public override fun getHelpTopic(): String {
        return myCompilerOutputEditor.getHelpTopic()!!
    }

    {
        myCompilerOutputEditor = object : BuildElementsEditor(state) {


        }
    }

}
