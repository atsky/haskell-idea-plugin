package org.jetbrains.generator

import java.io.FileReader
import java.util.ArrayList
import org.jetbrains.generator.grammar.TokenDescription
import java.io.PrintStream
import java.io.File
import java.io.FileWriter
import org.jetbrains.generator.grammar.*
import java.util.HashMap
import java.util.HashSet
import java.util.TreeSet

/**
 * Created by atsky on 11/7/14.
 */
class ParserGenerator(val grammar: Grammar) {
    class object {
        val MAIN_PATH = "./plugin/gen/org/jetbrains/grammar/"
    }

    val tokens: Map<String, TokenDescription>
    val rules: Map<String, AbstractRule>
    val fakeRules: List<FakeRule>;

    {
        val tokens = HashMap<String, TokenDescription>()
        val rules = HashMap<String, AbstractRule>()
        val fakeRules = ArrayList<FakeRule>()


        for (token in grammar.tokens) {
            if (token.useText) {
                tokens["'${token.text}'"] = token;
            } else {
                tokens[token.text] = token;
            }
        }

        for (rule in grammar.rules) {
            rules[rule.name] = rule
        }

        this.tokens = tokens;
        this.rules = rules;
        this.fakeRules = fakeRules;
    }

    fun generate() {
        generateLexerTokens(grammar.tokens)
        generateTokens()
        generateParser();
    }

    fun generateLexerTokens(tokens: List<TokenDescription>) {
        val result = TextGenerator()
        with(result) {
            line("package org.jetbrains.grammar;")
            line()
            line("import org.jetbrains.haskell.parser.HaskellTokenType;")
            line()
            line()
            line("public interface HaskellLexerTokens {")

            indent {
                for (token in tokens) {
                    val name = token.name.toUpperCase()
                    line("public static HaskellTokenType ${name} = new HaskellTokenType(\"${token.text}\");");
                }
            }
            line("}")
        }


        val parent = File(MAIN_PATH)
        parent.mkdirs()
        val writer = FileWriter(File(parent, "HaskellLexerTokens.java"))
        writer.write(result.toString())
        writer.close()
    }

    fun generateTokens() {
        val result = TextGenerator()
        val elementSet = TreeSet<String>()
        for (token in grammar.rules) {
            for (variant in token.variants) {
                variant.fillElements(elementSet)
            }
        }
        with(result) {
            line("package org.jetbrains.grammar")
            line()
            line("import com.intellij.psi.tree.IElementType")
            line("import org.jetbrains.haskell.parser.HaskellCompositeElementType")
            line("import org.jetbrains.haskell.psi.*")
            line()
            line()
            for (element in elementSet) {
                line("public val ${camelCaseToUpperCase(element)} : IElementType = HaskellCompositeElementType(\"${element}\", ::${element})")
            }
        }


        val parent = File(MAIN_PATH)
        parent.mkdirs()
        val writer = FileWriter(File(parent, "HaskellTokens.kt"))
        writer.write(result.toString())
        writer.close()
    }

    fun camelCaseToUpperCase(string: String): String {
        val result = StringBuilder()
        var first = true;
        for (char in string) {
            if (Character.isUpperCase(char)) {
                if (!first) {
                    result.append("_")
                }
                result.append(char)
            } else {
                result.append(Character.toUpperCase(char))
            }
            first = false;
        }
        return result.toString();
    }

    fun generateParser() {
        val result = TextGenerator()
        with(result) {
            line("package org.jetbrains.grammar;")
            line()
            line("import static org.jetbrains.grammar.HaskellLexerTokens.*;")
            line("import com.intellij.lang.PsiBuilder;")
            line("import org.jetbrains.annotations.NotNull;")
            line("import org.jetbrains.grammar.dumb.*;")
            line()
            line("import java.util.*;")
            line()
            line("public class HaskellParser extends BaseHaskellParser {")

            indent {
                line("public HaskellParser(PsiBuilder builder) {")
                line("  super(builder);")
                line("}")
                line()
                line("@NotNull")
                line("public Map<String, Rule> getGrammar() {")
                indent {
                    line("Map<String, Rule> grammar = new HashMap<String, Rule>();")

                    for (rule in rules.values()) {
                        if (rule is Rule) {
                            generateRule(this, rule)
                        }
                    }

                    line("return grammar;");
                }
                line("}")
            }

            line("}")
        }


        val parent = File(MAIN_PATH)
        parent.mkdirs()
        val writer = FileWriter(File(parent, "HaskellParser.java"))
        writer.write(result.toString())
        writer.close()
    }

    fun generateRule(textGenerator: TextGenerator,
                     rule: Rule) {
        with(textGenerator) {
            line("{")
            indent {
                line("List<Variant> variants = new ArrayList<Variant>();")
                line("List<Variant> left = new ArrayList<Variant>();")
                for (varinant in rule.variants) {
                    generateVariant(this, rule, varinant);
                }
                line("grammar.put(\"${rule.name}\", new Rule(\"${rule.name}\", variants, left));")
            }
            line("}")
        }
    }

    fun generateFakeRule(textGenerator: TextGenerator,
                         rule: FakeRule) {
        with(textGenerator) {
            line()
            line("// Fake rule")
            line("fun ${getParseFun(rule.name)}() : Boolean {")
            indent {
                line("throw FakeRuleException()")
            }
            line("}")
        }
    }

    fun generateVariant(textGenerator: TextGenerator,
                        rule: Rule,
                        variant: Variant) {
        with(textGenerator) {
            val builder = StringBuilder()

            fillVariant(builder, variant)

            if (variant is NonFinalVariant && variant.atom.toString() == rule.name) {
                line("addVar(left, ${builder});")
            } else {
                line("addVar(variants, ${builder});")
            }
        }
    }

    fun fillVariant(builder : StringBuilder, variant : Variant): StringBuilder {
        if (variant is NonFinalVariant) {
            if (variant.next.size() == 1) {
                fillVariant(builder, variant.next.firstOrNull()!!)
                val atom = variant.atom
                if (tokens.containsKey(atom.toString())) {
                    val tokenDescription = tokens[atom.toString()]!!
                    builder.append(".add(" + tokenDescription.name.toUpperCase() + ")")
                } else {
                    builder.append(".add(\"" + atom.text + "\")")
                }
            } else {
                val atom = variant.atom
                if (tokens.containsKey(atom.toString())) {
                    val tokenDescription = tokens[atom.toString()]!!
                    builder.append("many(" + tokenDescription.name.toUpperCase() + "")
                } else {
                    builder.append("many(\"" + atom.text + "\"")
                }
                for (variant in variant.next) {
                    builder.append(", ")
                    fillVariant(builder, variant)
                }
                builder.append(")")
            }
        } else {
            val elementName = (variant as FinalVariant).elementName
            builder.append(if (elementName != null) {
                "end(GrammarPackage.get${camelCaseToUpperCase(elementName)}())"
            } else {
                "end()"
            })
        }
        return builder
    }

    fun getParseFun(name: String): String =
            "parse" + Character.toUpperCase(name[0]) + name.substring(1)

}


