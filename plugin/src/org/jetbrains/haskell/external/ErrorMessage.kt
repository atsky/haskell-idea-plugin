package org.jetbrains.haskell.external

import org.json.simple.JSONObject

/**
 * Created by atsky on 17/05/14.
 */
class ErrorMessage(
    val text : String,
    val file : String,
    val line : Int,
    val column : Int,
    val eLine : Int,
    val eColumn : Int) {


    override fun toString(): String {
        return "Error: " + text + "\n" +
                "in " + file + " " + line + ":" + column + "-" + eLine + ":" + eColumn;
    }

    class object {
        fun fromJson(a : Any) : ErrorMessage {
            val obj = a as JSONObject
            val text = obj.get("t") as String
            val location = obj.get("l") as JSONObject
            return ErrorMessage(
                text,
                location.get("f") as String,
                (location.get("l") as Long).toInt(),
                (location.get("c") as Long).toInt(),
                (location.get("el") as Long).toInt(),
                (location.get("ec") as Long).toInt()
            )
        }
    }
}