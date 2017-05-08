package org.jetbrains.haskell

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleType
import org.jetbrains.haskell.module.HaskellModuleType
import org.jetbrains.cabal.CabalInterface
import com.intellij.util.ui.UIUtil
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.options.ShowSettingsUtil
import org.jetbrains.haskell.config.HaskellConfigurable
import com.intellij.openapi.module.Module
import java.io.File
import org.jetbrains.haskell.util.deleteRecursive
import org.jetbrains.haskell.util.OSUtil
import org.jetbrains.haskell.external.GhcMod
import com.intellij.openapi.roots.ProjectRootManager
import org.jetbrains.haskell.sdk.HaskellSdkType


class HaskellProjectComponent(val project: Project) : ProjectComponent {
    companion object {
        val GHC_PATH_NOT_FOUND = "ghc not found in PATH. It can cause issues."+
                                 " Please spicify haskell SDK for project."
    }

    fun invokeInUI(block: () -> Unit) {
        UIUtil.invokeAndWaitIfNeeded(object : Runnable {
            override fun run() {
                block()
            }
        })
    }

    fun getHaskellModules(): List<Module> {
        val moduleManager = ModuleManager.getInstance(project)!!
        return moduleManager.modules.filter { ModuleType.get(it) == HaskellModuleType.INSTANCE }
    }

    override fun projectOpened() {
        if (!getHaskellModules().isEmpty()) {
            val paths = System.getenv("PATH")!!.split(File.pathSeparator.toRegex()).toTypedArray().toMutableList()

            val sdk = ProjectRootManager.getInstance(project).projectSdk
            if (sdk != null && sdk.sdkType is HaskellSdkType) {
                paths.add(sdk.homePath + File.separator + "bin")
            }

            if (OSUtil.isMac) {
                paths.add("/usr/local/bin")
            }
        }
    }


    override fun projectClosed() {
    }

    override fun getComponentName(): String {
        return "HaskellProjectComponent"
    }

    override fun initComponent() {
    }

    override fun disposeComponent() {
    }

}
