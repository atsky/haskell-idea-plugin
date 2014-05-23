package org.jetbrains.haskell

import com.intellij.openapi.compiler.CompilerManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import org.jetbrains.haskell.fileType.HaskellFileType
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleType
import org.jetbrains.haskell.module.HaskellModuleType
import org.jetbrains.cabal.CabalInterface
import com.intellij.util.ui.UIUtil
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.options.ShowSettingsUtil
import org.jetbrains.haskell.config.HaskellConfigurable
import org.jetbrains.haskell.external.GHC_MOD
import org.jetbrains.haskell.external.BuildWrapper


public class HaskellProjectComponent(val project: Project, manager: CompilerManager) : ProjectComponent {


    fun invokeInUI(block: () -> Unit) {
        UIUtil.invokeAndWaitIfNeeded(object : Runnable {
            override fun run() {
                block()
            }
        });
    }

    fun packageNotFound(pkg : String) {
        invokeInUI {
            val result = Messages.showDialog(
                    project,
                    pkg + " executable not found. ",
                    pkg + " not found. You can install it by cabal or set path in settings.",
                    array("Install", "Open settings", "Close"),
                    0,
                    null)
            if (result == 0) {
                CabalInterface(project).install("ghc-mod")
            } else if (result == 1) {
                ShowSettingsUtil.getInstance()!!.editConfigurable(project, HaskellConfigurable());
            }
        }
    }

    override fun projectOpened() {
        val moduleManager = ModuleManager.getInstance(project)!!
        val hasHaskellModules = moduleManager.getModules().any { ModuleType.get(it) == HaskellModuleType.INSTANCE }
        if (hasHaskellModules) {
            val cabalFound = CabalInterface(project).checkVersion()
            if (!cabalFound) {
                invokeInUI {
                    val result = Messages.showDialog(
                            project,
                            "Cabal executable not found. Please add it to PATH or specify path in settings",
                            "Cabal not found",
                            array("Open settings", "Close"),
                            0,
                            null)
                    if (result == 0) {
                        ShowSettingsUtil.getInstance()!!.editConfigurable(project, HaskellConfigurable());
                    }
                }
            } else {
                if (!GHC_MOD.сheck()) {
                    packageNotFound("ghc-mod")
                }
                if (!BuildWrapper.check()) {
                    packageNotFound("buildwrapper")
                }
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

    {
        manager.addCompilableFileType(HaskellFileType.INSTANCE)
    }
}
