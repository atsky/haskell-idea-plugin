package org.jetbrains.haskell.external.ghcfs

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import java.io.InputStream
import java.io.ByteArrayInputStream
import java.io.OutputStream
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiFile

/**
 * Created by atsky on 11/12/14.
 */
class RamFile(val fileName: String, val content : String) : VirtualFile() {

    override fun getName(): String {
        return fileName
    }

    override fun getFileSystem(): VirtualFileSystem = RamVirtualFileSystem.INSTANCE

    override fun getPath(): String = fileName + ".hs"

    override fun isWritable() = false

    override fun isDirectory() = false

    override fun isValid() = true

    override fun getParent(): VirtualFile? {
        return null
    }

    override fun getChildren(): Array<VirtualFile>? {
        return arrayOf()
    }

    override fun getOutputStream(requestor: Any?, newModificationStamp: Long, newTimeStamp: Long): OutputStream {
        throw UnsupportedOperationException()
    }

    override fun contentsToByteArray(): ByteArray {
        return content.toByteArray()
    }

    override fun getTimeStamp(): Long {
        return 0
    }

    override fun getModificationStamp(): Long {
        return 0
    }

    override fun getLength(): Long {
        return content.length.toLong()
    }

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?) {
        throw UnsupportedOperationException()
    }

    override fun getInputStream(): InputStream? {
        return ByteArrayInputStream(content.toByteArray())
    }


    fun getPsiFile(project : Project) : PsiFile? =
        PsiManager.getInstance(project).findFile(this)


}