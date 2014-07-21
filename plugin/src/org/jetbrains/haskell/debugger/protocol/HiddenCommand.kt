package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import java.util.Deque

/**
 * Created by vlad on 7/17/14.
 */

public abstract class HiddenCommand : AbstractCommand() {

    class object {
        public fun createInstance(command: String): HiddenCommand {
            return object : HiddenCommand() {
                override fun getBytes(): ByteArray {
                    return command.toByteArray()
                }

            }
        }
    }

    override fun handleOutput(output: Deque<String?>, debugProcess: HaskellDebugProcess) {
    }
}
