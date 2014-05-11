package org.jetbrains.haskell.vfs

import java.io.FileInputStream
import java.io.BufferedInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.archivers.ar.ArArchiveEntry

/**
 * Created by atsky on 10/05/14.
 */
fun main(args : Array<String>) {
    val fin = FileInputStream("/Users/atsky/Library/Haskell/repo-cache/hackage.haskell.org/cpphs/1.18.4/cpphs-1.18.4.tar.gz");
    val bin = BufferedInputStream(fin);
    val gzIn = GzipCompressorInputStream(bin);

    val tarArchiveInputStream = TarArchiveInputStream(gzIn)

    while (true) {
        val entry = tarArchiveInputStream.getNextEntry();
        if (entry == null) {
            break
        }
        val byteArray = tarArchiveInputStream.readBytes(entry.getSize().toInt())
        System.out.println(entry.getName())
    }

    gzIn.close();

}