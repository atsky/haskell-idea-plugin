package org.jetbrains.haskell.compiler;

import com.intellij.compiler.CompilerConfiguration;
import com.intellij.compiler.impl.CompilerUtil;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleFileIndex;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.haskell.fileType.HaskellFileType;
import org.jetbrains.haskell.module.HaskellModuleType;
import org.jetbrains.haskell.util.ProcessRunner;


import java.io.IOException;
import java.util.*;

public final class HaskellCompiler implements TranslatingCompiler {

    private final Project project;

    public HaskellCompiler(Project project) {
        this.project = project;
    }

    public boolean isCompilableFile(VirtualFile file, CompileContext context) {
        FileType fileType = FileTypeManager.getInstance().getFileTypeByFile(file);
        return HaskellFileType.INSTANCE.equals(fileType);
    }

    public void compile(CompileContext context, Chunk<Module> moduleChunk, VirtualFile[] files, OutputSink sink) {
        Map<Module, List<VirtualFile>> mapModulesToVirtualFiles;
        if (moduleChunk.getNodes().size() == 1) {
            mapModulesToVirtualFiles = Collections.singletonMap(moduleChunk.getNodes().iterator().next(), Arrays.asList(files));
        } else {
            mapModulesToVirtualFiles = CompilerUtil.buildModuleToFilesMap(context, files);
        }
        for (Module module : moduleChunk.getNodes()) {
            List<VirtualFile> moduleFiles = mapModulesToVirtualFiles.get(module);
            if (moduleFiles == null) {
                continue;
            }

            ModuleFileIndex index = ModuleRootManager.getInstance(module).getFileIndex();
            List<VirtualFile> toCompile = new ArrayList<VirtualFile>();
            List<VirtualFile> toCompileTests = new ArrayList<VirtualFile>();
            CompilerConfiguration configuration = CompilerConfiguration.getInstance(project);

            if (isAcceptableModuleType(module)) {
                for (VirtualFile file : moduleFiles) {
                    if (shouldCompile(file, configuration)) {
                        (index.isInTestSourceContent(file) ? toCompileTests : toCompile).add(file);
                    }
                }
            }

            if (!toCompile.isEmpty()) {
                compileFiles(context, module, toCompile, sink, false);
            }
            if (!toCompileTests.isEmpty()) {
                compileFiles(context, module, toCompileTests, sink, true);
            }
        }
    }

    static VirtualFile getMainOutput(CompileContext compileContext, Module module, boolean tests) {
        return tests
            ? compileContext.getModuleOutputDirectoryForTests(module)
            : compileContext.getModuleOutputDirectory(module);
    }

    private static void compileFiles(CompileContext context, Module module, List<VirtualFile> toCompile,
                                     OutputSink sink, boolean tests) {

        VirtualFile outputDir = getMainOutput(context, module, tests);
        List<OutputItem> output = new ArrayList<OutputItem>();

        sink.add(outputDir.getPath(), output, VfsUtil.toVirtualFileArray(toCompile));

        for (VirtualFile file : toCompile) {
            for (GHCMessage message : new GHCInterface().runGHC(module, file, outputDir)) {
                context.addMessage(CompilerMessageCategory.ERROR,
                        message.myText, "file://" + message.myFile, message.myLine, message.myCol);
            }
        }
    }

    private static boolean shouldCompile(VirtualFile file, CompilerConfiguration configuration) {
        return !configuration.isResourceFile(file);
    }

    private static boolean isAcceptableModuleType(Module module) {
        return HaskellModuleType.get(module) instanceof HaskellModuleType;
    }

    @NotNull
    public String getDescription() {
        return "Haskell compiler";
    }

    public boolean validateConfiguration(CompileScope compileScope) {
        return true;
    }
}
