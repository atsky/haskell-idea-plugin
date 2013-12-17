package org.jetbrains.haskell.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.haskell.psi.Module;

import java.util.HashSet;
import java.util.Set;


public class HaskellCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(final CompletionParameters parameters, final CompletionResultSet result) {
        if (parameters.getCompletionType() == CompletionType.BASIC) {

            final Set<String> values = new HashSet<String>();
            findCompletion(parameters.getOriginalPosition(), values);
            for (String value : values) {
                result.addElement(LookupElementBuilder.create(value));
            }
        }
    }

    private void findCompletion(PsiElement element, Set<String> completions) {
        while (!(element instanceof PsiFile || element instanceof Module)) {
            element = element.getParent();
        }
        if (element instanceof Module) {
            System.out.println(((Module) element).getImportDecl().length);
        }
    }
}