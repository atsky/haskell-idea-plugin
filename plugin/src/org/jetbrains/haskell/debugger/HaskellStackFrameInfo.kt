package org.jetbrains.haskell.debugger

import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.parser.Parser

/**
 * @author Habibullin Marat
 */
public class HaskellStackFrameInfo(private val filePosition: Parser.FilePosition) {
    public val startLine: Int = filePosition.startLine
    public val filePath: String = filePosition.file
}