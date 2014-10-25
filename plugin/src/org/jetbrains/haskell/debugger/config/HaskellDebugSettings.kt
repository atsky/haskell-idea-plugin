package org.jetbrains.haskell.debugger.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.ServiceManager
import java.io.File
import org.jetbrains.haskell.util.*


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
            public var debuggerType: DebuggerType = DebuggerType.REMOTE
            public var remoteDebuggerPath: String? = null
            public var traceOff: Boolean = false
            public var printDebugOutput: Boolean = false
        }

        public fun getInstance(): HaskellDebugSettings {
            val persisted = ServiceManager.getService(javaClass<HaskellDebugSettings>())
            if (persisted == null) {
                val settings = HaskellDebugSettings()
                settings.update();
                return settings
            }
            persisted.update();
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

        update()
    }

    private fun update() {
        if (myState.remoteDebuggerPath == null || myState.remoteDebuggerPath == "") {
            myState.remoteDebuggerPath = OS.getDefaultCabalBin() + File.separator + "remote-debugger" + OS.getExe();
        }
    }
}