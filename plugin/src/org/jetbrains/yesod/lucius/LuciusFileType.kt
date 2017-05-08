package org.jetbrains.yesod.lucius

/**
 * @author Leyla H
 */
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NonNls
import org.jetbrains.haskell.icons.HaskellIcons
import org.jetbrains.yesod.lucius.LuciusLanguage

import javax.swing.*

class LuciusFileType private constructor() : LanguageFileType(LuciusLanguage.INSTANCE) {

    private val myIcon: Icon

    init {
        myIcon = HaskellIcons.HAMLET
    }

    override fun getName(): String {
        return "Lucius file"
    }

    override fun getDescription(): String {
        return "Lucius file"
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
        val INSTANCE: LuciusFileType = LuciusFileType()
        val DEFAULT_EXTENSION: String = "lucius"
    }
}