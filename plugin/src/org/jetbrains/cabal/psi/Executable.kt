package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import java.util.ArrayList

/**
 * @author Evgeny.Kurbatsky
 */
public class Executable(node: ASTNode) : Section(node) {

    public fun getExecutableName() : String {
        return getChildren()[0].getText()!!
    }

    public override fun getRequiredFieldNames(): List<String> = listOf("main-is")

    public override fun getAvailableFieldNames(): List<String> {
        var res = ArrayList<String>()
        res.addAll(EXECUTABLE_FIELDS)
        res.addAll(BUILD_INFO)
        res.addAll(listOf("is", "else"))
        return res
    }

    public fun getMainFile(): PropertyValue? = getField(CabalTokelTypes.MAIN_FILE)?.getLastValue()

    public fun getHSSourceDirs(): List<PropertyValue> {
        val values = getField(CabalTokelTypes.HS_SOURCE_DIRS)?.getValues()
        if (values == null) return listOf()
        return values
    }

    public fun getBuildDepends(): List<Pair<String, ComplexVersionConstraint?>> {
        val values = getField(CabalTokelTypes.BUILD_DEPENDS)?.getValues()
        if (values == null) return listOf()
        var res : ArrayList<Pair<String, ComplexVersionConstraint?>> = ArrayList()
        for (value in values) {
            res.add(Pair((value as FullVersionConstraint).getBaseName(), (value as FullVersionConstraint).getConstraint()))
        }
        return res
    }

}