package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo

/**
 * Created by vlad on 7/15/14.
 */

class StepOverCommand(callback: CommandCallback<HsStackFrameInfo?>?) : StepCommand(callback) {
    override fun getText(): String = ":steplocal\n"
}
