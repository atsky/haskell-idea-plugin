package org.jetbrains.cabal

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.cabal.psi.Executable
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.CachedValueProvider
import java.util.ArrayList
import org.jetbrains.haskell.external.BuildWrapper
import org.json.simple.JSONArray
import org.json.simple.JSONObject

public class CabalFile(provider: FileViewProvider) : PsiFileBase(provider, CabalLanguage.INSTANCE) {
    public override fun getFileType(): FileType {
        return CabalFileType.INSTANCE
    }
    public override fun accept(visitor: PsiElementVisitor): Unit {
        visitor.visitFile(this)
    }

    public fun getExecutables(): MutableList<Executable> {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, javaClass<Executable>())
    }

    public fun getPackagesList() : List<Pair<String, String>> {
        return CachedValuesManager.getCachedValue(this, object : CachedValueProvider<List<Pair<String, String>>> {
            override fun compute(): CachedValueProvider.Result<List<Pair<String, String>>>? {
               val result = ArrayList<Pair<String, String>>()

                val cabalFile = this@CabalFile

                val array = BuildWrapper.init(cabalFile).dependencies()
                val resultArray = array!!.get(0) as JSONArray?

                for (src in resultArray!!) {

                    for (pkg in ((src as JSONArray)[1] as JSONArray)) {
                        val obj = (pkg as JSONObject)
                        result.add(Pair(obj.get("n") as String,
                                        obj.get("v") as String))
                    }


                }


                return CachedValueProvider.Result.create(result, cabalFile)
            }

        })!!
    }

}
