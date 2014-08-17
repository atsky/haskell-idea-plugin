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
import com.intellij.openapi.application.ModalityState
import java.util.regex.Pattern
import java.util.ArrayList
import org.jetbrains.haskell.config.HaskellSettings

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

    fun getResultFromGhcModi(psiFile: PsiFile,
                             baseDir: VirtualFile,
                             file: VirtualFile): List<ErrorMessage> {
        ApplicationManager.getApplication()!!.invokeAndWait(object : Runnable {
            override fun run() {
                FileDocumentManager.getInstance()!!.saveAllDocuments()
            }
        }, ModalityState.any())


        val ghcModi = psiFile.getProject().getComponent(javaClass<GhcModi>())!!

        val relativePath = getRelativePath(baseDir.getPath(), file.getPath())

        val result = ghcModi.runCommand("check $relativePath")

        val errors = ArrayList<ErrorMessage>()

        for (resultLine in result) {
            val matcher = Pattern.compile("(.*):(\\d*):(\\d*):(.*)").matcher(resultLine)
            if (matcher.find()) {
                val path = matcher.group(1)!!
                val line = Integer.parseInt(matcher.group(2)!!)
                val col = Integer.parseInt(matcher.group(3)!!)
                val msg = matcher.group(4)!!.replace("\u0000", "\n")
                val severity = if (msg.startsWith("Warning")) {
                    ErrorMessage.Severity.Warning
                } else {
                    ErrorMessage.Severity.Error
                }
                if (relativePath == path) {
                    errors.add(ErrorMessage(msg, path, severity, line, col, line, col))
                }
            }
        }

        return errors
    }

    fun getResultFromBuidWrapper(psiFile: PsiFile,
                                 moduleContent: VirtualFile,
                                 file: VirtualFile): List<ErrorMessage> {
        ApplicationManager.getApplication()!!.invokeAndWait(object : Runnable {
            override fun run() {
                FileDocumentManager.getInstance()!!.saveAllDocuments()
            }
        }, ModalityState.any())

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

    fun getProjectBaseDir(psiFile: PsiFile): VirtualFile? {
        return psiFile.getProject().getBaseDir()
    }

    override fun doAnnotate(psiFile: PsiFile?): List<ErrorMessage> {
        val file = psiFile!!.getVirtualFile()
        if (file == null) {
            return listOf()
        }

        val baseDir = getProjectBaseDir(psiFile)

        if (baseDir == null) {
            return listOf()
        }

        if (!(HaskellSettings.getInstance().getState().useGhcMod!!)) {
            return listOf();
        }
        if (!file.isInLocalFileSystem()) {
            return listOf()
        }
        return getResultFromGhcModi(psiFile, baseDir, file)
    }


    override fun apply(file: PsiFile, annotationResult: List<ErrorMessage>?, holder: AnnotationHolder) {
        for (error in annotationResult!!) {

            val start = LineColPosition(error.line, error.column).getOffset(file)
            val end = LineColPosition(error.eLine, error.eColumn).getOffset(file)

            val element = file.findElementAt(start)
            if (element != null) {
                when (error.severity) {
                    ErrorMessage.Severity.Error -> holder.createErrorAnnotation(element, error.text);
                    ErrorMessage.Severity.Warning -> holder.createWarningAnnotation(element, error.text);
                }
            } else {
                when (error.severity) {
                    ErrorMessage.Severity.Error -> holder.createErrorAnnotation(TextRange(start, end), error.text);
                    ErrorMessage.Severity.Warning -> holder.createWarningAnnotation(TextRange(start, end), error.text);
                }
            }
        }
    }
}
