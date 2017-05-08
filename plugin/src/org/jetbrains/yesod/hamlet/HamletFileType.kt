package org.jetbrains.yesod.hamlet

/**
 * @author Leyla H
 */
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NonNls
import org.jetbrains.haskell.icons.HaskellIcons

import javax.swing.*

class HamletFileType private constructor() : LanguageFileType(HamletLanguage.INSTANCE) {

    private val myIcon: Icon

    init {
        myIcon = HaskellIcons.HAMLET
    }

    override fun getName(): String {
        return "Hamlet file"
    }

    override fun getDescription(): String {
        return "Hamlet file"
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
        val INSTANCE: HamletFileType = HamletFileType()
        val DEFAULT_EXTENSION: String = "hamlet"
    }
}