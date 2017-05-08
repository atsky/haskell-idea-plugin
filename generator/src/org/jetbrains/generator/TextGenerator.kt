package org.jetbrains.generator

/**
 * Created by atsky on 11/7/14.
 */


class TextGenerator {
    val data : StringBuilder = StringBuilder()

    var indent : Int = 0


    fun line() {
        data.append("\n")
    }

    fun line(text : String) {
        for (k in 1..indent) {
            data.append("  ")
        }
        data.append(text).append("\n")
    }

    fun indent(body : TextGenerator.() -> Unit) {
        indent++
        this.body()
        indent--
    }

    override fun toString() : String{
        return data.toString()
    }
}