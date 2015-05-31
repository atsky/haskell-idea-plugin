package org.jetbrains.haskell.external

import org.json.simple.JSONObject
import org.jetbrains.haskell.external.ErrorMessage.Severity

/**
 * Created by atsky on 17/05/14.
 */
class ErrorMessage(
    val text : String,
    val file : String,
    val severity : Severity,
    val line : Int,
    val column : Int,
    val eLine : Int,
    val eColumn : Int) {

    // Reflects BuildWrapper/Base.hs: BWNoteStatus
    enum class Severity { Error, Warning }

    override fun toString(): String {
        return "Error: " + text + "\n" +
                "in " + file + " " + line + ":" + column + "-" + eLine + ":" + eColumn;
    }

    companion object {
        fun fromJson(a : Any) : ErrorMessage {
            val obj = a as JSONObject
            val text = obj.get("t") as String
            val severity = obj.get("s") as String
            val location = obj.get("l") as JSONObject
            return ErrorMessage(
                text,
                location.get("f") as String,
                if (severity == "Warning") Severity.Warning else Severity.Error,
                (location.get("l") as Long).toInt(),
                (location.get("c") as Long).toInt(),
                (location.get("el") as Long).toInt(),
                (location.get("ec") as Long).toInt()
            )
        }
    }
}