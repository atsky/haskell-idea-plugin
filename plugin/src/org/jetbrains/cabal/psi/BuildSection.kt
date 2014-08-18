package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.CabalFile
import java.util.ArrayList
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File


public open class BuildSection(node: ASTNode): Section(node) {

    private fun <F : PropertyField, V : PropertyValue> getFieldsValues(fieldT: Class<F>, valueT: Class<V>): List<V>
            = getFields(fieldT) flatMap { it.getValues(valueT) }

    public fun getHSSourceDirs(): List<Path> = getFieldsValues(javaClass<HsSourceDirsField>(), javaClass<Path>())

    public fun getIncludeDirs() : List<Path> = getFieldsValues(javaClass<IncludeDirsField>() , javaClass<Path>())

    public fun getBuildDepends(): List<FullVersionConstraint>
            = getFieldsValues(javaClass<BuildDependsField>(), javaClass<FullVersionConstraint>())

}