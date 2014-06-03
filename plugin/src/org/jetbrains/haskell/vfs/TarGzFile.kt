package org.jetbrains.haskell.vfs

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import java.io.OutputStream
import java.io.InputStream
import org.apache.commons.compress.archivers.ArchiveEntry
import java.io.FileInputStream
import java.io.BufferedInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.ByteArrayInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import java.io.ByteArrayOutputStream
import java.util.ArrayList

/**
 * Created by atsky on 09/05/14.
 */
public class TarGzFile(archiveFile: VirtualFile,
                       path: String) : VirtualFile() {

    val myArchiveFile = archiveFile
    val myPath: String = path
    var isInit = false;
    var myEntry: TarArchiveEntry? = null
    var myData: ByteArray? = null
    val myChildren = ArrayList<String>()

    fun doInit(): Boolean {
        if (isInit) {
            return true
        }
        val archiveIns = getArchiveFile().getInputStream()
        val bin = BufferedInputStream(archiveIns as InputStream)
        val gzIn = GzipCompressorInputStream(bin);


        val tarArchiveInputStream = TarArchiveInputStream(gzIn)

        while (true) {
            val entry = tarArchiveInputStream.getNextTarEntry();
            if (entry == null) {
                break
            }
            val entryName = entry.getName() ?: ""
            if (myPath == entryName) {
                myEntry = entry
                myData = readToArray(tarArchiveInputStream)
            } else if (entryName.startsWith(myPath)) {
                val name = entryName.substring(myPath.size)
                if (!name.substring(0, name.size - 1).contains("/")) {
                    myChildren.add(name)
                }
            }
        }
        gzIn.close()
        return (myData != null)
    }

    fun readToArray(ins: InputStream): ByteArray {
        val buffer = ByteArrayOutputStream()

        var nRead: Int
        val data = ByteArray(16384)

        while (true) {
            nRead = ins.read(data, 0, data.size)
            if (nRead == -1) {
                break;
            }
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

    override fun getName(): String {
        val str = if (isDirectory()) {
            myPath.substring(0, myPath.length - 1)
        } else {
            myPath
        }
        val indexOf = str.lastIndexOf('/')
        return myPath.substring(indexOf + 1)
    }

    override fun getFileSystem(): VirtualFileSystem = TarGzVirtualFileSystem.INSTANCE

    fun getArchiveFile() = myArchiveFile;

    override fun getPath(): String =
            getArchiveFile().getPath() + "!" + myPath

    override fun isWritable() = false

    override fun isDirectory() = myPath.last() == '/'

    override fun isValid() = true

    override fun getParent(): VirtualFile? {
        val str = if (isDirectory()) {
            myPath.substring(0, myPath.length - 1)
        } else {
            myPath
        }
        val indexOf = str.lastIndexOf('/')
        if (indexOf == -1) {
            return null
        }
        return TarGzFile(myArchiveFile, str.substring(0, indexOf + 1))
    }

    override fun getChildren(): Array<VirtualFile>? {
        doInit()
        val files : List<VirtualFile> = myChildren.map { TarGzFile(myArchiveFile, myPath + it) }
        return files.copyToArray()
    }

    override fun getOutputStream(requestor: Any?, newModificationStamp: Long, newTimeStamp: Long): OutputStream {
        throw UnsupportedOperationException()
    }

    override fun contentsToByteArray(): ByteArray {
        doInit()
        return myData!!;
    }

    override fun getTimeStamp(): Long {
        return myArchiveFile.getTimeStamp()
    }

    override fun getModificationStamp(): Long {
        return myArchiveFile.getModificationStamp()
    }

    override fun getLength(): Long {
        doInit()
        return myEntry!!.getSize()
    }

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?) {
        throw UnsupportedOperationException()
    }

    override fun getInputStream(): InputStream? {
        doInit()
        return ByteArrayInputStream(myData!!)
    }

}