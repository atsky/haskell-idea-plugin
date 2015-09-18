package org.jetbrains.cabal.psi

import com.intellij.lang.ASTNode
import org.jetbrains.cabal.psi.MultiValueField
import org.jetbrains.cabal.psi.FullVersionConstraint
import org.jetbrains.cabal.psi.ComplexVersionConstraint
import org.jetbrains.cabal.psi.Checkable
import org.jetbrains.cabal.CabalInterface
import org.jetbrains.cabal.highlight.ErrorMessage
import java.util.ArrayList

public class BuildDependsField(node: ASTNode) : MultiValueField(node), Checkable {

    public fun getPackageNames(): List<String> = getValues(FullVersionConstraint::class.java) map { it.getBaseName() }

    public fun getConstraintsWithName(name: String): List<FullVersionConstraint>
            = getValues(FullVersionConstraint::class.java) filter { it.getBaseName().equals(name) }

    public override fun check(): List<ErrorMessage> {
        val packageConstraints = getValues(FullVersionConstraint::class.java)
        val installedPackages  = CabalInterface(getProject()).getInstalledPackagesList()
        var res = ArrayList<ErrorMessage>()
        for (constraint in packageConstraints) {
            val constrName = constraint.getBaseName()
            val sameConstraints = getConstraintsWithName(constraint.getBaseName())
            if (sameConstraints.size() != 1) {
                sameConstraints forEach { res.add(ErrorMessage(it, "dublicate package", "warning")) }
                continue
            }
            val installed = installedPackages firstOrNull { it.name == constrName }
            if (installed == null) {
                res.add(ErrorMessage(constraint, "this package is not installed", "warning"))
                continue
            }
            val versionConstr = constraint.getConstraint()
            if (versionConstr == null) continue
            if (!(installed.availableVersions map { versionConstr.satisfyConstraint(it) } reduce { curr, next -> curr || next })) {
                res.add(ErrorMessage(versionConstr, "installed package's version does not satisfy this constraint", "warning"))
            }
        }
        return res
    }
}