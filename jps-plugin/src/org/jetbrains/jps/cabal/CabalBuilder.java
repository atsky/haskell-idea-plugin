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


import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.*;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.incremental.messages.ProgressMessage;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class CabalBuilder extends ModuleLevelBuilder {


    public CabalBuilder() {
        super(BuilderCategory.TRANSLATOR);
    }


    public ExitCode build(final CompileContext context,
                          final ModuleChunk chunk,
                          final DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder,
                          final OutputConsumer outputConsumer) throws ProjectBuildException {
        try {
            for (JpsModule module : chunk.getModules()) {
                File cabalFile = getCabalFile(module);
                CabalJspInterface cabal = new CabalJspInterface(cabalFile);

                context.processMessage(new CompilerMessage("cabal", BuildMessage.Kind.INFO, "Start configure"));

                Process configureProcess = cabal.configure();

                Iterator<String> processOut = collectOutput(configureProcess);

                while (processOut.hasNext()) {
                    String line = processOut.next();
                    String warningPrefix = "Warning: ";
                    if (line.startsWith(warningPrefix)) {
                        String text = line.substring(warningPrefix.length()) + "\n" + processOut.next();
                        context.processMessage(new CompilerMessage("cabal", BuildMessage.Kind.WARNING, text));
                    } else {
                        context.processMessage(new CompilerMessage("cabal", BuildMessage.Kind.INFO, line));
                    }
                }

                if (configureProcess.waitFor() != 0) {
                    context.processMessage(new CompilerMessage(
                            "cabal",
                            BuildMessage.Kind.ERROR,
                            "configure failed."));
                    return ExitCode.ABORT;
                }
                context.processMessage(new ProgressMessage("Build build"));
                context.processMessage(new CompilerMessage("ghc", BuildMessage.Kind.INFO, "Start build"));
                Process buildProcess = cabal.build();
                processOut = collectOutput(buildProcess);

                while (processOut.hasNext()) {
                    String line = processOut.next();
                    context.processMessage(new CompilerMessage("cabal", BuildMessage.Kind.INFO, line));
                }

                if (buildProcess.waitFor() == 0) {
                    return ExitCode.OK;
                } else {
                    context.processMessage(new CompilerMessage("cabal", BuildMessage.Kind.ERROR, "build errors."));
                }
            }
            return ExitCode.ABORT;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ExitCode.ABORT;
    }

    private Iterator<String> collectOutput(Process process) throws IOException {
        final BufferedReader reader = new BufferedReader (new InputStreamReader(process.getInputStream()));
        return new Iterator<String>() {

            String line = null;

            @Override
            public boolean hasNext() {
                return fetch() != null;
            }

            private String fetch() {
                if (line == null) {
                    try {
                        line = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return line;
            }

            @Override
            public String next() {
                String result = fetch();
                line = null;
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private File getCabalFile(JpsModule module) {
        String url = module.getContentRootsList().getUrls().get(0);
        try {
            for (File file : new File(new URL(url).getFile()).listFiles()) {
                if (file.getName().endsWith(".cabal")) {
                    return file;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
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
