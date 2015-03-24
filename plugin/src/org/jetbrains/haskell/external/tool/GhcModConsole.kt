package org.jetbrains.haskell.external.tool

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.components.ProjectComponent
import com.intellij.execution.impl.ConsoleViewUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.notification.NotificationType
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.editor.colors.TextAttributesKey

/**
 * Created by atsky on 06/01/15.
 */
public class GhcModConsole(val project: Project) : ProjectComponent {
    var editor : EditorEx? = null

    override fun getComponentName(): String = "GhcModConsole"

    override fun initComponent() {
    }

    override fun disposeComponent() {
    }

    override fun projectOpened() {
        editor = ConsoleViewUtil.setupConsoleEditor(project, false, false);
    }

    override fun projectClosed() {
        val editorFactory = EditorFactory.getInstance()
        editorFactory.releaseEditor(editor)
    }

    companion object {
        fun getInstance(project : Project) =
            project.getComponent(javaClass<GhcModConsole>())

    }

    fun append(text: String, type : MessageType) {
        ApplicationManager.getApplication().invokeLater({
            val document = editor!!.getDocument()

            val msgStart = document.getTextLength()
            document.insertString(document.getTextLength(), text)
            val layer = HighlighterLayer.CARET_ROW + 1

            val attributes = EditorColorsManager.getInstance()
                    .getGlobalScheme().getAttributes(type.key);

            editor?.getMarkupModel()?.addRangeHighlighter(
                    msgStart,
                    document.getTextLength(),
                    layer,
                    attributes,
                    HighlighterTargetArea.EXACT_RANGE)
        });
    }

    enum class MessageType(val key : TextAttributesKey) {
        INFO : MessageType(ConsoleViewContentType.SYSTEM_OUTPUT_KEY)
        INPUT : MessageType(ConsoleViewContentType.USER_INPUT_KEY)
        OUTPUT : MessageType(ConsoleViewContentType.NORMAL_OUTPUT_KEY)
    }
}