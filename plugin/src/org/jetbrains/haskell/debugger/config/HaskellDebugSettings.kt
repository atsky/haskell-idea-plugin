package org.jetbrains.haskell.debugger.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.ServiceManager

/**
 * Created by vlad on 8/1/14.
 */

com.intellij.openapi.components.State(
        name = "HaskellDebugConfiguration",
        storages = array(
            Storage(id = "default", file = StoragePathMacros.APP_CONFIG + "/haskelldebug.xml")
        )
)
public class HaskellDebugSettings : PersistentStateComponent<HaskellDebugSettings.State> {
    class object {
        public enum class DebuggerType {
            GHCI
            REMOTE
        }

        public class State {
            public var debuggerType: DebuggerType = DebuggerType.GHCI
            public var remoteDebuggerPath: String? = null
            public var traceOff: Boolean = false
            public var printDebugOutput: Boolean = false
        }

        public fun getInstance(): HaskellDebugSettings {
            val persisted = ServiceManager.getService(javaClass<HaskellDebugSettings>())
            if (persisted == null) {
                return HaskellDebugSettings()
            }
            return persisted
        }
    }

    private var myState = State()

    override fun getState(): State {
        return myState
    }

    override fun loadState(state: State?) {
        if (state == null) {
            throw RuntimeException("given state is null")
        }
        this.myState = state
    }
}