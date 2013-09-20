package org.jetbrains.haskell.util;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessRunner {
    private String myWorkingDirectory = null;

    public ProcessRunner() {
    }

    public ProcessRunner(String workingDirectory) {
        myWorkingDirectory = workingDirectory;
    }

    public static void readData(InputStream input, Callback callback) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                return;
            }
            callback.call(line);
        }
    }

    public String execute(String ... cmd) {
        return execute(cmd, null);
    }

    public String execute(String [] cmd, String input) {

        try {
            Process process = getProcess(cmd);
            if (input != null) {
                final OutputStreamWriter streamWriter = new OutputStreamWriter(process.getOutputStream());
                streamWriter.write(input);
                streamWriter.close();
            }
            InputStream myInput = process.getInputStream();
            process.waitFor();
            return readData(myInput);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Process getProcess(String ... cmd) throws IOException {
        final ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        if (myWorkingDirectory != null) {
            processBuilder.directory(new File(myWorkingDirectory));
        }
        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }

    private static String readData(InputStream input) throws IOException {
        final StringBuilder builder = new StringBuilder();
        readData(input, new Callback() {
            @Override
            public boolean call(String command) {
                builder.append(command).append("\n");
                return true;
            }
        });
        return builder.toString();
    }

    public static interface Callback {
        public boolean call(String command);
    }
}
