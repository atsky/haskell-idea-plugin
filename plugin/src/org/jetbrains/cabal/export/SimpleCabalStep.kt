package org.jetbrains.cabal.export

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.externalSystem.service.project.wizard.AbstractImportFromExternalSystemWizardStep
import com.intellij.openapi.options.ConfigurationException
import org.jetbrains.cabal.export.ImportFromCabalControl
import org.jetbrains.cabal.settings.CabalProjectSettings

import javax.swing.*
import java.awt.*
import com.intellij.openapi.externalSystem.service.settings.AbstractImportFromExternalSystemControl
import com.intellij.openapi.externalSystem.service.project.wizard.AbstractExternalProjectImportBuilder

public class SimpleCabalStep(context: WizardContext) : AbstractImportFromExternalSystemWizardStep(context) {

    private val myComponent = JPanel(BorderLayout())

    private var mySettingsInitialised = false
    private var myControl: ImportFromCabalControl? = null

    override fun getBuilder(): CabalProjectImportBuilder? {
        return getWizardContext().getProjectBuilder() as CabalProjectImportBuilder
    }

    override fun getComponent(): JComponent {
        return myComponent
    }

    override fun getWizardContext(): WizardContext {
        return super.getWizardContext()!!
    }

    override fun updateStep() {
        if (!mySettingsInitialised) {
            initSimpleCabalControl()
        }
    }

    override fun updateDataModel() {
    }

    @Throws(ConfigurationException::class)
    override fun validate(): Boolean {
        myControl?.apply()
        if (myControl?.getProjectFormatPanel() != null) {
            myControl!!.getProjectFormatPanel()!!.updateData(getWizardContext())
        }
        val builder = getBuilder()
        if (builder == null) {
            return false
        }
        builder.ensureProjectIsDefined(getWizardContext())
        return true
    }


    private fun initSimpleCabalControl() {
        val builder = getBuilder()
        if (builder == null) {
            return
        }
        builder.prepare(getWizardContext())
        myControl = builder.getControl(getWizardContext().getProject())
        myComponent.add(myControl!!.getComponent())
        mySettingsInitialised = true
    }
}