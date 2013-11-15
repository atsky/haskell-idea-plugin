package org.jetbrains.haskell.run.haskell;

import com.intellij.execution.Location;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.junit.RuntimeConfigurationProducer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.haskell.fileType.HaskellFile;
import org.jetbrains.haskell.run.haskell.HaskellRunConfiguration;
import org.jetbrains.haskell.run.haskell.HaskellRunConfigurationType;

public final class HaskellRunConfigurationProducer extends RuntimeConfigurationProducer {

    private HaskellFile runFile;

    private static final Logger LOG = Logger.getInstance("ideah.run.HaskellRunConfigurationProducer");

    public HaskellRunConfigurationProducer() {
        super(HaskellRunConfigurationType.INSTANCE);
    }

    public PsiElement getSourceElement() {
        return runFile;
    }

    protected RunnerAndConfigurationSettings createConfigurationByElement(Location location, ConfigurationContext context) {
        PsiFile file = location.getPsiElement().getContainingFile();
        if (!(file instanceof HaskellFile))
            return null;
        HaskellFile hsFile = (HaskellFile) file;
        try {
            VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null)
                return null;
            runFile = hsFile;
            Project project = file.getProject();
            RunnerAndConfigurationSettings settings = cloneTemplateConfiguration(project, context);
            HaskellRunConfiguration configuration = (HaskellRunConfiguration) settings.getConfiguration();
            configuration.setMainFile(runFile);
            VirtualFile baseDir = project.getBaseDir();
            if (baseDir != null) {
                configuration.setWorkingDirectory(baseDir.getPath());
            }
            configuration.setName(configuration.getGeneratedName());
            return settings;
        } catch (Exception ex) {
            LOG.error(ex);
        }
        return null;
    }

    public int compareTo(Object o) {
        return PREFERED;
    }


}
