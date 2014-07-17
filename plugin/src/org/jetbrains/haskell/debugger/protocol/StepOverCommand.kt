package org.jetbrains.haskell.debugger.protocol

/**
 * Created by vlad on 7/15/14.
 */

public class StepOverCommand : StepCommand() {

    override fun getBytes(): ByteArray {
        return ":steplocal\n".toByteArray()
    }

}
