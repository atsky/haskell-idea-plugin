package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.parser.Parser

public class HaskellStackFrameInfo(public val filePosition: Parser.FilePosition) {
    public val startLine: Int = filePosition.startLine
    public val filePath: String = filePosition.file
}