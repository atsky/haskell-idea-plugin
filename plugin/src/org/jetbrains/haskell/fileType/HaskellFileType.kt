package org.jetbrains.haskell.fileType

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NonNls
import org.jetbrains.haskell.HaskellLanguage
import org.jetbrains.haskell.icons.HaskellIcons
import javax.swing.*

class HaskellFileType : LanguageFileType(HaskellLanguage.INSTANCE) {

    private var myIcon: Icon = HaskellIcons.DEFAULT

    override fun getName(): String =
            "Haskell file"

    override fun getDescription(): String =
            "Haskell file"

    override fun getDefaultExtension(): String =
            DEFAULT_EXTENSION

    override fun getIcon(): Icon =
            myIcon

    override fun getCharset(file: VirtualFile, content: ByteArray): String =
            "UTF-8"

    companion object {
        @JvmField val INSTANCE: HaskellFileType = HaskellFileType()
        val DEFAULT_EXTENSION: String = "hs"
    }
}
