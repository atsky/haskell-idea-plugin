package org.jetbrains.haskell.module

import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleWithNameAlreadyExists
import com.intellij.openapi.module.StdModuleTypes
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import org.jdom.JDOMException
import org.jetbrains.annotations.Nullable
import org.jetbrains.haskell.icons.HaskellIcons
import org.jetbrains.haskell.sdk.HaskellSdkType
import javax.swing.*
import java.io.File
import java.io.FileWriter
import java.io.IOException

public class HaskellModuleBuilder() : ModuleBuilder() {


    override fun getBuilderId() = "haskell.module.builder"

    override fun modifySettingsStep(settingsStep: SettingsStep): ModuleWizardStep? =
        StdModuleTypes.JAVA!!.modifySettingsStep(settingsStep, this)

    override fun getBigIcon(): Icon = HaskellIcons.HASKELL_BIG

    override fun getGroupName(): String? = "Haskell"

    override fun getPresentableName(): String? = "Haskell Module"

    override fun createWizardSteps(wizardContext: WizardContext, modulesProvider: ModulesProvider): Array<ModuleWizardStep> =
        getModuleType().createWizardSteps(wizardContext, this, modulesProvider)

    override fun getModuleType(): HaskellModuleType {
        return HaskellModuleType.INSTANCE
    }

    override fun setupRootModel(rootModel: ModifiableRootModel?) {
        if (myJdk != null) {
            rootModel!!.setSdk(myJdk)
        } else {
            rootModel!!.inheritSdk()
        }

        val contentEntry = doAddContentEntry(rootModel)
        if (contentEntry != null) {
            val srcPath = getContentEntryPath() + File.separator + "src"
            File(srcPath).mkdirs()
            val sourceRoot = LocalFileSystem.getInstance()!!.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(srcPath))
            if (sourceRoot != null) {
                contentEntry.addSourceFolder(sourceRoot, false, "")
            }
            val name = getName()
            try {
                makeCabal(getContentEntryPath() + File.separator + name + ".cabal", name!!)
                makeMain(srcPath + File.separator + "Main.hs")
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }

    public fun makeCabal(path: String, name: String) {
        val text = "name:              " + name + "\n" + "version:           1.0\n" + "Build-Type:        Simple\n" + "cabal-version:     >= 1.2\n" + "\n" + "executable " + name + "\n" + "  main-is:         Main.hs\n" + "  hs-source-dirs:  src\n" + "  build-depends:   base\n"
        val writer = FileWriter(path)
        writer.write(text)
        writer.close()
    }

    public fun makeMain(path: String) {
        val text = "module Main where\n" + "\n"

        val writer = FileWriter(path)
        writer.write(text)
        writer.close()
    }

    override fun isSuitableSdkType(sdkType: SdkTypeId?): Boolean {
        return sdkType is HaskellSdkType
    }
}
