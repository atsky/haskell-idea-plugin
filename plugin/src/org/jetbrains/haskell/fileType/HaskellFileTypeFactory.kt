package org.jetbrains.haskell.fileType

import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory
import org.jetbrains.cabal.CabalFileType
import org.jetbrains.yesod.hamlet.HamletFileType
import org.jetbrains.yesod.julius.JuliusFileType
import org.jetbrains.yesod.cassius.CassiusFileType
import org.jetbrains.yesod.lucius.LuciusFileType


public class HaskellFileTypeFactory() : FileTypeFactory() {
    override fun createFileTypes(consumer: FileTypeConsumer) {
        consumer.consume(HaskellFileType.INSTANCE, HaskellFileType.DEFAULT_EXTENSION)
        consumer.consume(CabalFileType.INSTANCE, CabalFileType.DEFAULT_EXTENSION)
        consumer.consume(HamletFileType.INSTANCE, HamletFileType.DEFAULT_EXTENSION)
        consumer.consume(JuliusFileType.INSTANCE, JuliusFileType.DEFAULT_EXTENSION)
        consumer.consume(LuciusFileType.INSTANCE, LuciusFileType.DEFAULT_EXTENSION)
        consumer.consume(CassiusFileType.INSTANCE, CassiusFileType.DEFAULT_EXTENSION)
    }
}