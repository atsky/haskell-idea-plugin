package org.jetbrains.jps.cabal;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;


public class CabalJspInterface{
    private final String myCabalPath;
    @NotNull
    private File myCabalFile;

    CabalJspInterface(String cabalPath, @NotNull File cabalFile) {
        myCabalPath = cabalPath;
        myCabalFile = cabalFile;
    }

    private Process runCommand(String command) throws IOException {
        final String path = myCabalPath != null ? myCabalPath : "cabal";
        return new ProcessWrapper(myCabalFile.getParentFile().getCanonicalPath()).
                            getProcess(path, command);
    }

    public Process configure() throws IOException {
        return runCommand("configure");
    }

    public Process build() throws IOException {
        return runCommand("build");
    }

    public Process clean() throws IOException {
        return runCommand("clean");
    }


}
