package org.jetbrains.haskell.debugger.commands

/**
 * Created by vlad on 7/15/14.
 */

public class StepOverCommand : AbstractCommand() {

    override fun getBytes(): ByteArray {
        return ":steplocal\n".toByteArray()
    }

}
