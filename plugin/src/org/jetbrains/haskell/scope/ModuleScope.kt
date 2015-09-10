package org.jetbrains.haskell.scope

import org.jetbrains.haskell.psi.*
import java.util.ArrayList

/**
 * Created by atsky on 03/05/14.
 */
public class ModuleScope(val module : Module) {

    fun getVisibleSignatureDeclaration() : List<SignatureDeclaration> {
        val list = ArrayList(module.getSignatureDeclarationsList())

        list.addAll(getImports().flatMap { ImportScope(it).getSignatureDeclarations() })

        return list
    }

    fun getVisibleForeignDeclarations() : List<ForeignDeclaration> {
        val list = ArrayList(module.getForeignDeclarationList())

        //list.addAll(getImports().flatMap { ImportScope(it).getSignatureDeclarations() })

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
