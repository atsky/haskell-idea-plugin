package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import java.util.ArrayList

public class ProgramThreadInfo(public val id: String?,
                            public val name: String,
                            public val topFrameInfo: HsStackFrameInfo) {

    public enum class State {
        RUNNING
        SUSPENDED
        KILLED
    }

    public var state: State = State.SUSPENDED
        private set
}