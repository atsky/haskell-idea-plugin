package org.jetbrains.grammar.bigTest

import java.io.File

import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.params.HttpMethodParams
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler
import org.apache.commons.httpclient.HttpStatus
import java.io.IOException
import org.apache.commons.httpclient.HttpException
import org.jetbrains.haskell.util.readLines
import java.util.ArrayList
import java.util.TreeMap
import java.io.BufferedInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.InputStream
import java.io.ByteArrayOutputStream
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream
import org.jetbrains.haskell.parser.lexer.HaskellLexer
import org.jetbrains.haskell.parser.getCachedTokens
import org.jetbrains.grammar.HaskellParser
import org.jetbrains.grammar.dumb.LazyLLParser
import java.io.FileOutputStream

/**
 * Created by atsky on 12/12/14.
 */
class BigParserTest {
    val MAX_PACKAGES = 200
    var packageProblems = 0
    var totalFilesChecked = 0
    val failOnError = false

    val exclude = setOf("CHXHtml-0.2.0")


    fun listHaskellFiles(packageName: String, stream: InputStream): Boolean {
        val bin = BufferedInputStream(stream)
        val gzIn = GzipCompressorInputStream(bin);

        val tarArchiveInputStream = TarArchiveInputStream(gzIn)

        while (true) {
            val entry = tarArchiveInputStream.getNextTarEntry();

            if (entry == null) {
                break
            }

            val name = entry.getName()
            if (name.endsWith(".hs")) {
                val content = readToArray(tarArchiveInputStream)
                if (!testFile(packageName, name, content)) {
                    return false;
                }
            }
        }

        bin.close()
        return true
    }

    fun testFile(packageName: kotlin.String,
                 name: kotlin.String?,
                 content: kotlin.ByteArray): Boolean {
        totalFilesChecked++
        val lexer = HaskellLexer()
        lexer.start(String(content))

        val cachedTokens = getCachedTokens(lexer, null)
        val grammar = HaskellParser(null).getGrammar()
        HaskellParser(null).findFirst(grammar)

        try {
            val parser = LazyLLParser(grammar, cachedTokens)
            val tree = parser.parse()
            if (tree == null) {
                println(packageName + " - " + name)
                if (failOnError) {
                    println(String(content))
                }
                return false;
            }
        } catch (e: Exception) {
            println(packageName + " - " + name + " - exception")
            if (failOnError) {
                println(String(content))
            }
            return false;
        }
        return true;
    }

    fun readToArray(ins: InputStream): ByteArray {
        val buffer = ByteArrayOutputStream()

        var nRead: Int
        val data = ByteArray(16384)

        while (true) {
            nRead = ins.read(data, 0, data.size)
            if (nRead == -1) {
                break;
            }
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

    fun fetchUrl(url: String): ByteArray? {
        val client = HttpClient();


        val method = GetMethod(url);

        // Provide custom retry handler is necessary

        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                DefaultHttpMethodRetryHandler(3, false));

        try {
            val statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
            }
            // Read the response body.
            return method.getResponseBody()
        } catch (e: HttpException) {
            System.err.println("Fatal protocol violation: " + e.message);
            e.printStackTrace();
        } catch (e: IOException) {
            System.err.println("Fatal transport error: " + e.message);
            e.printStackTrace();
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
        return null
    }

    fun doTest(packagesFile: File) {
        val map = TreeMap<String, MutableList<String>>()

        for (line in readLines(packagesFile)) {
            val strings = line.split(' ')
            if (strings[0] == "pkg:") {
                val key = strings[1]
                val value = strings[2]

                map.getOrPut(key) { ArrayList<String>() }.add(value)
                if (map.size >= MAX_PACKAGES) {
                    break;
                }
            }
        }
        for ((pkg, versions) in map) {
            val sortedVersions = versions.sorted()
            val name = pkg + "-" + sortedVersions.lastOrNull()

            if (exclude.contains(name)) {
                println(name + " - exclude!")
                continue
            } else {
                println(name)
            }
            val tmp = File("hackage-cache")
            tmp.mkdirs()
            val file = File(tmp, name + ".tar.gz")
            if (!file.exists()) {
                downloadPackage(file, name)
            }

            val byteArray = file.readBytes()
            val result = listHaskellFiles(name, ByteInputStream(byteArray, byteArray.size))
            if (!result) {
                packageProblems++;
                if (failOnError) {
                    break
                }
            }
        }
    }

    private fun downloadPackage(file: File, name: String) {
        val url = "http://hackage.haskell.org/package/${name}/${name}.tar.gz"
        println("Dowloading: " + url)
        val byteArray = fetchUrl(url)
        val stream = FileOutputStream(file)
        stream.write(byteArray)
        stream.close()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val test = BigParserTest()
            test.doTest(File("/Users/atsky/Library/Haskell/repo-cache/hackage.haskell.org/00-index.cache"))
            println("Total package problems: ${test.packageProblems}")
            println("Total files checked: ${test.totalFilesChecked}")
        }
    }
}