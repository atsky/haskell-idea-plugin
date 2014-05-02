package org.jetbrains.haskell;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.fileType.HaskellFileType;


public final class HaskellProjectComponent implements ProjectComponent {

    public HaskellProjectComponent(Project project, CompilerManager manager) {
        manager.addCompilableFileType(HaskellFileType.INSTANCE);
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
