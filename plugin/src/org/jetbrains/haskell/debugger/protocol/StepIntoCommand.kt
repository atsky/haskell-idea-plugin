package org.jetbrains.haskell.debugger.protocol

/**
 * Created by vlad on 7/15/14.
 */

public class StepIntoCommand : AbstractCommand() {

    override fun getBytes(): ByteArray {
        return ":step\n".toByteArray()
    }

}
