package org.jetbrains.generator

import java.io.FileReader
import java.util.ArrayList
import org.jetbrains.generator.grammar.TokenDescription
import java.io.PrintStream
import java.io.File
import java.io.FileWriter
import org.jetbrains.generator.grammar.*
import java.util.HashMap

/**
 * Created by atsky on 11/7/14.
 */
class Generator(val grammar: Grammar) {

    val tokens : Map<String, TokenDescription>
    val rules : Map<String, AbstractRule>
    val fakeRules : List<FakeRule>;

    {
        val tokens = HashMap<String, TokenDescription>()
        val rules = HashMap<String, AbstractRule>()
        val fakeRules = ArrayList<FakeRule>()


        for (token in grammar.tokens) {
            tokens[token.name] = token;
            tokens[token.text] = token;
        }

        for (rule in grammar.rules) {
            rules[rule.name] = rule
        }

        for (rule in grammar.rules) {
            for (variant in rule.variants) {
                for (ref in variant.atoms) {
                    if (ref.isName) {
                        val name = ref.text
                        if (!tokens.contains(name) && ! rules.contains(name)) {
                            val rule = FakeRule(name)
                            fakeRules.add(rule)
                            rules[name] = rule;
                        }
                    }
                }
            }
        }

        this.tokens = tokens;
        this.rules = rules;
        this.fakeRules = fakeRules;
    }

    fun generate() {
        generateTokens(grammar.tokens)
        generateParser();
    }

    fun generateTokens(tokens: List<TokenDescription>) {
        val result = TextGenerator()
        with(result) {
            line("package org.jetbrains.grammar")
            line("import org.jetbrains.haskell.parser.HaskellToken")
            line()
            line()
            line("object HaslkellTokens {")

            indent {
                for (token in tokens) {
                    val name = token.name.toUpperCase()
                    line("val ${name} = HaskellToken(\"${token.text}\")");
                }
            }
            line("}")
        }


        val parent = File("./grammar/gen/org/jetbrains/grammar/")
        parent.mkdirs()
        val writer = FileWriter(File(parent, "HaskellTokens.kt"))
        writer.write(result.toString())
        writer.close()
    }

    fun generateParser() {
        val result = TextGenerator()
        with(result) {
            line("package org.jetbrains.grammar")
            line("import org.jetbrains.haskell.parser.HaskellParser")
            line()
            line()
            line("class HaslkellParser {")

            indent {
                for (rule in grammar.rules) {
                    generateRule(this, rule)
                }

                for (rule in fakeRules) {
                    generateFakeRule(this, rule)
                }
            }

            line("}")
        }


        val parent = File("./grammar/gen/org/jetbrains/grammar/")
        parent.mkdirs()
        val writer = FileWriter(File(parent, "HaskellParser.kt"))
        writer.write(result.toString())
        writer.close()
    }

    fun generateRule(textGenerator: TextGenerator,
                     rule: Rule) {
        with(textGenerator) {
            line()
            line("// Fake")
            line("fun ${getParseFun(rule.name)}() : Boolean {")
            indent {
                for (variant in rule.variants) {
                    generateVariant(this, variant)
                }
                line("return false")
            }
            line("}")
        }
    }

    fun generateFakeRule(textGenerator: TextGenerator,
                         rule: FakeRule) {
        with(textGenerator) {
            line()
            line("// " + rule.toString())
            line("fun ${getParseFun(rule.name)}() : Boolean {")
            indent {
                line("return false")
            }
            line("}")
        }
    }

    fun generateVariant(textGenerator: TextGenerator,
                        variant: Variant) {
        with(textGenerator) {
            for (ref in variant.atoms) {
                if (ref.isName) {
                    line(getParseFun(ref.text) + "()")
                }
            }
        }
    }

    fun getParseFun(name: String): String =
        "parse" + Character.toUpperCase(name[0]) + name.substring(1)

}


