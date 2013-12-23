/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.jps.cabal;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Consumer;
import com.intellij.util.SystemProperties;
import com.intellij.util.containers.ContainerUtilRt;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.asm4.ClassReader;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.ProjectPaths;
import org.jetbrains.jps.builders.BuildRootIndex;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.FileProcessor;
import org.jetbrains.jps.builders.java.JavaBuilderUtil;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.builders.java.dependencyView.Callbacks;
import org.jetbrains.jps.builders.java.dependencyView.Mappings;
import org.jetbrains.jps.builders.storage.SourceToOutputMapping;
import org.jetbrains.jps.cmdline.ClasspathBootstrap;
import org.jetbrains.jps.cmdline.ProjectDescriptor;
import org.jetbrains.jps.incremental.*;
import org.jetbrains.jps.incremental.java.ClassPostProcessor;
import org.jetbrains.jps.incremental.java.JavaBuilder;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.incremental.messages.ProgressMessage;
import org.jetbrains.jps.javac.OutputFileObject;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
import org.jetbrains.jps.model.java.JpsJavaSdkType;
import org.jetbrains.jps.model.java.compiler.JpsJavaCompilerConfiguration;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.service.JpsServiceManager;
import org.jetbrains.jps.service.SharedThreadPool;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;


public class CabalBuilder extends ModuleLevelBuilder {


  public CabalBuilder() {
    super(BuilderCategory.TRANSLATOR);
  }


  public ExitCode build(final CompileContext context,
                                           ModuleChunk chunk,
                                           DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder,
                                           OutputConsumer outputConsumer) throws ProjectBuildException {
      return ExitCode.ABORT;
  }


  @Override
  public List<String> getCompilableFileExtensions() {
    return Arrays.asList("hs");
  }

  @Override
  public String toString() {
    return getPresentableName();
  }

  @NotNull
  public String getPresentableName() {
    return "Cabal builder";
  }

}
