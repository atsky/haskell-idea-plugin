package org.jetbrains.yesod.hamlet.completion

/**
 * @author Leyla H
 */

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder

import java.util.Arrays

public class HamletCompletionContributor : CompletionContributor() {

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        if (parameters.getCompletionType() === CompletionType.BASIC) {
            for (tagName in TAGS) {
                result.addElement(LookupElementBuilder.create(tagName))
            }
            for (tagName in ATTRIBUTES) {
                result.addElement(LookupElementBuilder.create(tagName))
            }
        }
    }

    companion object {

        public var ATTRIBUTES: List<String> = Arrays.asList(
                "accept",     "accept-charset", "accesskey",       "action",      "align",       "itemprop",
                "alt",        "async",          "autocomplete",    "autofocus",   "autoplay",    "autosave",
                "bgcolor",    "border",         "buffered",        "challenge",   "charset",     "checked",
                "cite",       "class",          "code",            "codebase",    "color",       "cols",
                "colspan",    "content",        "contenteditable", "contextmenu", "controls",    "coords",
                "data",       "data-*",         "datetime",        "default",     "defer",       "dir",
                "dirname",    "disabled",       "download",        "draggable",   "dropzone",    "enctype",
                "for",        "form",           "formaction",      "headers",     "height",      "hidden",
                "high",       "href",           "hreflang",        "http-equiv",  "icon",        "id",
                "ismap",      "keytype",        "kind",            "label",       "lang",        "language",
                "list",       "loop",           "low",             "manifest",    "max",         "maxlength",
                "media",      "method",         "min",             "multiple",    "name",        "novalidate",
                "open",       "optimum",        "pattern",         "ping",        "placeholder", "poster",
                "preload",    "pubdate",        "radiogroup",      "readonly",    "rel",         "alternate",
                "archives",   "author",         "bookmark",        "external",    "first",       "help",
                "icon",       "index",          "last",            "license",     "next",        "nofollow",
                "noreferrer", "pingback",       "prefetch",        "prev",        "search",      "sidebar",
                "stylesheet", "tag",            "up",              "required",    "reversed",    "rows",
                "rowspan",    "sandbox",        "scope",           "scoped",      "seamless",    "selected",
                "shape",      "size",           "sizes",           "span",        "spellcheck",  "src",
                "srcdoc",     "srclang",        "srcset",          "start",       "step",        "style",
                "summary",    "tabindex",       "target",          "title",       "type",        "usemap",
                "value",      "width",          "wrap"
        )

        public var TAGS: List<String> = Arrays.asList(
                "a",          "abbr",       "address",  "area",      "b",        "base",
                "bdo",        "blockquote", "body",     "br",        "button",   "caption",
                "cite",       "code",       "col",      "colgroup",  "dd",       "del",
                "dfn",        "div",        "dl",       "dt",        "em",       "fieldset",
                "form",       "h1",         "h2",       "h3",        "h4",       "h5",
                "h6",         "head",       "hr",       "html",      "i",        "iframe",
                "img",        "input",      "ins",      "kbd",       "label",    "legend",
                "li",         "link",       "map",      "meta",      "noscript", "object",
                "ol",         "optgroup",   "option",   "p",         "param",    "pre",
                "q",          "s",          "samp",     "script",    "select",   "small",
                "span",       "strong",     "style",    "sub",       "sup",      "table",
                "tbody",      "td",         "textarea", "tfoot",     "th",       "thead",
                "title",      "tr",         "u",        "ul",        "var",

                "acronym",    "applet",     "basefont", "big",       "blink",    "center",
                "command",    "dir",        "font",     "frame",     "frameset", "hgroup",
                "isindex",    "listing",    "noframes", "plaintext", "spacer",   "strike",
                "tt",         "xmp",

                "article",    "aside",      "audio",    "bdi",       "canvas",   "content",
                "data",       "datalist",   "details",  "dialog",    "element",  "embed",
                "figcaption", "figure",     "footer",   "header",    "keygen",   "main",
                "mark",       "menu",       "menuitem", "meter",     "nav",      "output",
                "picture",    "progress",   "rp",       "rt",        "rtc",      "ruby",
                "section",    "source",     "summary",  "template",  "time",     "track",
                "video",      "wbr"
        )
    }
}


