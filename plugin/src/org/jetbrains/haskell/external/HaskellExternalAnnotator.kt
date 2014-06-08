package org.jetbrains.haskell.external

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.annotations.Nullable
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.module.ModuleUtilCore
import org.json.simple.JSONArray
import java.io.File
import org.jetbrains.haskell.util.copyFile
import org.json.simple.JSONObject
import org.jetbrains.haskell.util.LineColPosition
import com.intellij.openapi.vfs.LocalFileSystem
import java.util.HashSet
import sun.nio.cs.StandardCharsets
import java.io.ByteArrayInputStream
import org.jetbrains.haskell.util.getRelativePath

public class HaskellExternalAnnotator() : ExternalAnnotator<PsiFile, List<ErrorMessage>>() {

    override fun collectInformation(file: PsiFile): PsiFile {
        return file
    }

    fun copyContent(basePath: VirtualFile, destination: File) {
        if (!destination.exists()) {
            destination.mkdir()
        }
        val localFileSystem = LocalFileSystem.getInstance()!!

        val destinationFiles = HashSet(destination.list()!!.toList())

        for (child in basePath.getChildren()!!) {
            destinationFiles.remove(child.getName())
            if (child.getName().equals(".idea")) {
                continue
            }
            if (child.getName().equals("dist")) {
                continue
            }
            if (child.getName().equals(".buildwrapper")) {
                continue
            }
            val destinationFile = File(destination, child.getName())
            if (child.isDirectory()) {
                copyContent(child, destinationFile)
            } else {
                val childTime = child.getModificationStamp()
                val document = FileDocumentManager.getInstance()!!.getCachedDocument(child)
                if (document != null) {
                    val stream = ByteArrayInputStream(document.getText().getBytes(child.getCharset()!!));
                    copyFile(stream, destinationFile)
                } else {
                    val destinationTime = localFileSystem.findFileByIoFile(destinationFile)?.getModificationStamp()
                    if (destinationTime == null || childTime > destinationTime) {
                        copyFile(child.getInputStream()!!, destinationFile)
                    }
                }
            }
        }
        for (file in destinationFiles) {
            if (file.endsWith(".hs")) {
                File(destination, file).delete()
            }
        }
    }

    override fun doAnnotate(psiFile: PsiFile?): List<ErrorMessage> {
        val file = psiFile!!.getVirtualFile()
        if (file == null) {
            return listOf()
        }

        val moduleContent = BuildWrapper.getModuleContentDir(psiFile)

        if (moduleContent == null) {
            return listOf()
        }

        copyContent(moduleContent, File(moduleContent.getCanonicalPath()!!, ".buildwrapper"))

        val out = BuildWrapper.init(psiFile).build1(file)
        if (out != null) {
            val errors = out.get(1) as JSONArray

            return errors.map {
                ErrorMessage.fromJson(it!!)
            }
        }
        return listOf()
    }


    override fun apply(file: PsiFile, annotationResult: List<ErrorMessage>?, holder: AnnotationHolder) {
        val moduleContent = BuildWrapper.getModuleContentDir(file)
        if (moduleContent == null) {
            return
        }
        val relativePath = getRelativePath(moduleContent.getPath(), file.getVirtualFile()!!.getPath())

        for (error in annotationResult!!) {
            if (relativePath == error.file) {
                var start = LineColPosition(error.line, error.column).getOffset(file)
                val end = LineColPosition(error.eLine, error.eColumn).getOffset(file)
                if (start == end) {
                    start = start - 1
                }
                when (error.severity) {
                    ErrorMessage.Severity.Error -> holder.createErrorAnnotation(TextRange(start, end), error.text);
                    ErrorMessage.Severity.Warning -> holder.createWarningAnnotation(TextRange(start, end), error.text);
                }
            }
        }
    }
}
