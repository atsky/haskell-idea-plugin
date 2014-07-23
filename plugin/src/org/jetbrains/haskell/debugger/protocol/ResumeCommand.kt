package org.jetbrains.haskell.debugger.protocol

/**
 * Created by vlad on 7/16/14.
 */

public class ResumeCommand(callback: CommandCallback?) : FlowCommand(callback) {
    override fun getBytes(): ByteArray = ":continue\n".toByteArray()
}
