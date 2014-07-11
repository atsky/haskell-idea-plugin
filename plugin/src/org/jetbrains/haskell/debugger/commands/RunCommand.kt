package org.jetbrains.haskell.debugger.commands

/**
 * Created by vlad on 7/10/14.
 */

public class RunCommand() : AbstractCommand("Run") {
    override fun getBytes(): ByteArray {
        return "main\n".toByteArray()
    }

}