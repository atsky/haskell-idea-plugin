package org.jetbrains.haskell.external.ghcfs

import com.intellij.openapi.vfs.newvfs.NewVirtualFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.util.io.FileAttributes
import java.io.OutputStream
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem

/**
 * Created by atsky on 09/05/14.
 */
public class RamVirtualFileSystem() : NewVirtualFileSystem() {

    fun getTarGzRootForLocalFile(entryVFile : VirtualFile) : VirtualFile {

        throw UnsupportedOperationException()
    }



    override fun exists(file: VirtualFile): Boolean {
        return file.exists()
    }

    override fun list(file: VirtualFile): Array<String> {
        throw UnsupportedOperationException()
    }

    override fun isDirectory(file: VirtualFile) = file.isDirectory()

    override fun getTimeStamp(file: VirtualFile): Long = file.getTimeStamp()

    override fun setTimeStamp(file: VirtualFile, timeStamp: Long) {
        throw UnsupportedOperationException()
    }

    override fun isWritable(file: VirtualFile) = false

    override fun setWritable(file: VirtualFile, writableFlag: Boolean) {
        throw UnsupportedOperationException()
    }

    override fun contentsToByteArray(file: VirtualFile): ByteArray {
        val stream = file.getInputStream()!!
        return FileUtil.loadBytes(stream, file.getLength().toInt())
    }

    override fun getInputStream(file: VirtualFile) = file.getInputStream()!!

    override fun getOutputStream(file: VirtualFile,
                                 requestor: Any?,
                                 modStamp: Long,
                                 timeStamp: Long): OutputStream {
        throw UnsupportedOperationException()
    }

    override fun getLength(file: VirtualFile) = file.getLength()

    override fun getProtocol(): String = "ghci"

    override fun findFileByPath(path: String): VirtualFile? {
        throw UnsupportedOperationException()
    }

    override fun refresh(asynchronous: Boolean) {
        throw UnsupportedOperationException()
    }

    override fun refreshAndFindFileByPath(path: String): VirtualFile? {
        throw UnsupportedOperationException()
    }

    override fun findFileByPathIfCached(path: String): VirtualFile? {
        return null
    }

    override fun extractRootPath(path: String): String {
        throw UnsupportedOperationException()
    }

    override fun getRank(): Int = 2

    override fun copyFile(requestor: Any?,
                          file: VirtualFile,
                          newParent: VirtualFile,
                          copyName: String): VirtualFile {
        throw UnsupportedOperationException()
    }

    override fun createChildDirectory(requestor: Any?, parent: VirtualFile, dir: String): VirtualFile {
        throw UnsupportedOperationException()
    }

    override fun createChildFile(requestor: Any?, parent: VirtualFile, file: String): VirtualFile {
        throw UnsupportedOperationException()
    }

    override fun deleteFile(requestor: Any?, file: VirtualFile) {
        throw UnsupportedOperationException()
    }

    override fun moveFile(requestor: Any?, file: VirtualFile, newParent: VirtualFile) {
        throw UnsupportedOperationException()
    }

    override fun renameFile(requestor: Any?, file: VirtualFile, newName: String) {
        throw UnsupportedOperationException()
    }

    override fun getAttributes(file: VirtualFile): FileAttributes? {
        throw UnsupportedOperationException()
    }

    companion object {
        public val INSTANCE : RamVirtualFileSystem = RamVirtualFileSystem()
    }
}
