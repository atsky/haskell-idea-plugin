package org.jetbrains.yesod.hamlet.completion;

/**
 * @author Leyla H
 */

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;

import java.util.Arrays;
import java.util.List;

public class HamletCompletionContributor extends CompletionContributor {
    public static List<String> TAG_NAMES = Arrays.asList(
                                                         "a",        "abbr",     "address",    "area",
                                                         "article",  "aside",    "audio",      "b",
                                                         "base",     "bdi",      "bdo",        "blockquote",
                                                         "body",     "br",       "button",     "canvas",
                                                         "caption",  "cite",     "code",       "col",
                                                         "colgroup", "data",     "datalist",   "dd",
                                                         "del",      "details",  "dfn",        "dialog",
                                                         "div",      "dl",       "dt",         "em",
                                                         "embed",    "fieldset", "figcaption", "figure",
                                                         "footer",   "form",     "h1",         "h2",
                                                         "h3",       "h4",       "h5",         "h6",
                                                         "head",     "header",   "hgroup",     "hr",
                                                         "html",     "i",        "iframe",     "img",
                                                         "input",    "ins",      "kbd",        "keygen",
                                                         "label",    "legend",   "li",         "link",
                                                         "main",     "map",      "mark",       "menu",
                                                         "menuitem", "meta",     "meter",      "nav",
                                                         "noscript", "object",   "ol",         "optgroup",
                                                         "option",   "output",   "p",          "param",
                                                         "pre",      "progress", "q",          "rb",
                                                         "rp",       "rt",       "rtc",        "ruby",
                                                         "s",        "samp",     "script",     "section",
                                                         "select",   "small",    "source",     "span",
                                                         "strong",   "style",    "sub",        "summary",
                                                         "sup",      "table",    "tbody",      "td",
                                                         "template", "textarea", "tfoot",      "th",
                                                         "thead",    "time",     "title",      "tr",
                                                         "track",    "u",        "ul",         "var",
                                                         "video",    "wbr"
    );

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        if (parameters.getCompletionType() == CompletionType.BASIC) {
            for (String tagName : TAG_NAMES) {
                result.addElement(LookupElementBuilder.create(tagName));
            }
        }
    }
}


