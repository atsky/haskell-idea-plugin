package org.jetbrains.haskell;

import com.intellij.compiler.CompilerWorkspaceConfiguration;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.compiler.HaskellCompiler;
import org.jetbrains.haskell.fileType.HaskellFileType;
import org.jetbrains.haskell.fileType.HiFileType;

import java.util.Collections;
import java.util.HashSet;

public final class HaskellProjectComponent implements ProjectComponent {
    private final Project project;
   
    public HaskellProjectComponent(Project project, CompilerManager manager) {
        this.project = project;

        CompilerWorkspaceConfiguration.getInstance(project).USE_COMPILE_SERVER = false;

        HashSet<FileType> inputSet = new HashSet<FileType>(Collections.singleton(HaskellFileType.INSTANCE));
        HashSet<FileType> outputSet = new HashSet<FileType>(Collections.singleton(HiFileType.INSTANCE));
        manager.addCompilableFileType(HaskellFileType.INSTANCE);
        manager.addTranslatingCompiler(new HaskellCompiler(project), inputSet, outputSet);
    }

    public void projectOpened() {

    }

    public void projectClosed() {
    }

    @NotNull
    public String getComponentName() {
        return "HaskellProjectComponent";
    }

    public void initComponent() {
     
    }

    public void disposeComponent() {
     
    }
}
