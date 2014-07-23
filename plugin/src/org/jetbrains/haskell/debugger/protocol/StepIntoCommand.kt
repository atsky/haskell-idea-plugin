package org.jetbrains.haskell.debugger.protocol

/**
 * Created by vlad on 7/15/14.
 */

public class StepIntoCommand(callback: CommandCallback?) : StepCommand(callback) {

    override fun getBytes(): ByteArray = ":step\n".toByteArray()
}
