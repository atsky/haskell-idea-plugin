package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.parser.FilePosition
import org.jetbrains.haskell.debugger.parser.LocalBinding
import java.util.ArrayList

public class HaskellStackFrameInfo(public val filePosition: FilePosition, public val bindings: ArrayList<LocalBinding>)