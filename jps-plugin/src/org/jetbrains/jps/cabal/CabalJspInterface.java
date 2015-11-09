package org.jetbrains.jps.cabal;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class CabalJspInterface{
    private final String myCabalPath;
    @NotNull
    private File myCabalFile;

    CabalJspInterface(String cabalPath, @NotNull File cabalFile) {
        myCabalPath = cabalPath;
        myCabalFile = cabalFile;
    }

    private Process runCommand(String ... command) throws IOException {
        final String path = myCabalPath != null ? myCabalPath : "cabal";
        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add(path);
        arguments.addAll(Arrays.asList(command));
        return new ProcessWrapper(myCabalFile.getParentFile().getCanonicalPath()).
                            getProcess(arguments);
    }

    public Process configure(String ghcPath) throws IOException {
        if (ghcPath != null) {
            return runCommand("configure", "-w", ghcPath);
        } else {
            return runCommand("configure");

        }
    }

    public Process build() throws IOException {
        return runCommand("build");
    }

    public Process clean() throws IOException {
        return runCommand("clean");
    }


}
