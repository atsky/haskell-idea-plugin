package org.jetbrains.haskell.run.haskell

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.cabal.CabalFile
import org.jetbrains.cabal.CabalInterface
import org.jetbrains.haskell.fileType.HaskellFile


class CabalRunConfigurationProducer : RunConfigurationProducer<CabalRunConfiguration>(HaskellRunConfigurationType.INSTANCE) {


    override fun setupConfigurationFromContext(configuration: CabalRunConfiguration?,
                                               context: ConfigurationContext?,
                                               sourceElement: Ref<PsiElement>?): Boolean {
        val file = sourceElement!!.get()!!.containingFile
        if (file !is HaskellFile) {
            return false
        }
        try {
            val virtualFile = file.getVirtualFile()
            if (virtualFile == null) {
                return false
            }
            val project = file.getProject()

            val module = ProjectRootManager.getInstance(project)!!.fileIndex.getModuleForFile(virtualFile)

            val cabal = CabalInterface.findCabal(module!!)
            if (cabal == null) {
                return false
            }
            val psiFile = CabalInterface(project).getPsiFile(cabal)
            val executables = psiFile.getExecutables()

            val name = if (executables.size > 0) {
                executables.get(0).getExecutableName()
            } else {
                "Default"
            }

            configuration!!.myExecutableName = name
            configuration.module = module

            val baseDir = project.baseDir
            if (baseDir != null) {
                configuration.workingDirectory = baseDir.path
            }
            configuration.name = configuration.suggestedName()
            return true
        } catch (ex: Exception) {
            LOG.error(ex)
        }

        return false
    }

    override fun isConfigurationFromContext(configuration: CabalRunConfiguration?, context: ConfigurationContext?): Boolean {
        return context!!.psiLocation!!.containingFile is HaskellFile
    }

    companion object {

        private val LOG: Logger = Logger.getInstance("ideah.run.CabalRunConfigurationProducer")
    }
}
