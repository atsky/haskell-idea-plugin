package org.jetbrains.grammar.bigTest

import java.io.File
import java.io.FilenameFilter
import org.jetbrains.grammar.parseFile

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
import java.io.FileInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.InputStream
import java.io.ByteArrayOutputStream
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream
import org.jetbrains.haskell.parser.lexer.HaskellLexer
import org.jetbrains.haskell.parser.getCachedTokens
import org.jetbrains.grammar.HaskellParser
import org.jetbrains.grammar.dumb.LazyLLParser
import java.io.FileWriter
import java.io.FileOutputStream

/**
 * Created by atsky on 12/12/14.
 */
fun main(args : Array<String>) {

    val map = TreeMap<String, MutableList<String>>()

    for (line in readLines(File("/home/atsky/.cabal/packages/hackage.haskell.org/00-index.cache"))) {
        val strings = line.split(' ')
        if (strings[0] == "pkg:") {
            val key = strings[1]
            val value = strings[2]

            map.getOrPut(key) { ArrayList<String>() }.add(value)
        }
    }
    for ((pkg, versions) in map) {
        versions.sort()
        val name = pkg + "-" + versions.last
        println(name)
        val tmp = File("hackage-cache")
        tmp.mkdirs()
        val file = File(tmp, name + ".tar.gz")
        if (!file.exists()) {
            val url = "http://hackage.haskell.org/package/${name}/${name}.tar.gz"
            println(url)
            val byteArray = fetchUrl(url)
            val stream = FileOutputStream(file)
            stream.write(byteArray)
            stream.close()
        }

        val byteArray = file.readBytes()
        val result = listHaskellFiles(name, ByteInputStream(byteArray, byteArray.size))
        if (!result) {
            break
        }
    }
}

fun listHaskellFiles(packageName : String, stream : InputStream) : Boolean {
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
             content: kotlin.ByteArray) : Boolean {
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
            println(String(content))
            return false;
        }
    } catch (e : Exception) {
        println(packageName + " - " + name + " - exception")
        println(String(content))
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

fun fetchUrl(url : String): ByteArray? {
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
    } catch (e : HttpException) {
        System.err.println("Fatal protocol violation: " + e.getMessage());
        e.printStackTrace();
    } catch (e : IOException) {
        System.err.println("Fatal transport error: " + e.getMessage());
        e.printStackTrace();
    } finally {
        // Release the connection.
        method.releaseConnection();
    }
    return null
}