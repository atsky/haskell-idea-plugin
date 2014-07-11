package org.jetbrains.haskell.debugger

import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.XSourcePosition
import com.intellij.execution.process.ProcessHandler
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.evaluation.EvaluationMode
import com.intellij.openapi.editor.Document

/**
 * Created by vlad on 7/10/14.
 */

public class GHCiDebugProcess(session: XDebugSession,
                              ghciProcess: Process,
                              executionConsole: ExecutionConsole,
                              processHandler: ProcessHandler) : XDebugProcess(session) {

//    private val editorsProvider: XDebuggerEditorsProvider
    private val debugger: ProcessDebugger

    {
//        editorsProvider = object : XDebuggerEditorsProvider() {
//            override fun getFileType(): FileType {
//                throw UnsupportedOperationException()
//            }
//            override fun createDocument(project: Project, text: String, sourcePosition: XSourcePosition?, mode: EvaluationMode): Document {
//                throw UnsupportedOperationException()
//            }
//
//        }
        debugger = GHCiDebugger(this, ghciProcess)
    }

    override fun getEditorsProvider(): XDebuggerEditorsProvider {
        throw UnsupportedOperationException()
//        return editorsProvider
    }

    override fun startStepOver() {
        throw UnsupportedOperationException()
    }

    override fun startStepInto() {
        throw UnsupportedOperationException()
    }

    override fun startStepOut() {
        throw UnsupportedOperationException()
    }

    override fun stop() {
        throw UnsupportedOperationException()
    }

    override fun resume() {
        throw UnsupportedOperationException()
    }

    override fun runToPosition(position: XSourcePosition) {
        throw UnsupportedOperationException()
    }


}