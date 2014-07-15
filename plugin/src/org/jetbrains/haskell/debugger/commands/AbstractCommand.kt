package org.jetbrains.haskell.debugger.commands

import org.jetbrains.haskell.debugger.GHCiDebugProcess

/**
 * Created by vlad on 7/10/14.
 */

public abstract class AbstractCommand() {
    public abstract fun getBytes(): ByteArray

}