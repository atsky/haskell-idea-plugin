package org.jetbrains.haskell.scope

import org.jetbrains.haskell.fileType.HaskellFile
import org.jetbrains.haskell.psi.ModuleName
import org.jetbrains.haskell.util.ProcessRunner
import java.util.HashMap
import org.jetbrains.haskell.external.ghcfs.RamFile
import com.intellij.psi.PsiManager
import com.intellij.openapi.project.Project

/**
 * Created by atsky on 12/9/14.
 */
public object GlobalScope {
    val cache = HashMap<String, HaskellFile>()

    fun getModule(project : Project, name: String): HaskellFile? {
        if (cache.containsKey(name)) {
            return cache[name];
        }
        val text = ProcessRunner(null).executeNoFail("ghc", "-e", ":browse! ${name}")
        if (text == "") {
            return null;
        }

        val content = "module ${name} where\n" +
                      text
        val ramFile = RamFile(name + ".hs", content)
        return PsiManager.getInstance(project).findFile(ramFile) as HaskellFile?;
    }

}