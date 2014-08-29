package org.jetbrains.cabal.export

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.openapi.externalSystem.service.project.wizard.AbstractExternalModuleBuilder
import com.intellij.openapi.module.*
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.roots.ModifiableRootModel
import org.jetbrains.haskell.module.HaskellModuleType
import org.jetbrains.cabal.settings.CabalProjectSettings
import org.jetbrains.cabal.util.*

import javax.swing.*
import java.io.File
import java.io.IOException

public class CabalModuleBuilder() : AbstractExternalModuleBuilder<CabalProjectSettings>(SYSTEM_ID, CabalProjectSettings()) {

    throws(javaClass<ConfigurationException>())
    override fun setupRootModel(modifiableRootModel: ModifiableRootModel?) {
//        val contentEntryPath = getContentEntryPath()
//        if (StringUtil.isEmpty(contentEntryPath)) {
//            return
//        }
//        val contentRootDir = File(contentEntryPath)
//        FileUtilRt.createDirectory(contentRootDir)
//        val fileSystem = LocalFileSystem.getInstance()
//        val modelContentRootDir = fileSystem.refreshAndFindFileByIoFile(contentRootDir)
//        if (modelContentRootDir == null) {
//            return
//        }
//
//        modifiableRootModel.addContentEntry(modelContentRootDir)
//        if (myJdk != null) {
//            modifiableRootModel.setSdk(myJdk)
//        } else {
//            modifiableRootModel.inheritSdk()
//        }
//
//        val project = modifiableRootModel.getProject()
//
//        setupGradleBuildFile(modelContentRootDir)
//        setupGradleSettingsFile(modelContentRootDir, modifiableRootModel)
//
//        if (myWizardContext.isCreatingNewProject()) {
//            val externalProjectPath = FileUtil.toCanonicalPath(project.getBasePath())
//            getExternalProjectSettings().setExternalProjectPath(externalProjectPath)
//            val settings = ExternalSystemApiUtil.getSettings(project, GradleConstants.SYSTEM_ID)
//            project.putUserData(ExternalSystemDataKeys.NEWLY_CREATED_PROJECT, java.lang.Boolean.TRUE)
//            //noinspection unchecked
//            settings.linkProject(getExternalProjectSettings())
//        } else {
//            FileDocumentManager.getInstance().saveAllDocuments()
//            ExternalSystemUtil.refreshProjects(project, GradleConstants.SYSTEM_ID, false)
//        }
    }

//    override fun createWizardSteps(wizardContext: WizardContext, modulesProvider: ModulesProvider): Array<ModuleWizardStep> {
//        myWizardContext = wizardContext
//        return super.createWizardSteps(wizardContext, modulesProvider)
//    }
//
//    override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): ModuleWizardStep? {
//        if (!myWizardContext.isCreatingNewProject())
//            return object : ModuleWizardStep() {
//                override fun getComponent(): JComponent {
//                    return JPanel()
//                }
//
//                override fun updateDataModel() {
//                }
//            }
//        val settingsControl = GradleProjectSettingsControl(getExternalProjectSettings())
//        return ExternalModuleSettingsStep<GradleProjectSettings>(this, settingsControl)
//    }
//
//    override fun isSuitableSdkType(sdk: SdkTypeId): Boolean {
//        return sdk is JavaSdkType
//    }
//
//    override fun getParentGroup(): String {
//        return JavaModuleType.BUILD_TOOLS_GROUP
//    }
//
//    override fun getWeight(): Int {
//        return JavaModuleBuilder.BUILD_SYSTEM_WEIGHT
//    }
//
    override fun getModuleType(): ModuleType<ModuleBuilder> {
        return HaskellModuleType.INSTANCE as ModuleType<ModuleBuilder>
    }
//
//    throws(javaClass<ConfigurationException>())
//    private fun setupGradleBuildFile(modelContentRootDir: VirtualFile): VirtualFile? {
//        val file = getExternalProjectConfigFile(modelContentRootDir.getPath(), GradleConstants.DEFAULT_SCRIPT_NAME)
//        val templateName = if (getExternalProjectSettings().getDistributionType() == DistributionType.WRAPPED)
//            TEMPLATE_GRADLE_BUILD_WITH_WRAPPER
//        else
//            DEFAULT_TEMPLATE_GRADLE_BUILD
//
//        val attributes = ContainerUtil.newHashMap()
//        if (file != null) {
//            saveFile(file, templateName, attributes)
//        }
//        return file
//    }
//
//    throws(javaClass<ConfigurationException>())
//    private fun setupGradleSettingsFile(modelContentRootDir: VirtualFile, model: ModifiableRootModel): VirtualFile? {
//        val file = null
//        if (myWizardContext.isCreatingNewProject()) {
//            val moduleDirName = VfsUtilCore.getRelativePath(modelContentRootDir, model.getProject().getBaseDir(), '/')
//            file = getExternalProjectConfigFile(model.getProject().getBasePath(), GradleConstants.SETTINGS_FILE_NAME)
//            if (file == null) return null
//
//            val attributes = ContainerUtil.newHashMap()
//            val projectName = model.getProject().getName()
//            attributes.put(TEMPLATE_ATTRIBUTE_PROJECT_NAME, projectName)
//            attributes.put(TEMPLATE_ATTRIBUTE_MODULE_DIR_NAME, moduleDirName)
//            attributes.put(TEMPLATE_ATTRIBUTE_MODULE_NAME, model.getModule().getName())
//            saveFile(file, TEMPLATE_GRADLE_SETTINGS, attributes)
//        } else {
//            val moduleMap = ContainerUtil.newHashMap()
//            for (module in ModuleManager.getInstance(model.getProject()).getModules()) {
//                for (contentEntry in model.getContentEntries()) {
//                    if (contentEntry.getFile() != null) {
//                        moduleMap.put(contentEntry.getFile()!!.getPath(), module)
//                    }
//                }
//            }
//
//            val virtualFile = modelContentRootDir
//            val module = null
//            while (virtualFile != null && module == null) {
//                module = moduleMap.get(virtualFile!!.getPath())
//                virtualFile = virtualFile!!.getParent()
//            }
//
//            if (module != null) {
//                val rootProjectPath = module!!.getOptionValue(ExternalSystemConstants.ROOT_PROJECT_PATH_KEY)
//
//                if (!StringUtil.isEmpty(rootProjectPath)) {
//                    val rootProjectFile = VfsUtil.findFileByIoFile(File(rootProjectPath), true)
//                    if (rootProjectFile == null) return null
//
//                    val moduleDirName = VfsUtilCore.getRelativePath(modelContentRootDir, rootProjectFile, '/')
//                    file = getExternalProjectConfigFile(rootProjectPath, GradleConstants.SETTINGS_FILE_NAME)
//                    if (file == null) return null
//
//                    val attributes = ContainerUtil.newHashMap()
//                    attributes.put(TEMPLATE_ATTRIBUTE_MODULE_DIR_NAME, moduleDirName)
//                    attributes.put(TEMPLATE_ATTRIBUTE_MODULE_NAME, model.getModule().getName())
//                    appendToFile(file, TEMPLATE_GRADLE_SETTINGS_MERGE, attributes)
//                }
//            }
//        }
//        return file
//    }
//
//    class object {
//
//        private val LOG = Logger.getInstance(javaClass<GradleModuleBuilder>())
//
//        private val TEMPLATE_GRADLE_SETTINGS = "Gradle Settings.gradle"
//        private val TEMPLATE_GRADLE_SETTINGS_MERGE = "Gradle Settings merge.gradle"
//        private val TEMPLATE_GRADLE_BUILD_WITH_WRAPPER = "Gradle Build Script with wrapper.gradle"
//        private val DEFAULT_TEMPLATE_GRADLE_BUILD = "Gradle Build Script.gradle"
//
//        private val TEMPLATE_ATTRIBUTE_PROJECT_NAME = "PROJECT_NAME"
//        private val TEMPLATE_ATTRIBUTE_MODULE_DIR_NAME = "MODULE_DIR_NAME"
//        private val TEMPLATE_ATTRIBUTE_MODULE_NAME = "MODULE_NAME"
//
//        throws(javaClass<ConfigurationException>())
//        private fun saveFile(file: VirtualFile, templateName: String, templateAttributes: Map<Any, Any>?) {
//            val manager = FileTemplateManager.getInstance()
//            val template = manager.getInternalTemplate(templateName)
//            try {
//                VfsUtil.saveText(file, if (templateAttributes != null) template.getText(templateAttributes) else template.getText())
//            } catch (e: IOException) {
//                LOG.warn(String.format("Unexpected exception on applying template %s config", GradleConstants.SYSTEM_ID.getReadableName()), e)
//                throw ConfigurationException(e.getMessage(), String.format("Can't apply %s template config text", GradleConstants.SYSTEM_ID.getReadableName()))
//            }
//
//        }
//
//        throws(javaClass<ConfigurationException>())
//        private fun appendToFile(file: VirtualFile, templateName: String, templateAttributes: Map<Any, Any>?) {
//            val manager = FileTemplateManager.getInstance()
//            val template = manager.getInternalTemplate(templateName)
//            try {
//                VfsUtil.saveText(file, VfsUtilCore.loadText(file) + (if (templateAttributes != null) template.getText(templateAttributes) else template.getText()))
//            } catch (e: IOException) {
//                LOG.warn(String.format("Unexpected exception on appending template %s config", GradleConstants.SYSTEM_ID.getReadableName()), e)
//                throw ConfigurationException(e.getMessage(), String.format("Can't append %s template config text", GradleConstants.SYSTEM_ID.getReadableName()))
//            }
//
//        }
//
//        private fun getExternalProjectConfigFile(parent: String, fileName: String): VirtualFile? {
//            val file = File(parent, fileName)
//            FileUtilRt.createIfNotExists(file)
//            return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
//        }
//    }
}
