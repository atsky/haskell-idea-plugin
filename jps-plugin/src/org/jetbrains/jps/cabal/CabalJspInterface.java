package org.jetbrains.jps.cabal;

import java.io.File;
import java.io.IOException;


public class CabalJspInterface{
    private File myCabalFile;

    CabalJspInterface(File cabalFile) {
        myCabalFile = cabalFile;
    }

    private Process runCommand(String command) throws IOException {
        return new ProcessWrapper(myCabalFile.getParentFile().getCanonicalPath()).
                            getProcess("cabal", command);
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
