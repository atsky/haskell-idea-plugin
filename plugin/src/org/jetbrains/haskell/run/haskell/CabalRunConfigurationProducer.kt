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
import org.jetbrains.cabal.findCabal


public class CabalRunConfigurationProducer() : RunConfigurationProducer<CabalRunConfiguration>(HaskellRunConfigurationType.INSTANCE) {


    override fun setupConfigurationFromContext(configuration: CabalRunConfiguration?,
                                               context: ConfigurationContext?,
                                               sourceElement: Ref<PsiElement>?): Boolean {
        val file = sourceElement!!.get()!!.getContainingFile()
        if (!(file is HaskellFile)) {
            return false
        }
        try {
            val virtualFile = file.getVirtualFile()
            if (virtualFile == null) {
                return false
            }
            val project = file.getProject()

            val module = ProjectRootManager.getInstance(project)!!.getFileIndex().getModuleForFile(virtualFile)

            val psiFile = CabalInterface(project).getPsiFile(findCabal(module!!)!!)
            val name = psiFile.getExecutables().get(0).getExecutableName()

            configuration!!.setMyExecutableName(name)
            configuration.setModule(module)

            val baseDir = project.getBaseDir()
            if (baseDir != null) {
                configuration.setWorkingDirectory(baseDir.getPath())
            }
            configuration.setName(configuration.suggestedName())
            return true
        } catch (ex: Exception) {
            LOG.error(ex)
        }

        return false
    }

    override fun isConfigurationFromContext(configuration: CabalRunConfiguration?, context: ConfigurationContext?): Boolean {
        return context!!.getPsiLocation()!!.getContainingFile() is HaskellFile
    }

    class object {

        private val LOG: Logger = Logger.getInstance("ideah.run.CabalRunConfigurationProducer")!!
    }
}
