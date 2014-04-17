package org.jetbrains.haskell.scope

import org.jetbrains.haskell.psi.Import
import org.jetbrains.haskell.psi.ValueDeclaration
import org.jetbrains.haskell.psi.Declaration

public class ImportScope(val import : Import) {


    public fun filterDeclarations<A : Declaration>(list : List<A>) : List<A> {
        val moduleExports = import.getModuleExports()
        if (moduleExports != null) {
            if (import.hasHiding()) {

            } else {

            }
        }
        return list
    }

    fun getFunctions() : List<ValueDeclaration> {
        val list = import.findModule()?.getValueDeclarationList() ?: listOf()
        return filterDeclarations(list)
    }

}