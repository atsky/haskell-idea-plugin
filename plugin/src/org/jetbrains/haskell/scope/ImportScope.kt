package org.jetbrains.haskell.scope

import org.jetbrains.haskell.psi.Import
import org.jetbrains.haskell.psi.FunctionDeclaration
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

    fun getFunctions() : List<FunctionDeclaration> {
        val list = import.findModule()?.getFunctionDeclarationList() ?: listOf()
        return filterDeclarations(list)
    }

}