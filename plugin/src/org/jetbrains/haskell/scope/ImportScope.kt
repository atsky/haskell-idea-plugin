package org.jetbrains.haskell.scope

import org.jetbrains.haskell.psi.Import
import org.jetbrains.haskell.psi.SignatureDeclaration
import org.jetbrains.haskell.psi.Declaration
import org.jetbrains.haskell.psi.DataDeclaration
import com.intellij.psi.util.PsiTreeUtil
import sun.reflect.generics.tree.TypeSignature
import org.jetbrains.haskell.psi.TypeSynonym

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

    fun getValues() : List<SignatureDeclaration> {
        val module = import.findModule()
        if (module != null) {
            //return filterDeclarations(ModuleScope(module).getSignatureDeclaration())
        }
        return listOf()
    }

    fun getDataDeclarations() : List<DataDeclaration> {
        val module = import.findModule()
        if (module != null) {
            return filterDeclarations(module.getDataDeclarationList())
        }
        return listOf()
    }

    fun getTypeSynonym() : List<TypeSynonym> {
        val module = import.findModule()
        if (module != null) {
            return filterDeclarations(module.getTypeSynonymList())
        }
        return listOf()
    }

}