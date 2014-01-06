package org.jetbrains.haskell.run.haskell;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.cabal.CabalFile;
import org.jetbrains.cabal.CabalInterface;
import org.jetbrains.cabal.CabalPackage;
import org.jetbrains.haskell.fileType.HaskellFile;


public final class CabalRunConfigurationProducer extends RunConfigurationProducer<CabalRunConfiguration> {

    private static final Logger LOG = Logger.getInstance("ideah.run.CabalRunConfigurationProducer");

    public CabalRunConfigurationProducer() {
        super(HaskellRunConfigurationType.INSTANCE);
    }


    @Override
    protected boolean setupConfigurationFromContext(CabalRunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
        PsiFile file = sourceElement.get().getContainingFile();
        if (!(file instanceof HaskellFile)) {
            return false;
        }
        try {
            VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null) {
                return false;
            }
            Project project = file.getProject();

            Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(virtualFile);

            CabalFile psiFile = new CabalInterface(project).getPsiFile(CabalPackage.findCabal(module));
            String name = psiFile.getExecutables().get(0).getExecutableName();

            configuration.setMyExecutableName(name);
            configuration.setModule(module);

            VirtualFile baseDir = project.getBaseDir();
            if (baseDir != null) {
                configuration.setWorkingDirectory(baseDir.getPath());
            }
            configuration.setName(configuration.suggestedName());
            return true;
        } catch (Exception ex) {
            LOG.error(ex);
        }
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(CabalRunConfiguration configuration, ConfigurationContext context) {
        PsiFile file = context.getPsiLocation().getContainingFile();
        return file instanceof HaskellFile;
    }
}
