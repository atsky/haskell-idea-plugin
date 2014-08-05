package org.jetbrains.haskell.debugger.frames

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.HsStackFrameInfo
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.openapi.application.ApplicationManager
import java.util.ArrayList
import org.jetbrains.haskell.debugger.parser.LocalBinding
import com.intellij.xdebugger.frame.XValueChildrenList

/**
 * Created by marat-x on 7/22/14.
 */
public class HsTopStackFrame(debugProcess: HaskellDebugProcess,
                             stackFrameInfo: HsStackFrameInfo)
                           : HsStackFrame(debugProcess, stackFrameInfo.filePosition, stackFrameInfo.bindings) {

    override fun tryGetBindings() {
        // does nothing, because there is no way to get bindings if they are not set
    }
}