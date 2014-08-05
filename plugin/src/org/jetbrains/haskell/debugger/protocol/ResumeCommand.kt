package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.frames.HsTopStackFrame
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo

/**
 * Created by vlad on 7/16/14.
 */

public class ResumeCommand(callback: CommandCallback<HsTopStackFrameInfo?>?) : FlowCommand(callback) {
    override fun getText(): String = ":continue\n"
}
