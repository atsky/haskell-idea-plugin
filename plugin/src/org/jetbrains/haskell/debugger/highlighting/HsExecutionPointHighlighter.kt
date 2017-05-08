package org.jetbrains.haskell.debugger.highlighting

import com.intellij.openapi.project.Project
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.Editor
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
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Color
import com.intellij.openapi.editor.markup.HighlighterLayer

/**
 * Modified copy of com.intellij.xdebugger.impl.ui.ExecutionPointHighlighter. Differences:
 * 1) Simplified (not all methods are implemented)
 * 2) Highlights code ranges, not just lines
 *
 * @author Habibullin Marat
 */
class HsExecutionPointHighlighter(private val myProject: Project,
                                         private val highlighterType: HsExecutionPointHighlighter.HighlighterType
                                         = HsExecutionPointHighlighter.HighlighterType.STACK_FRAME) {

    enum class HighlighterType {
        STACK_FRAME,
        HISTORY
    }

    private var myRangeHighlighter: RangeHighlighter? = null
    private var myEditor: Editor? = null
    private var filePosition: HsFilePosition? = null
    private var myOpenFileDescriptor: OpenFileDescriptor? = null
    private var myUseSelection: Boolean = false
    private var myGutterIconRenderer: GutterIconRenderer? = null
    private val EXECUTION_POINT_HIGHLIGHTER_KEY: Key<Boolean>? = Key.create("EXECUTION_POINT_HIGHLIGHTER_KEY")

    private val updateRequested = AtomicBoolean()

    fun show(stackFrame: HsStackFrame, useSelection: Boolean, gutterIconRenderer: GutterIconRenderer?) {
        updateRequested.set(false)
        if (stackFrame.hackSourcePosition == null) {
            hide()
            return
        }
        AppUIUtil.invokeLaterIfProjectAlive(myProject, object : Runnable {
            override fun run() {
                updateRequested.set(false)

                filePosition = stackFrame.stackFrameInfo.filePosition

                myOpenFileDescriptor = XSourcePositionImpl.createOpenFileDescriptor(myProject, stackFrame.hackSourcePosition!!)
                myOpenFileDescriptor!!.isUseCurrentWindow = true

                myGutterIconRenderer = gutterIconRenderer
                myUseSelection = useSelection

                doShow()
            }
        })
    }

    fun hide() {
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
            myEditor!!.selectionModel.removeSelection()
        }

        myRangeHighlighter!!.dispose()
        myRangeHighlighter = null
    }

    private fun addHighlighter() {
        if (filePosition != null) {
            val document = myEditor!!.document
            val startLineOffset = document.getLineStartOffset(filePosition!!.normalizedStartLine)
            val endLineOffset = document.getLineStartOffset(filePosition!!.normalizedEndLine)
            val startOffset = startLineOffset + filePosition!!.normalizedStartSymbol - 1
            val endOffset = endLineOffset + filePosition!!.normalizedEndSymbol - 1
            if (myUseSelection) {
                myEditor!!.selectionModel.setSelection(startOffset, endOffset)
                return
            }

            if (myRangeHighlighter != null) return

            myRangeHighlighter = myEditor!!.markupModel.addRangeHighlighter(
                    startOffset,
                    endOffset,
                    getHighlightLayer(),
                    getTextAttributes(),
                    HighlighterTargetArea.EXACT_RANGE)
            myRangeHighlighter!!.putUserData(EXECUTION_POINT_HIGHLIGHTER_KEY!!, true)
            myRangeHighlighter!!.gutterIconRenderer = myGutterIconRenderer
        }
    }

    /**
     * Returns needed color scheme, based on current highlighter type and on the current global scheme,
     * so that highlighters with different types have different colors.
     */
    private fun getTextAttributes(): TextAttributes? {
        when (highlighterType) {
            HsExecutionPointHighlighter.HighlighterType.STACK_FRAME ->
                return EditorColorsManager.getInstance()!!.globalScheme.getAttributes(DebuggerColors.EXECUTIONPOINT_ATTRIBUTES)
            HsExecutionPointHighlighter.HighlighterType.HISTORY -> {
                val scheme = EditorColorsManager.getInstance()!!.globalScheme
                val attr1 = scheme.getAttributes(DebuggerColors.EXECUTIONPOINT_ATTRIBUTES)
                val attr2 = scheme.getAttributes(DebuggerColors.BREAKPOINT_ATTRIBUTES)
                if (attr1 == null) {
                    return null
                }
                return TextAttributes(mix(attr1.foregroundColor, attr2?.foregroundColor),
                        mix(attr1.backgroundColor, attr2?.backgroundColor),
                        mix(attr1.effectColor, attr2?.effectColor),
                        attr1.effectType,
                        attr1.fontType)
            }
            else -> return null
        }
    }

    private fun getHighlightLayer(): Int {
        when (highlighterType) {
            HsExecutionPointHighlighter.HighlighterType.STACK_FRAME ->
                return DebuggerColors.EXECUTION_LINE_HIGHLIGHTERLAYER
            HsExecutionPointHighlighter.HighlighterType.HISTORY ->
                return DebuggerColors.EXECUTION_LINE_HIGHLIGHTERLAYER - 1
            else ->
                return HighlighterLayer.SELECTION
        }
    }

    private fun mix(a: Color?, b: Color?): Color? {
        if (a == null) {
            return b
        }
        if (b == null) {
            return a
        }
        return Color((a.red + b.red) / 2, (a.green + b.green) / 2, (a.blue + b.blue) / 2,
                (a.alpha + b.alpha) / 2)
    }
}
