package org.jetbrains.haskell.debugger.highlighting

import com.intellij.xdebugger.XDebugSessionListener
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.impl.ui.ExecutionPointHighlighter
import com.intellij.openapi.project.Project
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.Editor
import com.intellij.xdebugger.XSourcePosition
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.Key
import java.util.concurrent.atomic.AtomicBoolean
import com.intellij.ui.AppUIUtil
import com.intellij.xdebugger.impl.XSourcePositionImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.xdebugger.impl.XDebuggerUtilImpl
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.xdebugger.ui.DebuggerColors
import org.jetbrains.haskell.debugger.frames.HsStackFrame
import org.jetbrains.haskell.debugger.parser.HsFilePosition
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import org.jetbrains.haskell.run.haskell.HaskellCommandLineState
import org.jetbrains.haskell.debugger.utils.HaskellUtils
import java.awt.font.TextAttribute
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.Colors
import com.intellij.openapi.editor.colors.EditorFontType
import org.intellij.lang.annotations.JdkConstants.FontStyle
import com.intellij.xdebugger.impl.XDebuggerManagerImpl

/**
 * Modified copy of com.intellij.xdebugger.impl.ui.ExecutionPointHighlighter. Differences:
 * 1) Simplified (not all methods implemented)
 * 2) Highlights code ranges, not just lines
 *
 * @author Habibullin Marat
 */
public class HsExecutionPointHighlighter(private val myProject: Project) {
    private var myRangeHighlighter: RangeHighlighter? = null
    private var myEditor: Editor? = null
    private var filePosition: HsFilePosition? = null
    private var myOpenFileDescriptor: OpenFileDescriptor? = null
    private var myUseSelection: Boolean = false
    private var myGutterIconRenderer: GutterIconRenderer? = null
    private val EXECUTION_POINT_HIGHLIGHTER_KEY: Key<Boolean>? = Key.create("EXECUTION_POINT_HIGHLIGHTER_KEY")

    private val updateRequested = AtomicBoolean()

    public fun show(stackFrame: HsStackFrame, useSelection: Boolean, gutterIconRenderer: GutterIconRenderer?) {
        updateRequested.set(false)
        AppUIUtil.invokeLaterIfProjectAlive(myProject, object : Runnable {
            override fun run() {
                updateRequested.set(false)

                filePosition = stackFrame.filePosition

                myOpenFileDescriptor = XSourcePositionImpl.createOpenFileDescriptor(myProject, stackFrame.hackSourcePosition!!)
                myOpenFileDescriptor!!.setUseCurrentWindow(true)

                myGutterIconRenderer = gutterIconRenderer
                myUseSelection = useSelection

                doShow()
            }
        })
    }

    public fun hide() {
        AppUIUtil.invokeOnEdt(object : Runnable {
            override fun run() {
                updateRequested.set(false)

                removeHighlighter()
                myOpenFileDescriptor = null
                myEditor = null
                myGutterIconRenderer = null
            }
        })
    }

    private fun doShow() {
        ApplicationManager.getApplication()!!.assertIsDispatchThread()
        removeHighlighter()

        myEditor = if (myOpenFileDescriptor == null) null else XDebuggerUtilImpl.createEditor(myOpenFileDescriptor!!)
        if (myEditor != null) {
            addHighlighter()
        }
    }

    private fun removeHighlighter() {
        if (myRangeHighlighter == null || myEditor == null) {
            return
        }

        if (myUseSelection) {
            myEditor!!.getSelectionModel().removeSelection()
        }

        myRangeHighlighter!!.dispose()
        myRangeHighlighter = null
    }

    private fun addHighlighter() {
        if (filePosition != null) {
            val document = myEditor!!.getDocument()
            val startLineOffset = document.getLineStartOffset(filePosition!!.normalizedStartLine)
            val endLineOffset = document.getLineStartOffset(filePosition!!.normalizedEndLine)
            val startOffset = startLineOffset + filePosition!!.normalizedStartSymbol - 1
            val endOffset = endLineOffset + filePosition!!.normalizedEndSymbol - 1
            if (myUseSelection) {
                myEditor!!.getSelectionModel().setSelection(startOffset, endOffset)
                return
            }

            if (myRangeHighlighter != null) return

            val scheme = EditorColorsManager.getInstance()!!.getGlobalScheme()
            myRangeHighlighter = myEditor!!.getMarkupModel().addRangeHighlighter(
                                                        startOffset,
                                                        endOffset,
                                                        DebuggerColors.EXECUTION_LINE_HIGHLIGHTERLAYER,
                                                        scheme.getAttributes(DebuggerColors.EXECUTIONPOINT_ATTRIBUTES),
                                                        HighlighterTargetArea.EXACT_RANGE)
            myRangeHighlighter!!.putUserData(EXECUTION_POINT_HIGHLIGHTER_KEY!!, true)
            myRangeHighlighter!!.setGutterIconRenderer(myGutterIconRenderer)
        }
    }
}
