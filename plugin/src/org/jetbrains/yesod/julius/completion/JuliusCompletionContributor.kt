package org.jetbrains.yesod.julius.completion

/**
 * @author Leyla H
 */

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder

import java.util.Arrays

public class JuliusCompletionContributor : CompletionContributor() {

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        if (parameters.getCompletionType() === CompletionType.BASIC) {
            for (tagName in KEYWORDS) {
                result.addElement(LookupElementBuilder.create(tagName))
            }
            for (tagName in AJAX_FUNCTIONS) {
                result.addElement(LookupElementBuilder.create(tagName))
            }
            for (tagName in AJAX_SETTINGS) {
                result.addElement(LookupElementBuilder.create(tagName))
            }
            for (tagName in JS_FUNCTIONS) {
                result.addElement(LookupElementBuilder.create(tagName))
            }
        }
    }

    companion object {

        public var KEYWORDS: List<String> = Arrays.asList(

            "abstract",      "arguments",    "boolean",       "break",      "byte",        "case",
            "catch",         "char",         "class",         "const",      "continue",    "debugger",
            "default",       "delete",       "do",            "double",     "else",        "enum",
            "eval",          "export",       "extends",       "false",      "final",       "finally",
            "float",         "for",          "function",      "goto",       "if",          "implements",
            "import",        "in",           "instanceof",    "int",        "interface",   "let",
            "long",          "native",       "new",           "null",       "package",     "private",
            "protected",     "public",       "return",        "short",      "static",      "super",
            "switch",        "synchronized", "this",          "throw",      "throws",      "transient",
            "true",          "try",          "typeof",        "var",        "void",        "volatile",
            "while",         "with",         "yield"
        )

        public var AJAX_SETTINGS: List<String> = Arrays.asList(

            "ajax",          "accepts",      "async",         "beforeSend", "cache",       "complete",
            "contents",      "contentType",  "context",       "converters", "crossDomain", "data",
            "dataFilter",    "dataType",     "error",         "global",     "headers",     "ifModified",
            "isLocal",       "jsonp",        "jsonpCallback", "mimeType",   "password",    "processData",
            "scriptCharset", "statusCode",   "success",       "timeout",    "traditional", "type",
            "url",           "username",     "xhr",           "xhrFields",

            "xml",           "html",         "script",        "json",       "text"
        )

        public var AJAX_FUNCTIONS: List<String> = Arrays.asList(

            "jQuery.ajax",      "ajaxComplete",   "ajaxError",     "ajaxSend",   "jQuery.ajaxSetup",
            "ajaxStart",        "ajaxStop",       "ajaxSuccess",   "jQuery.get", "jQuery.getJSON",
            "jQuery.getScript"
        )

        public var JS_FUNCTIONS: List<String> = Arrays.asList(

            "anchor",                  "apply",                     "alert",
            "atEnd",                   "big",                       "bind",
            "blink",                   "bold",                      "call",
            "charAt",                  "charCodeAt",                "compile",
            "concat",                  "dimensions",                "every",
            "exec",                    "filter",                    "fixed",
            "fontcolor",               "fontsize",                  "forEach",
            "getDate",                 "getDay",                    "getFullYear",
            "getHours",                "getItem",                   "getMilliseconds",
            "getMinutes",              "getMonth",                  "getSeconds",
            "getTime",                 "getTimezoneOffset",         "getUTCDate",
            "getUTCDay",               "getUTCFullYear",            "getUTCHours",
            "getUTCMilliseconds",      "getUTCMinutes",             "getUTCMonth",
            "getUTCSeconds",           "getVarDate",                "getYear",
            "hasOwnProperty",          "indexOf",                   "isPrototypeOf",
            "italics",                 "item",                      "join",
            "lastIndexOf",             "lbound",                    "link",
            "localeCompare",           "map",                       "match",
            "moveFirst",               "moveNext",                  "pop",
            "propertyIsEnumerable",    "push",                      "reduce",
            "reduceRight",             "replace",                   "reverse",
            "search",                  "setDate",                   "setFullYear",
            "setHours",                "setMilliseconds",           "setMinutes",
            "setMonth",                "setSeconds",                "setTime",
            "setUTCDate",              "setUTCFullYear",            "setUTCHours",
            "setUTCMilliseconds",      "setUTCMinutes",             "setUTCMonth",
            "setUTCSeconds",           "setYear",                   "shift",
            "slice",                   "small",                     "some",
            "sort",                    "splice",                    "split",
            "strike",                  "sub",                       "substr",
            "substring",               "sup",                       "test",
            "toArray",                 "toDateString",              "toExponential",
            "toFixed",                 "toGMTString",               "toISOString",
            "toJSON",                  "toLocaleDateString",        "toLocaleLowerCase",
            "toLocaleString",          "toLocaleTimeString",        "toLocaleUpperCase",
            "toLowerCase",             "toPrecision",               "toString",
            "toTimeString",            "toUpperCase",               "toUTCString",
            "trim",                    "ubound",                    "unshift",
            "valueOf",

            "abs",                      "acos",                     "asin",
            "atan",                     "atan2",                    "ceil",
            "cos",                      "create",                   "decodeURI",
            "decodeURIComponent",       "defineProperties",         "defineProperty",
            "encodeURI",                "encodeURIComponent",       "escape",
            "eval",                     "exp",                      "floor",
            "freeze",                   "fromCharCode",             "GetObject",
            "getOwnPropertyDescriptor", "getOwnPropertyNames",      "getPrototypeOf",
            "isArray",                  "isExtensible",             "isFinite",
            "isFrozen",                 "isNaN",                    "isSealed",
            "keys",                     "log",                      "max",
            "min",                      "now",                      "parse",
            "parseFloat",               "parseInt",                 "pow",
            "preventExtensions",        "random",                   "round",
            "ScriptEngine",             "ScriptEngineBuildVersion", "ScriptEngineMajorVersion",
            "ScriptEngineMinorVersion", "seal",                     "sin",
            "sqrt",                     "stringify",                "tan",
            "unescape",                 "UTC",                      "write",
            "writeln"

        )
    }
}

