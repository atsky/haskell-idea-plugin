package org.jetbrains.haskell.compiler;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.haskell.util.ProcessRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Evgeny.Kurbatsky
 */
public class GHCInterface {
    public static String getGHC() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return "C:\\Program Files (x86)\\Haskell Platform\\2013.2.0.0\\bin\\ghc.exe";
        } else {
            return "/usr/bin/ghc";
        }
    }

    public static String getRunGHC() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return "C:\\Program Files (x86)\\Haskell Platform\\2013.2.0.0\\bin\\runghc.exe";
        } else {
            return "/usr/bin/runghc";
        }
    }

    public List<GHCMessage> runGHC(VirtualFile file, VirtualFile outputDir) {
        final String[] command = new String[] {getGHC(),  "-c", "-outputdir", outputDir.getPath(), file.getPath()};
        final String result = new ProcessRunner().execute(command);
        System.out.println(result);
        final List<String> lines = Arrays.asList(result.split("\n"));
        final Iterator<String> iterator = lines.iterator();
        List<GHCMessage> messages = new ArrayList<GHCMessage>();

        while (iterator.hasNext()) {
            String line = iterator.next();
            if (isError(line)) {
                Matcher matcher = Pattern.compile("(.*):(\\d+):(\\d+):").matcher(line);
                matcher.find();
                GHCMessage message = new GHCMessage(matcher.group(1), matcher.group(2), matcher.group(3));

                String msg = "";
                while (iterator.hasNext()) {
                    String msgLine = iterator.next();
                    msg += msgLine + "\n";
                    if (msgLine.trim().length() == 0) {
                        break;
                    }
                }
                message.setText(msg);
                messages.add(message);
            }
        }
        return messages;
    }

    private boolean isError(String line) {
        return line.matches(".*:.*:.*:");
    }

}
