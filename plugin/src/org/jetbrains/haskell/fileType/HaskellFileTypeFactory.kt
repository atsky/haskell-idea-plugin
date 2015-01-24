package org.jetbrains.haskell.fileType

import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory
import org.jetbrains.cabal.CabalFileType
import org.jetbrains.yesod.hamlet.HamletFileType


public class HaskellFileTypeFactory() : FileTypeFactory() {
    override fun createFileTypes(consumer: FileTypeConsumer) {
        consumer.consume(HaskellFileType.INSTANCE, HaskellFileType.DEFAULT_EXTENSION)
        consumer.consume(CabalFileType.INSTANCE, CabalFileType.DEFAULT_EXTENSION)
        consumer.consume(HamletFileType.INSTANCE, HamletFileType.DEFAULT_EXTENSION)
    }
}