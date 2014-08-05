package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.ParseResult
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.LocalBindingList
import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.MoveHistResult

/**
 * Created by vlad on 8/4/14.
 */

public class BackCommand(callback: CommandCallback<MoveHistResult?>) : RealTimeCommand<MoveHistResult?>(callback) {
    override fun getText(): String = ":back\n"
    override fun parseGHCiOutput(output: Deque<String?>): MoveHistResult? = Parser.parseMoveHistResult(output)
}