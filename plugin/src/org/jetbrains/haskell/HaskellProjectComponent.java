package org.jetbrains.haskell;

import com.intellij.compiler.CompilerWorkspaceConfiguration;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.haskell.compiler.CabalCompiler;

import org.jetbrains.haskell.fileType.HaskellFileType;
import org.jetbrains.haskell.fileType.HiFileType;

import java.util.Collections;

public final class HaskellProjectComponent implements ProjectComponent {

    public HaskellProjectComponent(Project project, CompilerManager manager) {
        manager.addCompilableFileType(HaskellFileType.INSTANCE);
        manager.addTranslatingCompiler(
                new CabalCompiler(project),
                Collections.<FileType>singleton(HaskellFileType.INSTANCE),
                Collections.<FileType>singleton(HiFileType.INSTANCE));
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
