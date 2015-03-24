package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.parser.LocalBinding
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.GHCiParser
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.Condition
import org.json.simple.JSONObject

/**
 * Created by vlad on 8/5/14.
 */

public class PrintCommand(private val bindingName: String, callback: CommandCallback<LocalBinding?>)
: RealTimeCommand<LocalBinding?>(callback) {
    override fun getText(): String = ":sprint $bindingName\n"

    override fun parseGHCiOutput(output: Deque<String?>): LocalBinding? = GHCiParser.tryParseAnyPrintCommandOutput(output)

    override fun parseJSONOutput(output: JSONObject): LocalBinding? {
        throw RuntimeException("Not used in remote debugger")
    }

    companion object {
        public class StandardPrintCallback(val localBinding: LocalBinding, val syncObject: Lock, val bindingValueIsSet: Condition)
        : CommandCallback<LocalBinding?>() {
            override fun execAfterParsing(result: LocalBinding?) {
                syncObject.lock()
                try {
                    if (result != null && result.name != null && result.name == localBinding.name) {
                        localBinding.value = result.value
                    } else {
                        localBinding.value = ""
                    }
                } finally {
                    bindingValueIsSet.signal()
                    syncObject.unlock()
                }
            }
        }
    }
}