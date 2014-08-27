package org.jetbrains.cabal.export

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.externalSystem.service.settings.AbstractImportFromExternalSystemControl
import com.intellij.openapi.options.ConfigurationException
import com.intellij.projectImport.ProjectImportWizardStep

import javax.swing.*
import java.awt.*

public class SimpleCabalStep(context: WizardContext) : ProjectImportWizardStep(context) {

    override fun getBuilder(): CabalProjectImportBuilder {
        return getWizardContext()!!.getProjectBuilder() as CabalProjectImportBuilder
    }

    private val myComponent = JPanel(BorderLayout())

    override fun getComponent(): JComponent {
        return myComponent
    }

    override fun updateStep() {
    }

    override fun updateDataModel() {
    }

    throws(javaClass<ConfigurationException>())
    override fun validate(): Boolean {
        return getBuilder() != null
    }
}