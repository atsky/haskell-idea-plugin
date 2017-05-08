package org.jetbrains.haskell.scope

import org.jetbrains.haskell.fileType.HaskellFile
import org.jetbrains.haskell.psi.ModuleName
import org.jetbrains.haskell.util.ProcessRunner
import java.util.HashMap
import org.jetbrains.haskell.external.ghcfs.RamFile
import com.intellij.psi.PsiManager
import com.intellij.openapi.project.Project
import org.jetbrains.haskell.util.joinPath
import org.jetbrains.cabal.CabalInterface
import java.io.File
import org.jetbrains.haskell.vfs.TarGzArchive
import org.jetbrains.haskell.vfs.TarGzFile
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.LocalFileSystem

/**
 * Created by atsky on 12/9/14.
 */
object GlobalScope {
    val cache = HashMap<String, HaskellFile>()

    val tarCache = HashMap<String, TarGzArchive>()

    fun getModule(project: Project, name: String): HaskellFile? {
        if (cache.containsKey(name)) {
            return cache[name]
        }

        //val source = findSource(project, name)
        //if (source != null) {
        //    val result = PsiManager.getInstance(project).findFile(source) as HaskellFile?
        //    cache[name] = result
        //    return result;
        //}
/*
        val text = ProcessRunner(null).executeNoFail("ghc", "-e", ":browse! ${name}")
        if (text == "") {
            return null;
        }

        val content = "module ${name} where\n" +
                text
        val ramFile = RamFile(name + ".hs", content)
        val result = PsiManager.getInstance(project).findFile(ramFile) as HaskellFile?
        cache[name] = result
        return result
*/
        return null
    }

    fun findSource(project: Project, name: String): VirtualFile? {
        val cabalInterface = CabalInterface(project)
        return findSource(File(cabalInterface.getRepo()), project, name)
    }

    fun findSource(directory: File, project: Project, name: String) : VirtualFile? {
        for (file in directory.listFiles()) {
            if (file.isDirectory) {
                val result = findSource(file, project, name)
                if (result != null) {
                    return result
                }
            } else {
                val fileName = file.name
                if (fileName.endsWith(".tar.gz")) {
                    val filePath = file.absolutePath
                    if (!tarCache.contains(filePath)) {
                        tarCache[filePath] = TarGzArchive(file)
                    }
                    val result = findInArchive(tarCache[filePath]!!, name)
                    if (result != null) {
                        return TarGzFile(LocalFileSystem.getInstance().findFileByIoFile(file)!!, result)
                    }
                }
            }
        }
        return null
    }

    fun findInArchive(tarGzArchive: TarGzArchive, name: String) : String? {
        val fileEnd = name.replace("\\.".toRegex(), "/") + ".hs"

        for (file in tarGzArchive.filesList) {
            if (file.endsWith(fileEnd)) {
                return file
            }
        }
        return null
    }
}