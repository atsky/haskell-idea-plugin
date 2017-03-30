package org.jetbrains.yesod.julius

/**
 * @author Leyla H
 */
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NonNls
import org.jetbrains.haskell.icons.HaskellIcons

import javax.swing.*

public class JuliusFileType private constructor() : LanguageFileType(JuliusLanguage.INSTANCE) {

    private val myIcon: Icon

    init {
        myIcon = HaskellIcons.HAMLET
    }

    override fun getName(): String {
        return "Julius file"
    }

    override fun getDescription(): String {
        return "Julius file"
    }

    override fun getDefaultExtension(): String {
        return DEFAULT_EXTENSION
    }

    override fun getIcon(): Icon? {
        return myIcon
    }

    override fun getCharset(file: VirtualFile, content: ByteArray): String? {
        return "UTF-8"
    }

    companion object {
        public val INSTANCE: JuliusFileType = JuliusFileType()

        public val DEFAULT_EXTENSION: String = "julius"
    }
}