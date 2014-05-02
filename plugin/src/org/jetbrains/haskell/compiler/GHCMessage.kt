package org.jetbrains.haskell.compiler

/**
 * @author Evgeny.Kurbatsky
 */
public class GHCMessage(file: String, line: String, col: String, text: String) {
    public val myFile: String = file
    public val myLine: Int = Integer.parseInt(line)
    public val myCol: Int = Integer.parseInt(col)
    public var myText: String = text;
}
