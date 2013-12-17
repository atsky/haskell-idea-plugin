package org.jetbrains.haskell.compiler;

/**
 * @author Evgeny.Kurbatsky
 */
public class GHCMessage {
    public final String myFile;
    public  final int myLine;
    public final int myCol;
    public String myText;

    public GHCMessage(String file, String line, String col) {
        myFile = file;
        myLine = Integer.parseInt(line);
        myCol = Integer.parseInt(col);
    }

    public void setText(String text) {
        myText = text;
    }
}
