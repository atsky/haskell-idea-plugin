package org.jetbrains.haskell.scope

import org.jetbrains.haskell.psi.SignatureDeclaration
import org.jetbrains.haskell.psi.Module
import java.util.ArrayList
import org.jetbrains.haskell.psi.DataDeclaration
import org.jetbrains.haskell.psi.ConstructorDeclaration
import org.jetbrains.haskell.psi.TypeSynonym
import org.jetbrains.haskell.psi.Import

/**
 * Created by atsky on 03/05/14.
 */
public class ModuleScope(val module : Module) {

    fun getVisibleSignatureDeclaration() : List<SignatureDeclaration> {
        val list = ArrayList(module.getSignatureDeclarationsList())

        list.addAll(getImports().flatMap { ImportScope(it).getSignatureDeclarations() })

        return list
    }

    fun getVisibleTypeSynonyms() : List<TypeSynonym> {
        val list = ArrayList(module.getTypeSynonymList())
        list.addAll(getImports().flatMap { ImportScope(it).getTypeSynonym() })
        return list
    }

    fun getVisibleDataDeclarations() : List<DataDeclaration> {
        val list = ArrayList(module.getDataDeclarationList())
        list.addAll(getImports().flatMap { ImportScope(it).getDataDeclarations() })
        return list
    }

    private fun getImports(): List<Import> {
        return module.getImportList()
    }

    fun getVisibleConstructors() : List<ConstructorDeclaration> {
        return getVisibleDataDeclarations().flatMap { it.getConstructorDeclarationList() }
    }
}
