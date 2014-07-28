package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo

/**
 * Created by vlad on 7/15/14.
 */

public class StepOverCommand(callback: CommandCallback<HsTopStackFrameInfo?>?) : StepCommand(callback) {
    override fun getBytes(): ByteArray = ":steplocal\n".toByteArray()
}
