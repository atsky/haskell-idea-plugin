package org.jetbrains.haskell.run.haskell;

import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.Executor;
import com.intellij.execution.ExternalizablePath;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.haskell.fileType.HaskellFile;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class HaskellRunConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> implements CommonProgramRunConfigurationParameters {
    private static final Logger LOG = Logger.getInstance("ideah.run.HaskellRunConfiguration");

    private String myWorkingDir;
    private String mainFile;
    private String myProgramParameters;
    private Map<String, String> myEnvs = new HashMap<String, String>();

    public HaskellRunConfiguration(String name, Project project, ConfigurationFactory factory) {
        super(name, new RunConfigurationModule(project), factory);
    }

    public HaskellRunConfiguration(Project project, ConfigurationFactory factory) {
        this("Haskell", project, factory);
    }

    @Nullable
    public Module getModule() {
        return getConfigurationModule().getModule();
    }

    public Collection<Module> getValidModules() {
        Module[] modules = ModuleManager.getInstance(getProject()).getModules();
        return Arrays.asList(modules);
    }

    @Override
    protected HaskellRunConfiguration createInstance() {
        return new HaskellRunConfiguration(getName(), getProject(), getFactory());
    }

    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        Module[] modules = ModuleManager.getInstance(getProject()).getModules();
        return new ConfigurationEditor(modules);
    }

    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) {
        HaskellCommandLineState state = new HaskellCommandLineState(env, this);
        state.setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(getProject()));
        return state;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(this.mainFile);
        if (file == null || !file.exists())
            throw new RuntimeConfigurationException("Main file does not exist");
    }

    // getters/setters


    public String getMainFile() {
        return ExternalizablePath.localPathValue(mainFile);
    }

    public void setMainFile(HaskellFile mainFile) {
        VirtualFile file = mainFile.getVirtualFile();
        if (file != null) {
            this.mainFile = file.getUrl();
            Module module = getDeclModule(mainFile);
            setModule(module);
        }
    }

    public void setMainFile(Module module, String path) {
        setModule(module);
        this.mainFile = ExternalizablePath.urlValue(path);
    }


    @Override
    public String suggestedName() {
        VirtualFile file;
        if (mainFile == null) {
            file = null;
        } else {
            file = VirtualFileManager.getInstance().findFileByUrl(mainFile);
        }
        if (file == null) {
            return "Unnamed";
        } else {
            return file.getName();
        }
    }

    public void readExternal(Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);
        readModule(element);
        mainFile = JDOMExternalizer.readString(element, "mainFile");
    }

    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        writeModule(element);
        JDOMExternalizer.write(element, "mainFile", mainFile);
        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
    }

    @Override
    public void setProgramParameters(@Nullable String value) {
        myProgramParameters = value;
    }

    @Nullable
    @Override
    public String getProgramParameters() {
        return myProgramParameters;
    }

    @Override
    public void setWorkingDirectory(@Nullable String value) {
        myWorkingDir = value;
    }

    @Nullable
    @Override
    public String getWorkingDirectory() {
        return myWorkingDir;
    }

    @Override
    public void setEnvs(@NotNull Map<String, String> envs) {
        myEnvs = envs;
    }

    @NotNull
    @Override
    public Map<String, String> getEnvs() {
        return myEnvs;
    }

    @Override
    public void setPassParentEnvs(boolean passParentEnvs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isPassParentEnvs() {
        return false;
    }

    public static Module getDeclModule(PsiFile psiFile) {
        VirtualFile file = psiFile.getVirtualFile();
        if (file == null)
            return null;
        final Project project = psiFile.getProject();
        return ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(file);
    }

}
