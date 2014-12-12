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
public class GhciFile(val moduleName: String) : VirtualFile() {

    var content : String? = null

    override fun getName(): String {
        return moduleName + ".hs"
    }

    override fun getFileSystem(): VirtualFileSystem = GhciVirtualFileSystem.INSTANCE

    override fun getPath(): String = moduleName + ".hs"

    override fun isWritable() = false

    override fun isDirectory() = false

    override fun isValid() = true

    override fun getParent(): VirtualFile? {
        return null
    }

    override fun getChildren(): Array<VirtualFile>? {
        return array()
    }

    override fun getOutputStream(requestor: Any?, newModificationStamp: Long, newTimeStamp: Long): OutputStream {
        throw UnsupportedOperationException()
    }

    override fun contentsToByteArray(): ByteArray {
        doInit()
        return content!!.toByteArray()
    }

    override fun getTimeStamp(): Long {
        return 0
    }

    override fun getModificationStamp(): Long {
        return 0
    }

    override fun getLength(): Long {
        doInit()
        return content!!.length().toLong()
    }

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?) {
        throw UnsupportedOperationException()
    }

    override fun getInputStream(): InputStream? {
        doInit()
        return ByteArrayInputStream(content!!.toByteArray())
    }

    fun doInit() {
        content = "module ${getName()} where\n" +
                  " -- Content must be here"
    }

    fun getPsiFile(project : Project) : PsiFile? =
        PsiManager.getInstance(project).findFile(this);


}