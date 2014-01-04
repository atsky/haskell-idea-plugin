package org.jetbrains.jps.cabal;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ProcessWrapper {
    private String myWorkingDirectory;

    ProcessWrapper(String workingDirectory) {
        myWorkingDirectory = workingDirectory;
    }


    public Process getProcess(String ... cmd) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList(cmd));
        if (myWorkingDirectory != null) {
            processBuilder.directory(new File(myWorkingDirectory));
        }

        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }
}
