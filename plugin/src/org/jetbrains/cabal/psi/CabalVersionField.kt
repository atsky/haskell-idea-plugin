package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.SingleValueField
import org.jetbrains.cabal.CabalInterface
import org.jetbrains.cabal.highlight.ErrorMessage
import java.util.ArrayList

class CabalVersionField(node: ASTNode) : SingleValueField(node), Checkable {

    override fun check(): List<ErrorMessage> {
        val installedCabalVersions = (CabalInterface(project).getInstalledPackagesList().firstOrNull { it.name == "Cabal" })?.availableVersions
        if (installedCabalVersions == null) return listOf(ErrorMessage(getValue()!!, "Cabal package is not installed", "warning"))
        val versionConstr = (getValue() as VersionConstraint)
        if (!(installedCabalVersions.map({ versionConstr.satisfyConstraint(it) }).reduce { curr, next -> curr || next })) {
            return listOf(ErrorMessage(versionConstr, "installed cabal's version does not satisfy this constraint", "warning"))
        }
        return listOf()
    }
}