package org.jetbrains.haskell.repl;

import com.intellij.execution.*;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.console.ConsoleHistoryController;
import com.intellij.execution.console.ConsoleRootType;
import com.intellij.execution.console.ProcessBackedConsoleExecuteActionHandler;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.haskell.sdk.HaskellSdkType;
import org.jetbrains.haskell.util.GHCUtil;

import java.io.File;
import java.util.Arrays;

public final class HaskellConsoleRunner extends AbstractConsoleRunnerWithHistory<HaskellConsole> {
    static final String REPL_TITLE = "GHCi";

    private final ConsoleRootType myType = new HaskellConsoleRootType();
    private final Module module;
    private final Project project;
    private final String consoleTitle;
    private final String workingDir;
    private GeneralCommandLine cmdline;


    private HaskellConsoleRunner(@NotNull Module module,
                                 @NotNull String consoleTitle,
                                 @Nullable String workingDir) {
        super(module.getProject(), consoleTitle, workingDir);

        this.module = module;
        this.project = module.getProject();
        this.consoleTitle = consoleTitle;
        this.workingDir = workingDir;
    }

    public static HaskellConsoleProcessHandler run(@NotNull Module module) {
        String srcRoot = ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();
        String path = srcRoot + File.separator + "src";
        HaskellConsoleRunner runner = new HaskellConsoleRunner(module, REPL_TITLE, path);
        try {
            runner.initAndRun();
            return (HaskellConsoleProcessHandler) runner.getProcessHandler();
        } catch (ExecutionException e) {
            ExecutionHelper.showErrors(module.getProject(), Arrays.<Exception>asList(e), REPL_TITLE, null);
            return null;
        }
    }

    @NotNull
    @Override
    protected ProcessBackedConsoleExecuteActionHandler createExecuteActionHandler() {
        new ConsoleHistoryController(myType, "", getConsoleView()).install();
        return new ProcessBackedConsoleExecuteActionHandler(getProcessHandler(), false);
    }

    protected HaskellConsole createConsoleView() {
        return new HaskellConsole(project, consoleTitle);
    }

    @Nullable
    @Override
    protected Process createProcess() throws ExecutionException {
        cmdline = createCommandLine(module, workingDir);
        return cmdline.createProcess();
    }

    @Override
    protected OSProcessHandler createProcessHandler(Process process) {
        return new HaskellConsoleProcessHandler(process, cmdline.getCommandLineString(), getConsoleView());
    }

    private static GeneralCommandLine createCommandLine(Module module, String workingDir) throws CantRunException {
        Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
        VirtualFile homePath;
        if (sdk == null || !(sdk.getSdkType() instanceof HaskellSdkType) || sdk.getHomePath() == null) {
            throw new CantRunException("Invalid SDK Home path set. Please set your SDK path correctly.");
        } else {
            homePath = sdk.getHomeDirectory();
        }
        GeneralCommandLine line = new GeneralCommandLine();
        line.setExePath(GHCUtil.getCommandPath(homePath, "ghci"));
        line.withWorkDirectory(workingDir);

        return line;
    }
}
