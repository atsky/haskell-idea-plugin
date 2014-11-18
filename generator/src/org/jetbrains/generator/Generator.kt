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
            if (token.useText) {
                tokens["'${token.text}'"] = token;
            } else {
                tokens[token.text] = token;
            }
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
        //generateTokens()
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
            line("import org.jetbrains.haskell.parser.HaskellCompositeElementType")
            line("import org.jetbrains.haskell.psi.*")
            line()
            line()
            line("object HaskellTokens {")

            indent {
                for (token in grammar.rules) {
                    val name = token.name.toUpperCase()
                    val psiName = Character.toUpperCase(token.name[0]) + token.name.substring(1)
                    if (psiName == "Module") {
                        line("val ${name} = HaskellCompositeElementType(\"${token.name}\", ::${psiName})");
                    } else {
                        line("val ${name} = HaskellCompositeElementType(\"${token.name}\")");
                    }
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
            line("import org.jetbrains.grammar.HaskellLexerTokens.*")
            line("import com.intellij.lang.PsiBuilder")
            line("import org.jetbrains.grammar.dumb.Rule")
            line()
            line()
            line("public class HaskellParser(state : PsiBuilder?) : BaseHaskellParser(state) {")

            indent {
                line("override fun getGrammar() : Map<String, Rule> {")
                indent {
                    line("return grammar {")
                        indent {
                            for (rule in rules.values()) {
                                if (rule is Rule) {
                                    generateRule(this, rule)
                                }
                            }
                        }
                    line("}");
                }
                line("}")
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
            line("rule(\"${rule.name}\") {")
            indent {
                for (varinant in rule.variants) {
                    generateVariant(this, rule, varinant);
                }
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
                        rule : Rule,
                        variant: Variant) {
        with(textGenerator) {
            val builder = StringBuilder()
            var first = true;
            for (atom in variant.atoms) {
                if (!first) {
                    builder.append(", ")
                }
                if (tokens.containsKey(atom.toString())) {
                    val tokenDescription = tokens[atom.toString()]!!
                    builder.append(tokenDescription.name.toUpperCase())
                } else {
                    builder.append("\"" + atom.text + "\"")
                }
                first = false;
            }
            line("variant(${builder})")
        }
    }

    fun getParseFun(name: String): String =
            "parse" + Character.toUpperCase(name[0]) + name.substring(1)

}


