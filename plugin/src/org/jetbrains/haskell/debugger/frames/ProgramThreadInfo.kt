package org.jetbrains.haskell.debugger.frames

public class ProgramThreadInfo(public val id: String?,
                            public val name: String,
                            public var frames: List<HsStackFrameInfo>?) {

    public enum class State {
        RUNNING
        SUSPENDED
        KILLED
    }

    public var state: State = State.SUSPENDED
        private set

    public fun updateState(newState: State, newFrames: List<HsStackFrameInfo>?) {
        state = newState
        frames = if (newFrames == null || newFrames.size == 0) null else newFrames
    }
}