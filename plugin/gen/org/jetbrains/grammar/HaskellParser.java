package org.jetbrains.grammar;

import static org.jetbrains.grammar.HaskellLexerTokens.*;
import com.intellij.lang.PsiBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.grammar.dumb.*;

import java.util.*;

public class HaskellParser extends BaseHaskellParser {
  public HaskellParser(PsiBuilder builder) {
    super(builder);
  }

  @NotNull
  public Map<String, Rule> getGrammar() {
    Map<String, Rule> grammar = new HashMap<String, Rule>();
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(BANG));
      addVar(variants, many(CLOSE_PRAG, end(), end().add(BANG)).add(NOUNPACK_PRAG));
      addVar(variants, many(CLOSE_PRAG, end(), end().add(BANG)).add(UNPACK_PRAG));
      grammar.put("strict_mark", new Rule("strict_mark", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("decllist_inst").add(WHERE));
      grammar.put("where_inst", new Rule("where_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("tyvar"));
      addVar(variants, end().add(CPAREN).add("kind").add(DCOLON).add("tyvar").add(OPAREN));
      grammar.put("tv_bndr", new Rule("tv_bndr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qual"));
      addVar(variants, end().add("transformqual"));
      addVar(left, many(COMMA, end().add("qual"), end().add("transformqual")).add("squals"));
      grammar.put("squals", new Rule("squals", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qvarop"));
      addVar(variants, end().add("qconop"));
      grammar.put("qop", new Rule("qop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("cvtopdecls"));
      grammar.put("cvtopdecls0", new Rule("cvtopdecls0", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("rule_var", end(), end().add("rule_var_list")));
      grammar.put("rule_var_list", new Rule("rule_var_list", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("stmts").add(SEMI));
      grammar.put("stmts_help", new Rule("stmts_help", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(VARID));
      addVar(variants, end().add(UNDERSCORE));
      grammar.put("role", new Rule("role", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end(GrammarPackage.getRIGHT_HAND_SIDE()).add("wherebinds").add("exp").add(EQUAL));
      addVar(variants, end(GrammarPackage.getRIGHT_HAND_SIDE()).add("wherebinds").add("gdrhs"));
      grammar.put("rhs", new Rule("rhs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("close").add("decls_inst").add(VOCURLY));
      addVar(variants, end().add(CCURLY).add("decls_inst").add(OCURLY));
      grammar.put("decllist_inst", new Rule("decllist_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qvarsym_no_minus"));
      addVar(variants, end(GrammarPackage.getVARIABLE_OPERATION()).add(BACKQUOTE).add("qvarid").add(BACKQUOTE));
      grammar.put("qvaropm", new Rule("qvaropm", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(QVARSYM));
      grammar.put("qvarsym1", new Rule("qvarsym1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("opt_kind_sig").add("type").add(FAMILY).add(DATA));
      addVar(variants, many("tycl_hdr", end(GrammarPackage.getDATA_DECLARATION()).add("deriving").add("constrs"), end().add("deriving").add("gadt_constrlist").add("opt_kind_sig")).add("capi_ctype").add("data_or_newtype"));
      addVar(variants, many(TYPE, end().add("where_type_family").add("opt_kind_sig").add("type").add(FAMILY), end(GrammarPackage.getTYPE_SYNONYM()).add("ctypedoc").add(EQUAL).add("type")));
      grammar.put("ty_decl", new Rule("ty_decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, end(GrammarPackage.getOPERATOR_EXPRESSION()).add("exp10").add("qop").add("infixexp"));
      addVar(variants, end().add("exp10"));
      grammar.put("infixexp", new Rule("infixexp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("topdecl"));
      addVar(left, many(SEMI, end(), end().add("topdecl")).add("topdecls"));
      grammar.put("topdecls", new Rule("topdecls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("docnext"));
      addVar(variants, end().add("docnamed"));
      addVar(variants, end().add("docsection"));
      grammar.put("exp_doc", new Rule("exp_doc", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("header_body").add(WHERE).add("maybeexports").add("maybemodwarning").add("modid").add(MODULE).add("maybedocheader"));
      addVar(variants, end().add("header_body2"));
      grammar.put("header", new Rule("header", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add(FAMILY));
      grammar.put("opt_family", new Rule("opt_family", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CONID));
      grammar.put("conid", new Rule("conid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(DOCCOMMENTNEXT));
      grammar.put("docnext", new Rule("docnext", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many(VOCURLY, end().add("close").add("ty_fam_inst_eqns"), end().add("close").add(DOTDOT)));
      addVar(variants, many(OCURLY, end().add(CCURLY).add("ty_fam_inst_eqns"), end().add(CCURLY).add(DOTDOT)));
      grammar.put("ty_fam_inst_eqn_list", new Rule("ty_fam_inst_eqn_list", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CLOSE_PRAG).add(INTEGER).add(COLON).add(INTEGER).add(MINUS).add(INTEGER).add(COLON).add(INTEGER).add(STRING).add(GENERATED_PRAG));
      grammar.put("hpc_annot", new Rule("hpc_annot", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("role"));
      addVar(left, end().add("role").add("roles"));
      grammar.put("roles", new Rule("roles", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qtyconsym"));
      addVar(variants, end().add(BACKQUOTE).add("qtycon").add(BACKQUOTE));
      grammar.put("qtyconop", new Rule("qtyconop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("forall", end(GrammarPackage.getCONSTRUCTOR_DECLARATION()).add("maybe_docprev").add("constr_stuff"), end(GrammarPackage.getCONSTRUCTOR_DECLARATION()).add("maybe_docprev").add("constr_stuff").add(DARROW).add("context")).add("maybe_docnext"));
      grammar.put("constr", new Rule("constr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("docnext"));
      grammar.put("maybe_docnext", new Rule("maybe_docnext", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add(CLOSE_PRAG).add(SOURCE_PRAG));
      grammar.put("maybe_src", new Rule("maybe_src", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add(INTEGER));
      grammar.put("prec", new Rule("prec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("annotation"));
      addVar(variants, end().add(CLOSE_PRAG).add("qvar").add(NOVECT_PRAG));
      addVar(variants, end().add("role_annot"));
      addVar(variants, end().add(CLOSE_PRAG).add("warnings").add(WARNING_PRAG));
      addVar(variants, end().add(CPAREN).add("comma_types0").add(OPAREN).add(DEFAULT));
      addVar(variants, end().add("decl_no_th"));
      addVar(variants, end(GrammarPackage.getCLASS_DECLARATION()).add("cl_decl"));
      addVar(variants, many("gtycon", end().add(CLOSE_PRAG).add("gtycon").add(EQUAL), end().add(CLOSE_PRAG)).add(TYPE).add(VECT_SCALAR_PRAG));
      addVar(variants, end().add(CLOSE_PRAG).add("deprecations").add(DEPRECATED_PRAG));
      addVar(variants, end(GrammarPackage.getINSTANCE_DECLARATION()).add("inst_decl"));
      addVar(variants, end().add(CLOSE_PRAG).add("rules").add(RULES_PRAG));
      addVar(variants, many(VECT_PRAG, end().add(CLOSE_PRAG).add("gtycon").add(CLASS), end().add(CLOSE_PRAG).add("exp").add(EQUAL).add("qvar"), many("gtycon", end().add(CLOSE_PRAG).add("gtycon").add(EQUAL), end().add(CLOSE_PRAG)).add(TYPE)));
      addVar(variants, end().add("ty_decl"));
      addVar(variants, end().add("infixexp"));
      addVar(variants, end().add("fdecl").add(FOREIGN));
      addVar(variants, end().add("stand_alone_deriving"));
      grammar.put("topdecl", new Rule("topdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("atype").add(DCOLON));
      grammar.put("opt_asig", new Rule("opt_asig", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("sysdcon"));
      addVar(variants, end(GrammarPackage.getQ_CON()).add("qconid"));
      addVar(variants, end().add(CPAREN).add("qconsym").add(OPAREN));
      grammar.put("qcon", new Rule("qcon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qconsym"));
      addVar(variants, end().add(BACKQUOTE).add("qconid").add(BACKQUOTE));
      grammar.put("qconop", new Rule("qconop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("strings").add("namelist"));
      grammar.put("deprecation", new Rule("deprecation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("topdecls"));
      grammar.put("cvtopdecls", new Rule("cvtopdecls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(MINUS));
      addVar(variants, end().add("varsym_no_minus"));
      grammar.put("varsym", new Rule("varsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("importdecls").add("missing_module_keyword"));
      addVar(variants, end().add("importdecls").add(OCURLY));
      grammar.put("header_body2", new Rule("header_body2", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("decl"));
      addVar(variants, end().add("at_decl_cls"));
      addVar(variants, end().add("sigtypedoc").add(DCOLON).add("infixexp").add(DEFAULT));
      grammar.put("decl_cls", new Rule("decl_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("maybe_roles").add("oqtycon").add(ROLE).add(TYPE));
      grammar.put("role_annot", new Rule("role_annot", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("sigtype"));
      grammar.put("inst_type", new Rule("inst_type", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qcname_ext"));
      addVar(left, end().add("qcname_ext").add(COMMA).add("qcnames"));
      grammar.put("qcnames", new Rule("qcnames", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("expdoclist").add("exp_doc"));
      grammar.put("expdoclist", new Rule("expdoclist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(DOCCOMMENTPREV));
      grammar.put("docprev", new Rule("docprev", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("warning"));
      addVar(left, many(SEMI, end(), end().add("warning")).add("warnings"));
      grammar.put("warnings", new Rule("warnings", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("splice_exp"));
      addVar(variants, end().add("decl_no_th"));
      grammar.put("decl", new Rule("decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CPAREN).add("ctype").add(DCOLON).add("varid").add(OPAREN));
      addVar(variants, end().add("varid"));
      grammar.put("rule_var", new Rule("rule_var", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("type"));
      addVar(variants, end().add("type").add(DARROW).add("context"));
      grammar.put("tycl_hdr", new Rule("tycl_hdr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("vars0").add("varid"));
      grammar.put("vars0", new Rule("vars0", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("varids0").add(RARROW).add("varids0"));
      grammar.put("fd", new Rule("fd", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(DOCSECTION));
      grammar.put("docsection", new Rule("docsection", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end(GrammarPackage.getQ_VAR()).add("qvarid"));
      addVar(variants, many(OPAREN, end(GrammarPackage.getQ_VAR()).add(CPAREN).add("qvarsym1"), end(GrammarPackage.getQ_VAR_SYM()).add(CPAREN).add("varsym")));
      grammar.put("qvar", new Rule("qvar", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("close").add("top").add("missing_module_keyword"));
      addVar(variants, end().add(CCURLY).add("top").add(OCURLY));
      grammar.put("body2", new Rule("body2", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qtycon"));
      addVar(variants, end().add(CBRACK).add("kind").add(OBRACK));
      addVar(variants, many(OPAREN, end().add(CPAREN).add("comma_kinds1").add(COMMA).add("kind"), end().add(CPAREN)));
      grammar.put("pkind", new Rule("pkind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("ctype").add(EQUAL).add("type"));
      grammar.put("ty_fam_inst_eqn", new Rule("ty_fam_inst_eqn", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("close").add("decls").add(VOCURLY));
      addVar(variants, end().add(CCURLY).add("decls").add(OCURLY));
      grammar.put("decllist", new Rule("decllist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(DOTDOT));
      addVar(variants, many("fbind", end(), end().add("fbinds1").add(COMMA)));
      grammar.put("fbinds1", new Rule("fbinds1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("name_boolformula_and", end(), end().add("name_boolformula").add(VBAR)));
      grammar.put("name_boolformula", new Rule("name_boolformula", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qvarsym"));
      addVar(variants, end(GrammarPackage.getVARIABLE_OPERATION()).add(BACKQUOTE).add("qvarid").add(BACKQUOTE));
      grammar.put("qvarop", new Rule("qvarop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("fd"));
      addVar(left, end().add("fd").add(COMMA).add("fds1"));
      grammar.put("fds1", new Rule("fds1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end(GrammarPackage.getBIND_STATEMENT()).add("exp").add(LARROW).add("bindpat"));
      addVar(variants, end(GrammarPackage.getEXPRESSION_STATEMENT()).add("exp"));
      addVar(variants, end(GrammarPackage.getLET_STATEMENT()).add("binds").add(LET));
      grammar.put("qual", new Rule("qual", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end(GrammarPackage.getMODULE_NAME()).add(CONID));
      addVar(variants, end(GrammarPackage.getMODULE_NAME()).add(QCONID));
      grammar.put("modid", new Rule("modid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, many("texp", end(), end().add("exp").add(DOTDOT), end().add("exp").add(DOTDOT).add("exp").add(COMMA), end().add("flattenedpquals").add(VBAR)));
      addVar(variants, end().add("lexps"));
      grammar.put("parr", new Rule("parr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("sysdcon"));
      addVar(variants, end().add("conid"));
      addVar(variants, end().add(CPAREN).add("consym").add(OPAREN));
      grammar.put("con", new Rule("con", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("close").add("top").add(VOCURLY));
      addVar(variants, end().add(CCURLY).add("top").add(OCURLY));
      grammar.put("body", new Rule("body", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("squals", end(), end().add("pquals").add(VBAR)));
      grammar.put("pquals", new Rule("pquals", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(VCCURLY));
      grammar.put("close", new Rule("close", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("pquals"));
      grammar.put("flattenedpquals", new Rule("flattenedpquals", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, end(GrammarPackage.getAPPLICATION()).add("aexp").add("fexp"));
      addVar(variants, end().add("aexp"));
      grammar.put("fexp", new Rule("fexp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("ctype", end(), end().add("comma_types1").add(COMMA)));
      grammar.put("comma_types1", new Rule("comma_types1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qcon"));
      addVar(variants, end().add("qvar"));
      grammar.put("qcname", new Rule("qcname", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(LABEL));
      addVar(variants, end().add(JAVASCRIPTCALLCONV));
      addVar(variants, end().add(HIDING));
      addVar(variants, end().add(DYNAMIC));
      addVar(variants, end().add(AS));
      addVar(variants, end().add(CAPICONV));
      addVar(variants, end().add(QUALIFIED));
      addVar(variants, end().add(CCALLCONV));
      addVar(variants, end().add(PRIMCALLCONV));
      addVar(variants, end().add(STDCALLCONV));
      addVar(variants, end().add(EXPORT));
      addVar(variants, end().add(GROUP));
      grammar.put("special_id", new Rule("special_id", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("comma_types1"));
      grammar.put("comma_types0", new Rule("comma_types0", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("exportlist1"));
      addVar(variants, end().add("expdoclist").add(COMMA).add("expdoclist"));
      grammar.put("exportlist", new Rule("exportlist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many(LAM, end().add("altslist").add(LCASE), end(GrammarPackage.getLAMBDA_EXPRESSION()).add("exp").add(RARROW).add("opt_asig").add("apats").add("apat")));
      addVar(variants, end().add("exp").add(RARROW).add("aexp").add(PROC));
      addVar(variants, end().add("exp").add("hpc_annot"));
      addVar(variants, end(GrammarPackage.getCASE_EXPRESSION()).add("altslist").add(OF).add("exp").add(CASE));
      addVar(variants, end().add("fexp"));
      addVar(variants, end().add("exp").add("scc_annot"));
      addVar(variants, end(GrammarPackage.getDO_EXPRESSION()).add("stmtlist").add(DO));
      addVar(variants, end().add("fexp").add(MINUS));
      addVar(variants, end(GrammarPackage.getLET_EXPRESSION()).add("exp").add(IN).add("binds").add(LET));
      addVar(variants, end().add("stmtlist").add(MDO));
      addVar(variants, end().add("exp").add(CLOSE_PRAG).add(STRING).add(CORE_PRAG));
      addVar(variants, many(IF, end().add("ifgdpats"), end().add("exp").add(ELSE).add("optSemi").add("exp").add(THEN).add("optSemi").add("exp")));
      grammar.put("exp10", new Rule("exp10", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, many(WHERE, end().add("close").add("gadt_constrs").add(VOCURLY), end().add(CCURLY).add("gadt_constrs").add(OCURLY)));
      grammar.put("gadt_constrlist", new Rule("gadt_constrlist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("exp").add(EQUAL).add("ipvar"));
      grammar.put("dbind", new Rule("dbind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("kind").add(DCOLON));
      grammar.put("opt_kind_sig", new Rule("opt_kind_sig", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("decllist_cls").add(WHERE));
      grammar.put("where_cls", new Rule("where_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("exp", end(), end().add("texp").add(RARROW)));
      addVar(variants, end().add("qop").add("infixexp"));
      addVar(variants, end().add("infixexp").add("qopm"));
      grammar.put("texp", new Rule("texp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("exp").add(RARROW).add("guardquals").add(VBAR));
      grammar.put("gdpat", new Rule("gdpat", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("varsym"));
      addVar(variants, end(GrammarPackage.getVARIABLE_OPERATION()).add(BACKQUOTE).add("varid").add(BACKQUOTE));
      grammar.put("varop", new Rule("varop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, end().add("optSemi").add("gdpat").add("gdpatssemi"));
      addVar(variants, end().add("optSemi").add("gdpat"));
      grammar.put("gdpatssemi", new Rule("gdpatssemi", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("deprecation"));
      addVar(left, many(SEMI, end(), end().add("deprecation")).add("deprecations"));
      grammar.put("deprecations", new Rule("deprecations", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many(OBRACK, end().add(CBRACK).add(INTEGER), many(TILDE, end().add(CBRACK).add(INTEGER), end().add(CBRACK))));
      grammar.put("rule_explicit_activation", new Rule("rule_explicit_activation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("exp").add(RARROW));
      addVar(variants, end().add("gdpats"));
      grammar.put("ralt", new Rule("ralt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("opt_kind_sig").add("type").add("opt_family").add(DATA));
      addVar(variants, many(TYPE, end().add("ty_fam_inst_eqn"), end().add("ty_fam_inst_eqn").add(INSTANCE), end().add("opt_kind_sig").add("type").add(FAMILY), end().add("opt_kind_sig").add("type")));
      grammar.put("at_decl_cls", new Rule("at_decl_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many(VOCURLY, end().add("close").add("alts"), end().add("close")));
      addVar(variants, many(OCURLY, end().add(CCURLY).add("alts"), end().add(CCURLY)));
      grammar.put("altslist", new Rule("altslist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(DOCCOMMENTNEXT));
      grammar.put("moduleheader", new Rule("moduleheader", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end(GrammarPackage.getWHERE_BINDINGS()).add("binds").add(WHERE));
      grammar.put("wherebinds", new Rule("wherebinds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(QQUASIQUOTE));
      addVar(variants, end().add(QUASIQUOTE));
      grammar.put("quasiquote", new Rule("quasiquote", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CPAREN).add("varsym").add(OPAREN));
      addVar(variants, end().add("varid"));
      grammar.put("var", new Rule("var", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CBRACK).add("stringlist").add(OBRACK));
      addVar(variants, end().add(STRING));
      grammar.put("strings", new Rule("strings", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("fds1").add(VBAR));
      grammar.put("fds", new Rule("fds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("sigtype", end(), end().add("sigtypes1").add(COMMA)));
      grammar.put("sigtypes1", new Rule("sigtypes1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("stmt"));
      grammar.put("maybe_stmt", new Rule("maybe_stmt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("tv_bndrs").add("tv_bndr"));
      grammar.put("tv_bndrs", new Rule("tv_bndrs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("ops").add("prec").add("infix"));
      addVar(variants, end().add(CLOSE_PRAG).add("qvar").add("activation").add(INLINE_PRAG));
      addVar(variants, many(SPEC_PRAG, end().add(CLOSE_PRAG).add("inst_type").add(INSTANCE), end().add(CLOSE_PRAG).add("sigtypes1").add(DCOLON).add("qvar").add("activation")));
      addVar(variants, end().add("sigtypedoc").add(DCOLON).add("sig_vars").add(COMMA).add("var"));
      addVar(variants, end().add(CLOSE_PRAG).add("sigtypes1").add(DCOLON).add("qvar").add("activation").add(SPEC_INLINE_PRAG));
      addVar(variants, end().add("sigtypedoc").add(DCOLON).add("infixexp"));
      addVar(variants, end().add(CLOSE_PRAG).add("name_boolformula_opt").add(MINIMAL_PRAG));
      grammar.put("sigdecl", new Rule("sigdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many(THEN, many("exp", end(), end().add("exp").add(BY)), many(GROUP, end().add("exp").add(USING), end().add("exp").add(USING).add("exp").add(BY))));
      grammar.put("transformqual", new Rule("transformqual", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("aexp").add(BANG));
      addVar(variants, end().add("exp"));
      grammar.put("pat", new Rule("pat", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end(GrammarPackage.getCASE_ALTERNATIVE()).add("alt_rhs").add("opt_sig").add("pat"));
      grammar.put("alt", new Rule("alt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add(DOT).add("rule_var_list").add(FORALL));
      grammar.put("rule_forall", new Rule("rule_forall", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(JAVASCRIPTCALLCONV));
      addVar(variants, end().add(CAPICONV));
      addVar(variants, end().add(CCALLCONV));
      addVar(variants, end().add(PRIMCALLCONV));
      addVar(variants, end().add(STDCALLCONV));
      grammar.put("callconv", new Rule("callconv", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("sigtypedoc").add(DCOLON).add("var"));
      addVar(variants, end().add("sigtypedoc").add(DCOLON).add("var").add(STRING));
      grammar.put("fspec", new Rule("fspec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("consym"));
      addVar(variants, end().add(BACKQUOTE).add("conid").add(BACKQUOTE));
      grammar.put("conop", new Rule("conop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("cvtopdecls"));
      addVar(variants, many("importdecls", end(), end().add("cvtopdecls").add(SEMI)));
      grammar.put("top", new Rule("top", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("export_subspec").add("qcname_ext"));
      addVar(variants, end().add("modid").add(MODULE));
      addVar(variants, end().add("qcon").add(PATTERN));
      grammar.put("export", new Rule("export", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end(GrammarPackage.getMODULE_EXPORTS()).add(CPAREN).add("exportlist").add(OPAREN));
      grammar.put("maybeexports", new Rule("maybeexports", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("gdrh"));
      addVar(left, end().add("gdrh").add("gdrhs"));
      grammar.put("gdrhs", new Rule("gdrhs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qual"));
      addVar(variants, end().add("stmtlist").add(REC));
      grammar.put("stmt", new Rule("stmt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("ctype").add(DOT).add("tv_bndrs").add(FORALL));
      addVar(variants, end().add("type").add(DCOLON).add("ipvar"));
      addVar(variants, end().add("type"));
      addVar(variants, end().add("ctype").add(DARROW).add("context"));
      grammar.put("ctype", new Rule("ctype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("name_var"));
      addVar(variants, end().add(CPAREN).add("name_boolformula").add(OPAREN));
      grammar.put("name_boolformula_atom", new Rule("name_boolformula_atom", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("fspec").add("callconv").add(EXPORT));
      addVar(variants, many("callconv", end().add("fspec"), end().add("fspec").add("safety")).add(IMPORT));
      grammar.put("fdecl", new Rule("fdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("inst_type", end(), end().add("inst_types1").add(COMMA)));
      grammar.put("inst_types1", new Rule("inst_types1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CPABRACK).add(OPABRACK));
      addVar(variants, end().add("oqtycon"));
      addVar(variants, end().add(CBRACK).add(OBRACK));
      addVar(variants, many(OPAREN, end().add(CPAREN).add(TILDEHSH), end().add(CPAREN).add("commas"), end().add(CPAREN).add(RARROW)));
      addVar(variants, end().add(CUBXPAREN).add("commas").add(OUBXPAREN));
      grammar.put("ntgtycon", new Rule("ntgtycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(left, many(SEMI, end(), end().add("decl_cls")).add("decls_cls"));
      addVar(variants, end().add("decl_cls"));
      grammar.put("decls_cls", new Rule("decls_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("tycl_hdr", end(GrammarPackage.getDATA_DECLARATION()).add("deriving").add("constrs"), end().add("deriving").add("gadt_constrlist").add("opt_kind_sig")).add("capi_ctype").add("data_or_newtype"));
      addVar(variants, end().add("ty_fam_inst_eqn").add(TYPE));
      grammar.put("at_decl_inst", new Rule("at_decl_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end(GrammarPackage.getIMPORT_AS_PART()).add("modid").add(AS));
      grammar.put("maybeas", new Rule("maybeas", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("tyvar"));
      addVar(variants, end().add("pkind"));
      addVar(variants, end().add(STAR));
      addVar(variants, end().add(CPAREN).add("kind").add(OPAREN));
      grammar.put("akind", new Rule("akind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("roles"));
      grammar.put("maybe_roles", new Rule("maybe_roles", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many(SCC_PRAG, end().add(CLOSE_PRAG).add(VARID), end().add(CLOSE_PRAG).add(STRING)));
      grammar.put("scc_annot", new Rule("scc_annot", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("where_cls").add("fds").add("tycl_hdr").add(CLASS));
      grammar.put("cl_decl", new Rule("cl_decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("conid"));
      addVar(variants, end().add(PREFIXQCONSYM));
      addVar(variants, end().add(QCONID));
      grammar.put("qconid", new Rule("qconid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end(GrammarPackage.getIMPORT()).add("maybeimpspec").add("maybeas").add("modid").add("maybe_pkg").add("optqualified").add("maybe_safe").add("maybe_src").add(IMPORT));
      grammar.put("importdecl", new Rule("importdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("commas_tup_tail").add("texp"));
      addVar(variants, end().add("tup_tail").add("commas"));
      grammar.put("tup_exprs", new Rule("tup_exprs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(DOCCOMMENTNAMED));
      grammar.put("docnamed", new Rule("docnamed", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(left, end().add("tyvar").add("varids0"));
      grammar.put("varids0", new Rule("varids0", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("ty_fam_inst_eqn"));
      addVar(left, many(SEMI, end(), end().add("ty_fam_inst_eqn")).add("ty_fam_inst_eqns"));
      grammar.put("ty_fam_inst_eqns", new Rule("ty_fam_inst_eqns", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qtycon"));
      addVar(variants, many(OPAREN, end().add(CPAREN).add("qtyconsym"), end().add(CPAREN).add(TILDE)));
      grammar.put("oqtycon", new Rule("oqtycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many(ANN_PRAG, end().add(CLOSE_PRAG).add("aexp").add("name_var"), end().add(CLOSE_PRAG).add("aexp").add(MODULE), end().add(CLOSE_PRAG).add("aexp").add("tycon").add(TYPE)));
      grammar.put("annotation", new Rule("annotation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("guardquals1"));
      grammar.put("guardquals", new Rule("guardquals", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(INFIX));
      addVar(variants, end().add(INFIXL));
      addVar(variants, end().add(INFIXR));
      grammar.put("infix", new Rule("infix", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("name_boolformula"));
      grammar.put("name_boolformula_opt", new Rule("name_boolformula_opt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("pat", end().add("pat").add(EQUAL), many("pat", end(), end().add("where_decls")).add(LARROW)).add(PATTERN));
      grammar.put("pattern_synonym_decl", new Rule("pattern_synonym_decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("ctypedoc"));
      grammar.put("sigtypedoc", new Rule("sigtypedoc", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CONSYM));
      addVar(variants, end().add(MINUS));
      addVar(variants, end().add(STAR));
      addVar(variants, end().add(VARSYM));
      grammar.put("tyconsym", new Rule("tyconsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many(OBRACK, end().add(CBRACK).add(INTEGER), end().add(CBRACK).add(INTEGER).add(TILDE)));
      grammar.put("explicit_activation", new Rule("explicit_activation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, end().add("op").add(COMMA).add("ops"));
      addVar(variants, end().add("op"));
      grammar.put("ops", new Rule("ops", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("docprev"));
      grammar.put("maybe_docprev", new Rule("maybe_docprev", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many(WHERE, end().add("close").add("decls").add(VOCURLY), end().add(CCURLY).add("decls").add(OCURLY)));
      grammar.put("where_decls", new Rule("where_decls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("where_inst").add("inst_type").add("overlap_pragma").add(INSTANCE));
      addVar(variants, many("tycl_hdr", end().add("deriving").add("constrs"), end().add("deriving").add("gadt_constrlist").add("opt_kind_sig")).add("capi_ctype").add(INSTANCE).add("data_or_newtype"));
      addVar(variants, end().add("ty_fam_inst_eqn").add(INSTANCE).add(TYPE));
      grammar.put("inst_decl", new Rule("inst_decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("sigtype").add(DCOLON).add("con_list"));
      addVar(variants, end().add("sigtype").add(DCOLON).add(CCURLY).add("fielddecls").add(OCURLY).add("oqtycon"));
      grammar.put("gadt_constr", new Rule("gadt_constr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("conop"));
      addVar(variants, end().add("varop"));
      grammar.put("op", new Rule("op", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("var"));
      addVar(left, end().add("var").add(COMMA).add("sig_vars"));
      grammar.put("sig_vars", new Rule("sig_vars", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qvarsym1"));
      addVar(variants, end().add("varsym"));
      grammar.put("qvarsym", new Rule("qvarsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("tyvarid"));
      grammar.put("tyvar", new Rule("tyvar", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("akind"));
      addVar(left, end().add("akind").add("bkind"));
      grammar.put("bkind", new Rule("bkind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, many(DERIVING, end().add("qtycon"), many(OPAREN, end().add(CPAREN).add("inst_types1"), end().add(CPAREN))));
      grammar.put("deriving", new Rule("deriving", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("qvar", end(GrammarPackage.getFIELD_UPDATE()), end(GrammarPackage.getFIELD_UPDATE()).add("texp").add(EQUAL)));
      grammar.put("fbind", new Rule("fbind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("sigtype").add(DCOLON));
      grammar.put("opt_sig", new Rule("opt_sig", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("moduleheader"));
      grammar.put("maybedocheader", new Rule("maybedocheader", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qvaropm"));
      addVar(variants, end().add("qconop"));
      grammar.put("qopm", new Rule("qopm", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("impspec"));
      grammar.put("maybeimpspec", new Rule("maybeimpspec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("docnext"));
      addVar(variants, end().add("docnamed"));
      addVar(variants, end().add("docprev"));
      addVar(variants, end().add("docsection"));
      grammar.put("docdecld", new Rule("docdecld", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(left, many(SEMI, end(), end().add("decl_inst")).add("decls_inst"));
      addVar(variants, end().add("decl_inst"));
      grammar.put("decls_inst", new Rule("decls_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end(GrammarPackage.getGUARD()).add("exp").add(EQUAL).add("guardquals").add(VBAR));
      grammar.put("gdrh", new Rule("gdrh", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("decllist"));
      addVar(variants, end().add("close").add("dbinds").add(VOCURLY));
      addVar(variants, end().add(CCURLY).add("dbinds").add(OCURLY));
      grammar.put("binds", new Rule("binds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("quasiquote"));
      addVar(variants, end().add(INTEGER));
      addVar(variants, many("ctype", end(GrammarPackage.getLIST_TYPE()).add(CBRACK).add("comma_types1").add(COMMA), end(GrammarPackage.getLIST_TYPE()).add(CBRACK)).add(OBRACK));
      addVar(variants, end().add(CCURLY).add("fielddecls").add(OCURLY));
      addVar(variants, many(SIMPLEQUOTE, end().add("var"), end().add("qcon"), end().add(CBRACK).add("comma_types0").add(OBRACK), end().add(CPAREN).add("comma_types1").add(COMMA).add("ctype").add(OPAREN)));
      addVar(variants, end(GrammarPackage.getTYPE_VARIABLE()).add("tyvar"));
      addVar(variants, end().add(CPABRACK).add("ctype").add(OPABRACK));
      addVar(variants, end().add(IDESCAPE));
      addVar(variants, end().add("atype").add("strict_mark"));
      addVar(variants, end().add(STRING));
      addVar(variants, many(OPAREN, end(GrammarPackage.getTUPLE_TYPE()).add(CPAREN), many("ctype", end(GrammarPackage.getTUPLE_TYPE()).add(CPAREN), end(GrammarPackage.getTUPLE_TYPE()).add(CPAREN).add("comma_types1").add(COMMA), end(GrammarPackage.getTUPLE_TYPE()).add(CPAREN).add("kind").add(DCOLON))));
      addVar(variants, many(OUBXPAREN, end().add(CUBXPAREN), end().add(CUBXPAREN).add("comma_types1")));
      addVar(variants, end().add(CPAREN).add("exp").add(PARENESCAPE));
      addVar(variants, end(GrammarPackage.getTYPE_VARIABLE()).add("ntgtycon"));
      grammar.put("atype", new Rule("atype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("ctype"));
      grammar.put("sigtype", new Rule("sigtype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("tycon"));
      addVar(variants, end().add(PREFIXQCONSYM));
      addVar(variants, end().add(QCONID));
      grammar.put("qtycon", new Rule("qtycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add(CLOSE_PRAG).add(OVERLAPPING));
      addVar(variants, end().add(CLOSE_PRAG).add(OVERLAPPABLE));
      addVar(variants, end().add(CLOSE_PRAG).add(OVERLAPS));
      addVar(variants, end().add(CLOSE_PRAG).add(INCOHERENT));
      grammar.put("overlap_pragma", new Rule("overlap_pragma", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("con"));
      addVar(variants, end().add("var"));
      grammar.put("name_var", new Rule("name_var", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("close").add("cvtopdecls0").add(VOCURLY));
      addVar(variants, end().add(CCURLY).add("cvtopdecls0").add(OCURLY));
      grammar.put("cvtopbody", new Rule("cvtopbody", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("tup_tail").add("commas"));
      grammar.put("commas_tup_tail", new Rule("commas_tup_tail", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CBRACK).add(OBRACK));
      addVar(variants, many(OPAREN, end().add(CPAREN).add("commas"), end().add(CPAREN)));
      addVar(variants, many(OUBXPAREN, end().add(CUBXPAREN).add("commas"), end().add(CUBXPAREN)));
      grammar.put("sysdcon", new Rule("sysdcon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("bkind", end(), end().add("kind").add(RARROW)));
      grammar.put("kind", new Rule("kind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end(GrammarPackage.getPATTERN()).add("aexp").add(BANG));
      addVar(variants, end(GrammarPackage.getPATTERN()).add("aexp"));
      grammar.put("apat", new Rule("apat", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(DOT));
      addVar(variants, end().add(BACKQUOTE).add("tyvarid").add(BACKQUOTE));
      grammar.put("tyvarop", new Rule("tyvarop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("decl"));
      addVar(variants, end().add("at_decl_inst"));
      grammar.put("decl_inst", new Rule("decl_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("fbinds1"));
      grammar.put("fbinds", new Rule("fbinds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("ctypedoc").add(DOT).add("tv_bndrs").add(FORALL));
      addVar(variants, end().add("type").add(DCOLON).add("ipvar"));
      addVar(variants, end().add("ctypedoc").add(DARROW).add("context"));
      addVar(variants, end().add("typedoc"));
      grammar.put("ctypedoc", new Rule("ctypedoc", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end(GrammarPackage.getMODULE()).add("body2"));
      addVar(variants, end(GrammarPackage.getMODULE()).add("body").add(WHERE).add("maybeexports").add("maybemodwarning").add("modid").add(MODULE).add("maybedocheader"));
      grammar.put("module", new Rule("module", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CONSYM));
      addVar(variants, end().add(COLON));
      grammar.put("consym", new Rule("consym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("rhs").add("aexp").add(BANG));
      addVar(variants, end().add("docdecl"));
      addVar(variants, end(GrammarPackage.getVALUE_DEFINITION()).add("rhs").add("opt_sig").add("infixexp"));
      addVar(variants, end(GrammarPackage.getSIGNATURE_DECLARATION()).add("sigdecl"));
      addVar(variants, end().add("pattern_synonym_decl"));
      grammar.put("decl_no_th", new Rule("decl_no_th", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("ty_fam_inst_eqn_list").add(WHERE));
      grammar.put("where_type_family", new Rule("where_type_family", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("consym"));
      addVar(variants, end().add(QCONSYM));
      grammar.put("qconsym", new Rule("qconsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("aexp2"));
      grammar.put("acmd", new Rule("acmd", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end(GrammarPackage.getFIELD_DECLARATION()).add("maybe_docprev").add("ctype").add(DCOLON).add("sig_vars").add("maybe_docnext"));
      grammar.put("fielddecl", new Rule("fielddecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("btype", end(), end().add("btype").add("conop")));
      grammar.put("constr_stuff", new Rule("constr_stuff", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add(STRING));
      grammar.put("maybe_pkg", new Rule("maybe_pkg", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(VARSYM));
      addVar(variants, end().add("special_sym"));
      grammar.put("varsym_no_minus", new Rule("varsym_no_minus", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(BANG));
      addVar(variants, end().add(STAR));
      addVar(variants, end().add(DOT));
      grammar.put("special_sym", new Rule("special_sym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("expdoclist", end(), many("expdoclist", end(), end().add("exportlist1").add(COMMA)).add("export")));
      grammar.put("exportlist1", new Rule("exportlist1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(INTERRUPTIBLE));
      addVar(variants, end().add(SAFE));
      addVar(variants, end().add(UNSAFE));
      addVar(variants, end().add(FORALL));
      addVar(variants, end().add(VARID));
      addVar(variants, end().add("special_id"));
      addVar(variants, end().add(FAMILY));
      addVar(variants, end().add(ROLE));
      grammar.put("varid", new Rule("varid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(left, end().add("importdecl").add(SEMI).add("importdecls"));
      addVar(variants, end().add("importdecl"));
      grammar.put("importdecls", new Rule("importdecls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add(QUALIFIED));
      grammar.put("optqualified", new Rule("optqualified", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("strings").add("namelist"));
      grammar.put("warning", new Rule("warning", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("btype", end(), many("type", end(), end().add("docprev")).add("tyvarop"), many("type", end(), end().add("docprev")).add("qtyconop"), many("docprev", end(), end().add("ctypedoc").add(RARROW)), end(GrammarPackage.getFUNCTION_TYPE()).add("ctypedoc").add(RARROW), end().add("btype").add(TILDE), many(SIMPLEQUOTE, end().add("type").add("varop"), end().add("type").add("qconop"))));
      grammar.put("typedoc", new Rule("typedoc", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("aexp").add(AT).add("qvar"));
      addVar(variants, end().add("aexp").add(TILDE));
      addVar(variants, end().add("aexp1"));
      grammar.put("aexp", new Rule("aexp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(STRING));
      addVar(left, end().add(STRING).add(COMMA).add("stringlist"));
      grammar.put("stringlist", new Rule("stringlist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("close").add("gdpatssemi"));
      addVar(variants, end().add(CCURLY).add("gdpatssemi").add(OCURLY));
      grammar.put("ifgdpats", new Rule("ifgdpats", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("btype", end(), end().add("type").add("tyvarop"), end().add("type").add("qtyconop"), end(GrammarPackage.getFUNCTION_TYPE()).add("ctype").add(RARROW), end().add("btype").add(TILDE), many(SIMPLEQUOTE, end().add("type").add("varop"), end().add("type").add("qconop"))));
      grammar.put("type", new Rule("type", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, end().add(COMMA).add("commas"));
      addVar(variants, end().add(COMMA));
      grammar.put("commas", new Rule("commas", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("docdecld"));
      grammar.put("docdecl", new Rule("docdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CONID));
      grammar.put("tycon", new Rule("tycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("apats").add("apat"));
      grammar.put("apats", new Rule("apats", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qvarsym1"));
      addVar(variants, end().add("varsym_no_minus"));
      grammar.put("qvarsym_no_minus", new Rule("qvarsym_no_minus", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(DUPIPVARID));
      grammar.put("ipvar", new Rule("ipvar", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(PREFIXQVARSYM));
      addVar(variants, end().add("varid"));
      addVar(variants, end().add(QVARID));
      grammar.put("qvarid", new Rule("qvarid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("tyconsym"));
      addVar(variants, end().add(QCONSYM));
      addVar(variants, end().add(QVARSYM));
      grammar.put("qtyconsym", new Rule("qtyconsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(INTERRUPTIBLE));
      addVar(variants, end().add(SAFE));
      addVar(variants, end().add(UNSAFE));
      addVar(variants, end().add(VARID));
      addVar(variants, end().add("special_id"));
      grammar.put("tyvarid", new Rule("tyvarid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("atype"));
      addVar(left, end(GrammarPackage.getAPPLICATION_TYPE()).add("atype").add("btype"));
      grammar.put("btype", new Rule("btype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CPAREN).add("exportlist").add(OPAREN).add(HIDING));
      addVar(variants, end().add(CPAREN).add("exportlist").add(OPAREN));
      grammar.put("impspec", new Rule("impspec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("inst_type").add("overlap_pragma").add(INSTANCE).add(DERIVING));
      grammar.put("stand_alone_deriving", new Rule("stand_alone_deriving", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("wherebinds").add("ralt"));
      grammar.put("alt_rhs", new Rule("alt_rhs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add(DOT).add("tv_bndrs").add(FORALL));
      grammar.put("forall", new Rule("forall", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("con", end(), end().add("con_list").add(COMMA)));
      grammar.put("con_list", new Rule("con_list", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(NEWTYPE));
      addVar(variants, end().add(DATA));
      grammar.put("data_or_newtype", new Rule("data_or_newtype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("infixexp", end(), end().add("exp").add(RRARROWTAIL), end().add("exp").add(RARROWTAIL), end().add("exp").add(LLARROWTAIL), end().add("exp").add(LARROWTAIL), end().add("sigtype").add(DCOLON)));
      grammar.put("exp", new Rule("exp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CPAREN).add("exp").add(PARENTYESCAPE));
      addVar(variants, end().add(IDESCAPE));
      addVar(variants, end().add(IDTYESCAPE));
      addVar(variants, end().add(CPAREN).add("exp").add(PARENESCAPE));
      grammar.put("splice_exp", new Rule("splice_exp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("aexp").add(BANG));
      addVar(variants, end().add("exp"));
      grammar.put("bindpat", new Rule("bindpat", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add(CLOSE_PRAG).add("strings").add(DEPRECATED_PRAG));
      addVar(variants, end().add(CLOSE_PRAG).add("strings").add(WARNING_PRAG));
      grammar.put("maybemodwarning", new Rule("maybemodwarning", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("name_boolformula_atom", end(), end().add("name_boolformula_and").add(COMMA)));
      grammar.put("name_boolformula_and", new Rule("name_boolformula_and", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, many(STRING, end().add(CLOSE_PRAG).add(STRING), end().add(CLOSE_PRAG)).add(CTYPE));
      grammar.put("capi_ctype", new Rule("capi_ctype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, many("gadt_constr", end(), end().add("gadt_constrs").add(SEMI)));
      grammar.put("gadt_constrs", new Rule("gadt_constrs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("close").add("stmts").add(VOCURLY));
      addVar(variants, end().add(CCURLY).add("stmts").add(OCURLY));
      grammar.put("stmtlist", new Rule("stmtlist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add(SAFE));
      grammar.put("maybe_safe", new Rule("maybe_safe", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("close").add("decls_cls").add(VOCURLY));
      addVar(variants, end().add(CCURLY).add("decls_cls").add(OCURLY));
      grammar.put("decllist_cls", new Rule("decllist_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("dbind"));
      addVar(left, many(SEMI, end(), end().add("dbind")).add("dbinds"));
      grammar.put("dbinds", new Rule("dbinds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("stmts_help").add("stmt"));
      addVar(variants, end().add("stmts").add(SEMI));
      grammar.put("stmts", new Rule("stmts", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qcname").add(TYPE));
      addVar(variants, end().add("qcname"));
      grammar.put("qcname_ext", new Rule("qcname_ext", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CLOSEQUOTE).add("cvtopbody").add(OPENDECQUOTE));
      addVar(variants, end().add("quasiquote"));
      addVar(variants, end().add(INTEGER));
      addVar(variants, end().add(RATIONAL));
      addVar(variants, end().add(CBRACK).add("list").add(OBRACK));
      addVar(variants, end().add(CLOSETEXPQUOTE).add("exp").add(OPENTEXPQUOTE));
      addVar(variants, end(GrammarPackage.getQ_NAME_EXPRESSION()).add("qcname"));
      addVar(variants, end().add(UNDERSCORE));
      addVar(variants, many(SIMPLEQUOTE, end().add("qcon"), end().add("qvar")));
      addVar(variants, end().add(CLOSEQUOTE).add("ctype").add(OPENTYPQUOTE));
      addVar(variants, end().add(CLOSEQUOTE).add("exp").add(OPENEXPQUOTE));
      addVar(variants, end().add(CPABRACK).add("parr").add(OPABRACK));
      addVar(variants, many(TYQUOTE, end().add("gtycon"), end().add("tyvar")));
      addVar(variants, end().add("ipvar"));
      addVar(variants, end().add("literal"));
      addVar(variants, end().add("splice_exp"));
      addVar(variants, many(OPAREN, end(GrammarPackage.getPARENTHESIS_EXPRESSION()).add(CPAREN).add("texp"), end().add(CPAREN).add("tup_exprs")));
      addVar(variants, end().add(CLOSEQUOTE).add("infixexp").add(OPENPATQUOTE));
      addVar(variants, many(OUBXPAREN, end().add(CUBXPAREN).add("texp"), end().add(CUBXPAREN).add("tup_exprs")));
      addVar(variants, end().add(CPARENBAR).add("cmdargs").add("aexp2").add(OPARENBAR));
      grammar.put("aexp2", new Rule("aexp2", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("aexp2"));
      addVar(left, end().add(CCURLY).add("fbinds").add(OCURLY).add("aexp1"));
      grammar.put("aexp1", new Rule("aexp1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(CPAREN).add(OPAREN));
      addVar(variants, end().add(CUBXPAREN).add(OUBXPAREN));
      addVar(variants, end().add("ntgtycon"));
      grammar.put("gtycon", new Rule("gtycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("alts1"));
      addVar(variants, end().add("alts").add(SEMI));
      grammar.put("alts", new Rule("alts", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("kind", end(), end().add("comma_kinds1").add(COMMA)));
      grammar.put("comma_kinds1", new Rule("comma_kinds1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("rule_explicit_activation"));
      grammar.put("rule_activation", new Rule("rule_activation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("exp").add(EQUAL).add("infixexp").add("rule_forall").add("rule_activation").add(STRING));
      grammar.put("rule", new Rule("rule", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(left, end().add("acmd").add("cmdargs"));
      grammar.put("cmdargs", new Rule("cmdargs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, many(OPAREN, end().add(CPAREN).add("qcnames"), end().add(CPAREN), end().add(CPAREN).add(DOTDOT)));
      grammar.put("export_subspec", new Rule("export_subspec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("explicit_activation"));
      grammar.put("activation", new Rule("activation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("constrs1").add(EQUAL).add("maybe_docnext"));
      grammar.put("constrs", new Rule("constrs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("fielddecls1"));
      grammar.put("fielddecls", new Rule("fielddecls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      grammar.put("missing_module_keyword", new Rule("missing_module_keyword", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("importdecls").add(VOCURLY));
      addVar(variants, end().add("importdecls").add(OCURLY));
      grammar.put("header_body", new Rule("header_body", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("name_var", end(), end().add("namelist").add(COMMA)));
      grammar.put("namelist", new Rule("namelist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("alt"));
      addVar(left, many(SEMI, end(), end().add("alt")).add("alts1"));
      grammar.put("alts1", new Rule("alts1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("texp", end(), many(DOTDOT, end(), end().add("exp")), many(DOTDOT, end(), end().add("exp")).add("exp").add(COMMA), end().add("flattenedpquals").add(VBAR)));
      addVar(variants, end().add("lexps"));
      grammar.put("list", new Rule("list", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add(SEMI));
      grammar.put("optSemi", new Rule("optSemi", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(left, many(SEMI, end(), end().add("rule")).add("rules"));
      addVar(variants, end().add("rule"));
      grammar.put("rules", new Rule("rules", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(INTERRUPTIBLE));
      addVar(variants, end().add(SAFE));
      addVar(variants, end().add(UNSAFE));
      grammar.put("safety", new Rule("safety", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("fielddecl", end(), end().add("fielddecls1").add("maybe_docprev").add(COMMA).add("maybe_docnext")));
      grammar.put("fielddecls1", new Rule("fielddecls1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("qual"));
      addVar(left, end().add("qual").add(COMMA).add("guardquals1"));
      grammar.put("guardquals1", new Rule("guardquals1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, many("texp", end(), end().add("commas_tup_tail")));
      grammar.put("tup_tail", new Rule("tup_tail", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("texp").add(COMMA).add("texp"));
      addVar(left, end().add("texp").add(COMMA).add("lexps"));
      grammar.put("lexps", new Rule("lexps", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, many("btype", end(), end().add("btype").add(TILDE)));
      grammar.put("context", new Rule("context", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("gdpat"));
      addVar(left, end().add("gdpat").add("gdpats"));
      grammar.put("gdpats", new Rule("gdpats", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end());
      addVar(variants, end().add("decl"));
      addVar(left, many(SEMI, end(), end().add("decl")).add("decls"));
      grammar.put("decls", new Rule("decls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add("constr"));
      addVar(left, end().add("constr").add("maybe_docprev").add(VBAR).add("maybe_docnext").add("constrs1"));
      grammar.put("constrs1", new Rule("constrs1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, end().add(PRIMFLOAT));
      addVar(variants, end().add(PRIMCHAR));
      addVar(variants, end().add(CHAR));
      addVar(variants, end().add(PRIMWORD));
      addVar(variants, end().add(PRIMDOUBLE));
      addVar(variants, end().add(PRIMSTRING));
      addVar(variants, end(GrammarPackage.getSTRING_LITERAL()).add(STRING));
      addVar(variants, end().add(PRIMINT));
      grammar.put("literal", new Rule("literal", variants, left));
    }
    return grammar;
  }
}
