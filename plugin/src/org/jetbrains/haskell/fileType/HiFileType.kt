package org.jetbrains.haskell.fileType

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.*

public class HiFileType() : FileType {


    override fun getName(): String {
        return "Haskell interface"
    }

    override fun getDescription(): String {
        return "Haskell interface file"
    }

    override fun getDefaultExtension(): String {
        return "hi"
    }

    override fun getIcon(): Icon? {
        return null
    }

    override fun isBinary(): Boolean {
        return true
    }

    override fun isReadOnly(): Boolean {
        return true
    }

    override fun getCharset(file: VirtualFile, content: ByteArray?): String? {
        return null
    }


    class object {
        public val INSTANCE: HiFileType = HiFileType()
    }
}