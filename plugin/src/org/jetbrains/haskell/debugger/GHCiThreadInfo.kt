package org.jetbrains.haskell.debugger

/**
 * Represents info about debugging process thread. For now main thing here is frames property
 *
 * @author Habibullin Marat
 */
public class GHCiThreadInfo(public val id: String?,
                            public val name: String,
                            public var frames: List<HaskellStackFrameInfo>?) {

    public enum class State {
        RUNNING
        SUSPENDED
        KILLED
    }

    public var state: State = State.RUNNING
        private set

    public fun updateState(newState: State, newFrames: List<HaskellStackFrameInfo>?) {
        state = newState
        frames = if (newFrames == null || newFrames.size == 0) null else newFrames
    }
}