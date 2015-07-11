package org.jetbrains.yesod.julius.completion;

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

public class JuliusCompletionContributor extends CompletionContributor {
    public static List<String> TAG_NAMES = Arrays.asList(
            "var",
            "function",
            "instanceof",
            "if",
            "else",
            "switch",
            "case",
            "break",
            "default",
            "for",
            "while",
            "do",
            "continue",
            "new",
            "delete",
            "return",
            "catch",
            "try",
            "throw",
            "finally",
            "in",
            "typeof",
            "with",
            "this",
            "debugger"

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

