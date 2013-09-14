package org.jetbrains.haskell.util;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessRunner {

    public ProcessRunner() {
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
            final ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
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
