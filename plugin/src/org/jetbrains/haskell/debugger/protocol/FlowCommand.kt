package org.jetbrains.haskell.debugger.protocol

import org.jetbrains.haskell.debugger.HaskellDebugProcess
import org.jetbrains.haskell.debugger.parser.Parser
import java.util.Deque
import org.jetbrains.haskell.debugger.parser.ParseResult
import org.jetbrains.haskell.debugger.parser.HsTopStackFrameInfo
import com.intellij.openapi.vfs.LocalFileSystem
import org.jetbrains.haskell.debugger.utils.HaskellUtils

/**
 * Created by vlad on 7/17/14.
 */

public abstract class FlowCommand(callback: CommandCallback?) : AbstractCommand(callback) {

    override fun parseOutput(output: Deque<String?>): ParseResult? = Parser.tryParseStoppedAt(output)

    class object {
        public class StandardFlowCallback(val debugProcess: HaskellDebugProcess) : CommandCallback() {
            override fun execAfterParsing(result: ParseResult?) {
                if (result != null && result is HsTopStackFrameInfo) {
                    val breakpoint = debugProcess.getBreakpointAtPosition(
                            HaskellUtils.getModuleName(
                                    debugProcess.getSession()!!.getProject(),
                                    LocalFileSystem.getInstance()!!.findFileByPath(result.filePosition.filePath)!!),
                            result.filePosition.startLine)!!
                    debugProcess.debugger.history(breakpoint, result)
                }
            }
        }
    }
}
