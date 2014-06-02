package org.jetbrains.haskell.scope

import java.util.HashMap
import org.jetbrains.cabal.CabalInterface
import com.intellij.psi.PsiManager
import org.jetbrains.cabal.CabalFile
import org.jetbrains.haskell.vfs.TarGzFile
import com.intellij.openapi.vfs.LocalFileSystem
import org.jetbrains.haskell.fileType.HaskellFile
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import org.jetbrains.haskell.util.joinPath
import com.intellij.openapi.application.PathManager
import java.net.URL
import java.io.FileOutputStream
import java.io.IOException
import com.intellij.psi.PsiElement
import org.jetbrains.haskell.vfs.TarGzVirtualFileSystem
import java.util.ArrayList

/**
 * Created by atsky on 01/06/14.
 */
public class HackageScope {
    class object {
        val INSTANCE = HackageScope()
    }

    val cache = HashMap<String, List<String>>()
    var packagesList : List<Pair<String, String>>? = null

    public fun getModule(element: PsiElement, nameToFind: String): HaskellFile? {
        if (packagesList == null) {
            val virtualFile = CabalInterface.findCabal(element.getContainingFile()!!)!!
            val list = (PsiManager.getInstance(element.getProject()).findFile(virtualFile) as CabalFile).getPackagesList()

            packagesList = (list.filter { it.first != "haskell2010" }).toArrayList()
        }

        for (pkg in packagesList!!) {
            val fullName = pkg.first + "-" + pkg.second
            if (cache[fullName] == null) {
                val file = getHackageFile(pkg)
                if (file.exists()) {
                    val tarGzFile = TarGzFile(LocalFileSystem.getInstance()!!.findFileByIoFile(file)!!, fullName + "/")
                    val filesList = ArrayList<String>()
                    listHaskellFiles(tarGzFile, nameToFind, filesList)
                    cache[fullName] = filesList
                }
            }

            val listSources = cache[fullName]

            if (listSources == null) {
                continue
            }

            for (path in listSources) {
                val fileName = path.substring(0, path.length - 3).replaceAll("/", ".")
                if (fileName.endsWith(nameToFind)) {
                    val moduleFile = TarGzVirtualFileSystem.INSTANCE.findFileByPath(path)!!
                    val psiFile = PsiManager.getInstance(element.getProject()).findFile(moduleFile)
                    System.out.println(moduleFile)
                    return psiFile as? HaskellFile
                }
            }

        }
        return null
    }

    fun listHaskellFiles(file: VirtualFile, name: String, result: ArrayList<String>) {
        if (file.getName().endsWith(".hs")) {
            result.add(file.getPath())
        } else {
            file.isDirectory()
            for (child in file.getChildren()!!.toArrayList()) {
                listHaskellFiles(child, name, result)
            }
        }

    }

    fun getHackageFile(pkg: Pair<String, String>): File {
        val fullName = pkg.first + "-" + pkg.second
        val cabalPath = joinPath(PathManager.getSystemPath(), "cabal")
        if (!File(cabalPath).exists()) {
            File(cabalPath).mkdir()
        }
        val file = File(joinPath(cabalPath, fullName + ".tar.gz"))
        if (!file.exists()) {
            try {
                val byteArray = URL("http://hackage.haskell.org/package/${fullName}/${fullName}.tar.gz").readBytes()
                val stream = FileOutputStream(file)
                stream.write(byteArray)
                stream.close()
            } catch (e: IOException) {

            }
        }
        return file
    }

}