package org.jetbrains.haskell.util

import java.io.File
import java.io.BufferedReader
import java.io.Reader
import java.io.FileReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

public fun joinPath(first: String, vararg more: String): String {
    var result = first
    for (str in more) {
        result += File.separator + str
    }
    return result
}


public fun deleteRecursive(path: File) {
    val files = path.listFiles();
    if (files != null) {
        for (file in files) {
            if (file.isDirectory()) {
                deleteRecursive(file);
                file.delete();
            } else {
                file.delete();
            }
        }
    }
    path.delete();
}

public fun copyFile(iStream: InputStream, destination: File) {
    val oStream = FileOutputStream(destination);
    try {
        val buffer = ByteArray(1024 * 16);
        var length: Int;
        while (true ) {
            length = iStream.read(buffer)
            if (length <= 0) {
                break
            }
            oStream.write(buffer, 0, length);
        }
    } finally {
        iStream.close();
        oStream.close();
    }
}

public fun getRelativePath(base: String, path: String): String {
    val bpath = File(base).getCanonicalPath()
    val fpath = File(path).getCanonicalPath()

    if (fpath.startsWith(bpath)) {
        return fpath.substring(bpath.length() + 1)
    } else {
        throw RuntimeException("Base path " + base + "is wrong to " + path);
    }
}

fun readLines(file: File): Iterable<String> {
    return object : Iterable<String> {
        override fun iterator(): Iterator<String> {
            val br = BufferedReader(FileReader(file));

            return object : Iterator<String> {
                var reader: BufferedReader? = br
                var line: String? = null;

                fun fetch(): String? {
                    if (line == null) {
                        line = reader?.readLine();
                    }
                    if (line == null && reader != null) {
                        reader?.close()
                        reader == null
                    }
                    return line;
                }

                override fun next(): String {
                    val result = fetch()
                    line = null;
                    return result!!
                }
                override fun hasNext(): Boolean {
                    return fetch() != null
                }

            }
        }

    }
}