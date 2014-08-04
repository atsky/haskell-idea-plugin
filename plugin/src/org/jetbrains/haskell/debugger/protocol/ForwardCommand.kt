package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.MoveHistResult
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.Parser
import org.jetbrains.haskell.debugger.HaskellDebugProcess

/**
 * Created by vlad on 8/4/14.
 */

public class ForwardCommand(callback: CommandCallback<MoveHistResult?>) : RealTimeCommand<MoveHistResult?>(callback) {
    override fun getBytes(): ByteArray = ":forward\n".toByteArray()
    override fun parseGHCiOutput(output: Deque<String?>): MoveHistResult? = Parser.parseMoveHistResult(output)
}