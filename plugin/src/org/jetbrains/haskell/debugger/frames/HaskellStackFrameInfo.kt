package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.parser.FilePosition

public class HaskellStackFrameInfo(public val filePosition: FilePosition) {
    public val startLine: Int = filePosition.startLine
    public val filePath: String = filePosition.filePath
}