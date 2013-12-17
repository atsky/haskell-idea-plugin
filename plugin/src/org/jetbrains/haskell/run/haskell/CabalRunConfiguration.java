package org.jetbrains.haskell.run.haskell;

import com.intellij.execution.CommonProgramRunConfigurationParameters;
import com.intellij.execution.Executor;
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
import com.intellij.psi.PsiFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class CabalRunConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> implements CommonProgramRunConfigurationParameters {
    private static final Logger LOG = Logger.getInstance("ideah.run.CabalRunConfiguration");
    public static final String EXECUTABLE_NAME_ELEMENT = "executableName";
    public static final String PARAMETERS_ELEMENT = "parameters";
    public static final String WORKING_DIR = "workingDir";
    public static final String ENVIRONMENT_VARIABLES = "environmetVariables";
    public static final String VAR_ENTRY_NAME = "var";

    private String myWorkingDir;
    private String myExecutableName;
    private String myProgramParameters;
    private Map<String, String> myEnvs = new HashMap<String, String>();

    public CabalRunConfiguration(String name, Project project, ConfigurationFactory factory) {
        super(name, new RunConfigurationModule(project), factory);
    }

    public CabalRunConfiguration(Project project, ConfigurationFactory factory) {
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
    protected CabalRunConfiguration createInstance() {
        return new CabalRunConfiguration(getName(), getProject(), getFactory());
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
    }

    // getters/setters


    public String getMyExecutableName() {
        return myExecutableName;
    }

    public void setMyExecutableName(String name) {
        myExecutableName = name;
    }


    @Override
    public String suggestedName() {
        if (myExecutableName != null) {
            return myExecutableName;
        } else {
            return "Unnamed";
        }
    }

    public void readExternal(Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);
        readModule(element);
        myExecutableName = JDOMExternalizer.readString(element, EXECUTABLE_NAME_ELEMENT);
        myProgramParameters = JDOMExternalizer.readString(element, PARAMETERS_ELEMENT);
        myWorkingDir = JDOMExternalizer.readString(element, WORKING_DIR);
        JDOMExternalizer.readMap(element, myEnvs, ENVIRONMENT_VARIABLES, VAR_ENTRY_NAME);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        writeModule(element);
        JDOMExternalizer.write(element, EXECUTABLE_NAME_ELEMENT, myExecutableName);
        JDOMExternalizer.write(element, PARAMETERS_ELEMENT, myProgramParameters);
        JDOMExternalizer.write(element, WORKING_DIR, myWorkingDir);
        JDOMExternalizer.writeMap(element, myEnvs, ENVIRONMENT_VARIABLES, VAR_ENTRY_NAME);
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
