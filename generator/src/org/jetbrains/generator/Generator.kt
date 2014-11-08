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
                        if (!tokens.contains(name) && !rules.contains(name)) {
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
        generateLexerTokens(grammar.tokens)
        generateTokens()
        generateParser();
    }

    fun generateLexerTokens(tokens: List<TokenDescription>) {
        val result = TextGenerator()
        with(result) {
            line("package org.jetbrains.grammar;")
            line()
            line("import org.jetbrains.haskell.parser.HaskellToken;")
            line()
            line()
            line("public interface HaskellLexerTokens {")

            indent {
                for (token in tokens) {
                    val name = token.name.toUpperCase()
                    line("public static HaskellToken ${name} = new HaskellToken(\"${token.text}\");");
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
        with(result) {
            line("package org.jetbrains.grammar")
            line()
            line("import com.intellij.psi.tree.IElementType")
            line()
            line()
            line("object HaskellTokens {")

            indent {
                for (token in grammar.rules) {
                    val name = token.name.toUpperCase()
                    line("val ${name} = IElementType(\"${token.name}\", null)");
                }
            }
            line("}")
        }


        val parent = File(MAIN_PATH)
        parent.mkdirs()
        val writer = FileWriter(File(parent, "HaskellTokens.kt"))
        writer.write(result.toString())
        writer.close()
    }


    fun generateParser() {
        val result = TextGenerator()
        with(result) {
            line("package org.jetbrains.grammar")
            line()
            line("import org.jetbrains.grammar.HaskellTokens")
            line("import com.intellij.lang.PsiBuilder")
            line()
            line()
            line("public class HaskellParser(state : PsiBuilder) : BaseHaskellParser(state) {")

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


        val parent = File(MAIN_PATH)
        parent.mkdirs()
        val writer = FileWriter(File(parent, "HaskellParser.kt"))
        writer.write(result.toString())
        writer.close()
    }

    fun generateRule(textGenerator: TextGenerator,
                     rule: Rule) {
        with(textGenerator) {
            line()
            line("// " + rule.toString())
            line("fun ${getParseFun(rule.name)}() : Boolean {")
            indent {
                line("var res = true")
                var first = true
                for (variant in rule.variants) {
                    generateVariant(this, variant, first)
                    first = false
                }
                line("return res")
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
                        variant: Variant,
                        first : Boolean) {
        with(textGenerator) {
            if (variant.atoms.empty) {
                line("res = true")
            } else {
                if (first) {
                    line("var mark = makeMark()")
                } else {
                    line("res = true")
                    line("mark = makeMark()")
                }
                for (ref in variant.atoms) {
                    val prefix = "res = res && "
                    if (tokens.contains(ref.text)) {
                        val token = tokens[ref.text]!!
                        line(prefix + "token(HaskellLexerTokens.${token.name.toUpperCase()})")
                    } else {
                        line(prefix + getParseFun(ref.text) + "()")
                    }
                }

                line("if (res) {")
                line("  mark.drop()")
                line("  return true")
                line("}")
                line("mark.rollbackTo()")
            }
        }
    }

    fun getParseFun(name: String): String =
            "parse" + Character.toUpperCase(name[0]) + name.substring(1)

}


