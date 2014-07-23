package org.jetbrains.haskell.debugger.protocol

/**
 * Created by vlad on 7/15/14.
 */

public class StepOverCommand(callback: CommandCallback?) : StepCommand(callback) {
    override fun getBytes(): ByteArray = ":steplocal\n".toByteArray()
}
