package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.parser.*
import java.util.ArrayList
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File


public open class BuildSection(node: ASTNode): Section(node) {

    public fun getHSSourceDirs(): List<Path>? = getField(javaClass<HSSourceDirsField>())?.getValues() as List<Path>?

    public fun getBuildDepends(): List<FullVersionConstraint>? = getField(javaClass<BuildDependsField>())?.getValues() as List<FullVersionConstraint>?

}