package org.jetbrains.haskell.util

import java.io.File
import java.io.BufferedReader
import java.io.Reader
import java.io.FileReader

public fun joinPath(first : String, vararg more : String) : String {
    var result = first
    for (str in more) {
        result += File.separator + str
    }
    return result
}


fun fileToIterable(file : File) : Iterable<String> {
    return object : Iterable<String> {
        override fun iterator(): Iterator<String> {
            val br = BufferedReader(FileReader(file));

            return object : Iterator<String> {
                var reader : BufferedReader? = br
                var line : String? = null;

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