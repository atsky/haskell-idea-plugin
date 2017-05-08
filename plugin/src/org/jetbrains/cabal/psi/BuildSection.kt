package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import org.jetbrains.cabal.CabalFile
import java.util.ArrayList
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File


open class BuildSection(node: ASTNode): Section(node) {

    private fun <F : MultiValueField, V : PropertyValue> getFieldsValues(fieldT: Class<F>, valueT: Class<V>): List<V>
            = getFields(fieldT).flatMap { it.getValues(valueT) }

    fun getHsSourceDirs(): List<Path> = getFieldsValues(HsSourceDirsField::class.java, Path::class.java)

    fun getIncludeDirs() : List<Path> = getFieldsValues(IncludeDirsField::class.java, Path::class.java)

    fun getBuildDepends(): List<FullVersionConstraint>
            = getFieldsValues(BuildDependsField::class.java, FullVersionConstraint::class.java)

}