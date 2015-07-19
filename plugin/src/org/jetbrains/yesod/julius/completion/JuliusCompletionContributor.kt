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
        }
    }

    companion object {

        public var KEYWORDS: List<String> = Arrays.asList("abstract", "arguments", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "debugger", "default", "delete", "do", "double", "else", "enum", "eval", "export", "extends", "false", "final", "finally", "float", "for", "function", "goto", "if", "implements", "import", "in", "instanceof", "int", "interface", "let", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "typeof", "var", "void", "volatile", "while", "with", "yield")


        public var AJAX_SETTINGS: List<String> = Arrays.asList(
                "ajax", "accepts", "async", "beforeSend", "cache", "complete", "contents", "contentType", "context", "converters", "crossDomain", "data", "dataFilter", "dataType", "error", "global", "headers", "ifModified", "isLocal", "jsonp", "jsonpCallback", "mimeType", "password", "processData", "scriptCharset", "statusCode", "success", "timeout", "traditional", "type", "url", "username", "xhr", "xhrFields",
                "xml", "html", "script", "json", "text")

        public var AJAX_FUNCTIONS: List<String> = Arrays.asList(
                "jQuery.ajax", "ajaxComplete", "ajaxError", "ajaxSend", "jQuery.ajaxSetup", "ajaxStart", "ajaxStop", "ajaxSuccess", "jQuery.get", "jQuery.getJSON", "jQuery.getScript")
    }
}

