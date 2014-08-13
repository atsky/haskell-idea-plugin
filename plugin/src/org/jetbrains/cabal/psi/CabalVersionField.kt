package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.PropertyField
import org.jetbrains.cabal.CabalInterface
import org.jetbrains.cabal.highlight.ErrorMessage
import java.util.ArrayList


public class CabalVersionField(node: ASTNode) : PropertyField(node) {

    public fun checkVersion(): ErrorMessage? {
        val installedCabalVersions = (CabalInterface(getProject()).getInstalledPackagesList() firstOrNull { it.name == "Cabal" })?.availableVersions
        if (installedCabalVersions == null) return ErrorMessage(getValue()!!, "Cabal package is not installed", "warning")
        val versionConstr = (getValue() as VersionConstraint)
        if (!(installedCabalVersions map { versionConstr.satisfyConstraint(it) } reduce { (curr, next) -> curr || next })) {
            return ErrorMessage(versionConstr, "installed cabal's version does not satisfy this constraint", "warning")
        }
        return null
    }
}