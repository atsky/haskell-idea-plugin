package org.jetbrains.generator.grammar

import java.util.TreeSet

/**
 * Created by atsky on 11/7/14.
 */
abstract class Variant() {
    abstract fun fillElements(elementSet: TreeSet<String>)

}

class FinalVariant(val elementName: String?) : Variant() {
    override fun fillElements(elementSet: TreeSet<String>) {
        if (elementName != null) {
            elementSet.add(elementName)
        }
    }

}

class NonFinalVariant(val atom: RuleRef, val next : List<Variant>) : Variant() {

    override fun toString(): String {
        val result = StringBuilder()
/*
        for (ref in atoms) {
            if (ref != atoms[0]) {
                result.append(" ")
            }
            result.append(ref)
        }
*/
        return result.toString()
    }

    override fun fillElements(elementSet: TreeSet<String>) {
        for (n in next) {
            n.fillElements(elementSet)
        }
    }
}