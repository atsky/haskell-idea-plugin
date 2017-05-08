package org.jetbrains.haskell.vfs

import java.io.File
import java.io.BufferedInputStream
import java.io.InputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.FileInputStream
import java.util.ArrayList

/**
 * Created by atsky on 12/12/14.
 */
class TarGzArchive(val file : File) {
    val filesList : List<String>

    init {
        val bin = BufferedInputStream(FileInputStream(file))
        val gzIn = GzipCompressorInputStream(bin)


        val tarArchiveInputStream = TarArchiveInputStream(gzIn)

        var file = ArrayList<String>()

        while (true) {
            val entry = tarArchiveInputStream.nextTarEntry

            if (entry == null) {
                break
            }

            file.add(entry.name)
        }
        filesList = file
        bin.close()
    }
}