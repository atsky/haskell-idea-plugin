package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo
import org.jetbrains.haskell.debugger.parser.HsCommonStackFrameInfo
import java.util.ArrayList

public class ProgramThreadInfo(public val id: String?,
                            public val name: String,
                            public val topFrameInfo: HsTopStackFrameInfo,
                            public var histFramesInfo: ArrayList<HsCommonStackFrameInfo>) {

    public enum class State {
        RUNNING
        SUSPENDED
        KILLED
    }

    public var state: State = State.SUSPENDED
        private set
}