package org.jetbrains.haskell.debugger.commands

/**
 * Created by vlad on 7/16/14.
 */

public class HistoryCommand : AbstractCommand() {

    override fun getBytes(): ByteArray {
        return ":hist\n".toByteArray()
    }

}
