//package org.jetbrains.cabal.export
//
//import com.intellij.ide.util.projectWizard.WizardContext
//import com.intellij.openapi.options.ConfigurationException
//import com.intellij.projectImport.ProjectImportWizardStep
//import org.jetbrains.cabal.export.CabalProjectSettingsControl
//import org.jetbrains.cabal.settings.CabalProjectSettings
//
//import javax.swing.*
//import java.awt.*
//
//public class SimpleCabalStep(context: WizardContext) : ProjectImportWizardStep(context) {
//
//    private val myComponent = JPanel(BorderLayout())
//
//    private var mySettingsInitialised = false
//    private var myControl: CabalProjectSettingsControl? = null
//
//    override fun getBuilder(): CabalProjectImportBuilder {
//        return getWizardContext()!!.getProjectBuilder() as CabalProjectImportBuilder
//    }
//
//    override fun getComponent(): JComponent {
//        return myComponent
//    }
//
//    override fun updateStep() {
//        if (!mySettingsInitialised) {
//            initSimpleCabalControl()
//        }
//    }
//
//    override fun updateDataModel() {
//    }
//
//    throws(javaClass<ConfigurationException>())
//    override fun validate(): Boolean {
//        return getBuilder() != null
//    }
//
//
//    private fun initSimpleCabalControl() {
//        val builder = getBuilder()
//        if (builder == null) {
//            return
//        }
//    }
//
//}