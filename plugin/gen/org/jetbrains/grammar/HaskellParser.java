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
      addVar(variants, term(BANG));
      addVar(variants, term(UNPACK_PRAG), term(CLOSE_PRAG));
      addVar(variants, term(NOUNPACK_PRAG), term(CLOSE_PRAG));
      addVar(variants, term(UNPACK_PRAG), term(CLOSE_PRAG), term(BANG));
      addVar(variants, term(NOUNPACK_PRAG), term(CLOSE_PRAG), term(BANG));
      grammar.put("strict_mark", new Rule("strict_mark", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(WHERE), nonTerm("decllist_inst"));
      addVar(variants);
      grammar.put("where_inst", new Rule("where_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("tyvar"));
      addVar(variants, term(OPAREN), nonTerm("tyvar"), term(DCOLON), nonTerm("kind"), term(CPAREN));
      grammar.put("tv_bndr", new Rule("tv_bndr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("squals"), term(COMMA), nonTerm("transformqual"));
      addVar(left, nonTerm("squals"), term(COMMA), nonTerm("qual"));
      addVar(variants, nonTerm("transformqual"));
      addVar(variants, nonTerm("qual"));
      grammar.put("squals", new Rule("squals", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qvarop"));
      addVar(variants, nonTerm("qconop"));
      grammar.put("qop", new Rule("qop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, nonTerm("cvtopdecls"));
      grammar.put("cvtopdecls0", new Rule("cvtopdecls0", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("rule_var"));
      addVar(variants, nonTerm("rule_var"), nonTerm("rule_var_list"));
      grammar.put("rule_var_list", new Rule("rule_var_list", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(SEMI), nonTerm("stmts"));
      addVar(variants);
      grammar.put("stmts_help", new Rule("stmts_help", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(VARID));
      addVar(variants, term(UNDERSCORE));
      grammar.put("role", new Rule("role", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(EQUAL), nonTerm("exp"), nonTerm("wherebinds")).setElementType(GrammarPackage.getRIGHT_HAND_SIDE());
      addVar(variants, nonTerm("gdrhs"), nonTerm("wherebinds")).setElementType(GrammarPackage.getRIGHT_HAND_SIDE());
      grammar.put("rhs", new Rule("rhs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OCURLY), nonTerm("decls_inst"), term(CCURLY));
      addVar(variants, term(VOCURLY), nonTerm("decls_inst"), nonTerm("close"));
      grammar.put("decllist_inst", new Rule("decllist_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qvarsym_no_minus"));
      addVar(variants, term(BACKQUOTE), nonTerm("qvarid"), term(BACKQUOTE));
      grammar.put("qvaropm", new Rule("qvaropm", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(QVARSYM));
      grammar.put("qvarsym1", new Rule("qvarsym1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(TYPE), nonTerm("type"), term(EQUAL), nonTerm("ctypedoc")).setElementType(GrammarPackage.getTYPE_SYNONYM());
      addVar(variants, term(TYPE), term(FAMILY), nonTerm("type"), nonTerm("opt_kind_sig"), nonTerm("where_type_family"));
      addVar(variants, nonTerm("data_or_newtype"), nonTerm("capi_ctype"), nonTerm("tycl_hdr"), nonTerm("constrs"), nonTerm("deriving")).setElementType(GrammarPackage.getDATA_DECLARATION());
      addVar(variants, nonTerm("data_or_newtype"), nonTerm("capi_ctype"), nonTerm("tycl_hdr"), nonTerm("opt_kind_sig"), nonTerm("gadt_constrlist"), nonTerm("deriving"));
      addVar(variants, term(DATA), term(FAMILY), nonTerm("type"), nonTerm("opt_kind_sig"));
      grammar.put("ty_decl", new Rule("ty_decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("exp10"));
      addVar(left, nonTerm("infixexp"), nonTerm("qop"), nonTerm("exp10")).setElementType(GrammarPackage.getOPERATOR_EXPRESSION());
      grammar.put("infixexp", new Rule("infixexp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("topdecls"), term(SEMI), nonTerm("topdecl"));
      addVar(left, nonTerm("topdecls"), term(SEMI));
      addVar(variants, nonTerm("topdecl"));
      grammar.put("topdecls", new Rule("topdecls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("docsection"));
      addVar(variants, nonTerm("docnamed"));
      addVar(variants, nonTerm("docnext"));
      grammar.put("exp_doc", new Rule("exp_doc", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("maybedocheader"), term(MODULE), nonTerm("modid"), nonTerm("maybemodwarning"), nonTerm("maybeexports"), term(WHERE), nonTerm("header_body"));
      addVar(variants, nonTerm("header_body2"));
      grammar.put("header", new Rule("header", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, term(FAMILY));
      grammar.put("opt_family", new Rule("opt_family", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(CONID));
      grammar.put("conid", new Rule("conid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(DOCCOMMENTNEXT));
      grammar.put("docnext", new Rule("docnext", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OCURLY), nonTerm("ty_fam_inst_eqns"), term(CCURLY));
      addVar(variants, term(VOCURLY), nonTerm("ty_fam_inst_eqns"), nonTerm("close"));
      addVar(variants, term(OCURLY), term(DOTDOT), term(CCURLY));
      addVar(variants, term(VOCURLY), term(DOTDOT), nonTerm("close"));
      grammar.put("ty_fam_inst_eqn_list", new Rule("ty_fam_inst_eqn_list", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(GENERATED_PRAG), term(STRING), term(INTEGER), term(COLON), term(INTEGER), term(MINUS), term(INTEGER), term(COLON), term(INTEGER), term(CLOSE_PRAG));
      grammar.put("hpc_annot", new Rule("hpc_annot", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("role"));
      addVar(left, nonTerm("roles"), nonTerm("role"));
      grammar.put("roles", new Rule("roles", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qtyconsym"));
      addVar(variants, term(BACKQUOTE), nonTerm("qtycon"), term(BACKQUOTE));
      grammar.put("qtyconop", new Rule("qtyconop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("maybe_docnext"), nonTerm("forall"), nonTerm("context"), term(DARROW), nonTerm("constr_stuff"), nonTerm("maybe_docprev")).setElementType(GrammarPackage.getCONSTRUCTOR_DECLARATION());
      addVar(variants, nonTerm("maybe_docnext"), nonTerm("forall"), nonTerm("constr_stuff"), nonTerm("maybe_docprev")).setElementType(GrammarPackage.getCONSTRUCTOR_DECLARATION());
      grammar.put("constr", new Rule("constr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("docnext"));
      addVar(variants);
      grammar.put("maybe_docnext", new Rule("maybe_docnext", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(SOURCE_PRAG), term(CLOSE_PRAG));
      addVar(variants);
      grammar.put("maybe_src", new Rule("maybe_src", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, term(INTEGER));
      grammar.put("prec", new Rule("prec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("cl_decl")).setElementType(GrammarPackage.getCLASS_DECLARATION());
      addVar(variants, nonTerm("ty_decl"));
      addVar(variants, nonTerm("inst_decl")).setElementType(GrammarPackage.getINSTANCE_DECLARATION());
      addVar(variants, nonTerm("stand_alone_deriving"));
      addVar(variants, nonTerm("role_annot"));
      addVar(variants, term(DEFAULT), term(OPAREN), nonTerm("comma_types0"), term(CPAREN));
      addVar(variants, term(FOREIGN), nonTerm("fdecl"));
      addVar(variants, term(DEPRECATED_PRAG), nonTerm("deprecations"), term(CLOSE_PRAG));
      addVar(variants, term(WARNING_PRAG), nonTerm("warnings"), term(CLOSE_PRAG));
      addVar(variants, term(RULES_PRAG), nonTerm("rules"), term(CLOSE_PRAG));
      addVar(variants, term(VECT_PRAG), nonTerm("qvar"), term(EQUAL), nonTerm("exp"), term(CLOSE_PRAG));
      addVar(variants, term(NOVECT_PRAG), nonTerm("qvar"), term(CLOSE_PRAG));
      addVar(variants, term(VECT_PRAG), term(TYPE), nonTerm("gtycon"), term(CLOSE_PRAG));
      addVar(variants, term(VECT_SCALAR_PRAG), term(TYPE), nonTerm("gtycon"), term(CLOSE_PRAG));
      addVar(variants, term(VECT_PRAG), term(TYPE), nonTerm("gtycon"), term(EQUAL), nonTerm("gtycon"), term(CLOSE_PRAG));
      addVar(variants, term(VECT_SCALAR_PRAG), term(TYPE), nonTerm("gtycon"), term(EQUAL), nonTerm("gtycon"), term(CLOSE_PRAG));
      addVar(variants, term(VECT_PRAG), term(CLASS), nonTerm("gtycon"), term(CLOSE_PRAG));
      addVar(variants, nonTerm("annotation"));
      addVar(variants, nonTerm("decl_no_th"));
      addVar(variants, nonTerm("infixexp"));
      grammar.put("topdecl", new Rule("topdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, term(DCOLON), nonTerm("atype"));
      grammar.put("opt_asig", new Rule("opt_asig", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qconid")).setElementType(GrammarPackage.getQ_CON());
      addVar(variants, term(OPAREN), nonTerm("qconsym"), term(CPAREN));
      addVar(variants, nonTerm("sysdcon"));
      grammar.put("qcon", new Rule("qcon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qconsym"));
      addVar(variants, term(BACKQUOTE), nonTerm("qconid"), term(BACKQUOTE));
      grammar.put("qconop", new Rule("qconop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("namelist"), nonTerm("strings"));
      grammar.put("deprecation", new Rule("deprecation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("topdecls"));
      grammar.put("cvtopdecls", new Rule("cvtopdecls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("varsym_no_minus"));
      addVar(variants, term(MINUS));
      grammar.put("varsym", new Rule("varsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OCURLY), nonTerm("importdecls"));
      addVar(variants, nonTerm("missing_module_keyword"), nonTerm("importdecls"));
      grammar.put("header_body2", new Rule("header_body2", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("at_decl_cls"));
      addVar(variants, nonTerm("decl"));
      addVar(variants, term(DEFAULT), nonTerm("infixexp"), term(DCOLON), nonTerm("sigtypedoc"));
      grammar.put("decl_cls", new Rule("decl_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(TYPE), term(ROLE), nonTerm("oqtycon"), nonTerm("maybe_roles"));
      grammar.put("role_annot", new Rule("role_annot", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("sigtype"));
      grammar.put("inst_type", new Rule("inst_type", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("qcnames"), term(COMMA), nonTerm("qcname_ext"));
      addVar(variants, nonTerm("qcname_ext"));
      grammar.put("qcnames", new Rule("qcnames", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("exp_doc"), nonTerm("expdoclist"));
      addVar(variants);
      grammar.put("expdoclist", new Rule("expdoclist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(DOCCOMMENTPREV));
      grammar.put("docprev", new Rule("docprev", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("warnings"), term(SEMI), nonTerm("warning"));
      addVar(left, nonTerm("warnings"), term(SEMI));
      addVar(variants, nonTerm("warning"));
      addVar(variants);
      grammar.put("warnings", new Rule("warnings", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("decl_no_th"));
      addVar(variants, nonTerm("splice_exp"));
      grammar.put("decl", new Rule("decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("varid"));
      addVar(variants, term(OPAREN), nonTerm("varid"), term(DCOLON), nonTerm("ctype"), term(CPAREN));
      grammar.put("rule_var", new Rule("rule_var", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("context"), term(DARROW), nonTerm("type"));
      addVar(variants, nonTerm("type"));
      grammar.put("tycl_hdr", new Rule("tycl_hdr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, nonTerm("varid"), nonTerm("vars0"));
      grammar.put("vars0", new Rule("vars0", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("varids0"), term(RARROW), nonTerm("varids0"));
      grammar.put("fd", new Rule("fd", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(DOCSECTION));
      grammar.put("docsection", new Rule("docsection", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qvarid")).setElementType(GrammarPackage.getQ_VAR());
      addVar(variants, term(OPAREN), nonTerm("varsym"), term(CPAREN)).setElementType(GrammarPackage.getQ_VAR_SYM());
      addVar(variants, term(OPAREN), nonTerm("qvarsym1"), term(CPAREN)).setElementType(GrammarPackage.getQ_VAR());
      grammar.put("qvar", new Rule("qvar", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OCURLY), nonTerm("top"), term(CCURLY));
      addVar(variants, nonTerm("missing_module_keyword"), nonTerm("top"), nonTerm("close"));
      grammar.put("body2", new Rule("body2", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qtycon"));
      addVar(variants, term(OPAREN), term(CPAREN));
      addVar(variants, term(OPAREN), nonTerm("kind"), term(COMMA), nonTerm("comma_kinds1"), term(CPAREN));
      addVar(variants, term(OBRACK), nonTerm("kind"), term(CBRACK));
      grammar.put("pkind", new Rule("pkind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("type"), term(EQUAL), nonTerm("ctype"));
      grammar.put("ty_fam_inst_eqn", new Rule("ty_fam_inst_eqn", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OCURLY), nonTerm("decls"), term(CCURLY));
      addVar(variants, term(VOCURLY), nonTerm("decls"), nonTerm("close"));
      grammar.put("decllist", new Rule("decllist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("fbind"), term(COMMA), nonTerm("fbinds1"));
      addVar(variants, nonTerm("fbind"));
      addVar(variants, term(DOTDOT));
      grammar.put("fbinds1", new Rule("fbinds1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("name_boolformula_and"));
      addVar(variants, nonTerm("name_boolformula_and"), term(VBAR), nonTerm("name_boolformula"));
      grammar.put("name_boolformula", new Rule("name_boolformula", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qvarsym"));
      addVar(variants, term(BACKQUOTE), nonTerm("qvarid"), term(BACKQUOTE));
      grammar.put("qvarop", new Rule("qvarop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("fds1"), term(COMMA), nonTerm("fd"));
      addVar(variants, nonTerm("fd"));
      grammar.put("fds1", new Rule("fds1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("bindpat"), term(LARROW), nonTerm("exp")).setElementType(GrammarPackage.getBIND_STATEMENT());
      addVar(variants, nonTerm("exp")).setElementType(GrammarPackage.getEXPRESSION_STATEMENT());
      addVar(variants, term(LET), nonTerm("binds")).setElementType(GrammarPackage.getLET_STATEMENT());
      grammar.put("qual", new Rule("qual", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(CONID)).setElementType(GrammarPackage.getMODULE_NAME());
      addVar(variants, term(QCONID)).setElementType(GrammarPackage.getMODULE_NAME());
      grammar.put("modid", new Rule("modid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, nonTerm("texp"));
      addVar(variants, nonTerm("lexps"));
      addVar(variants, nonTerm("texp"), term(DOTDOT), nonTerm("exp"));
      addVar(variants, nonTerm("texp"), term(COMMA), nonTerm("exp"), term(DOTDOT), nonTerm("exp"));
      addVar(variants, nonTerm("texp"), term(VBAR), nonTerm("flattenedpquals"));
      grammar.put("parr", new Rule("parr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("conid"));
      addVar(variants, term(OPAREN), nonTerm("consym"), term(CPAREN));
      addVar(variants, nonTerm("sysdcon"));
      grammar.put("con", new Rule("con", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OCURLY), nonTerm("top"), term(CCURLY));
      addVar(variants, term(VOCURLY), nonTerm("top"), nonTerm("close"));
      grammar.put("body", new Rule("body", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("squals"), term(VBAR), nonTerm("pquals"));
      addVar(variants, nonTerm("squals"));
      grammar.put("pquals", new Rule("pquals", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(VCCURLY));
      grammar.put("close", new Rule("close", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("pquals"));
      grammar.put("flattenedpquals", new Rule("flattenedpquals", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("fexp"), nonTerm("aexp")).setElementType(GrammarPackage.getAPPLICATION());
      addVar(variants, nonTerm("aexp"));
      grammar.put("fexp", new Rule("fexp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("ctype"));
      addVar(variants, nonTerm("ctype"), term(COMMA), nonTerm("comma_types1"));
      grammar.put("comma_types1", new Rule("comma_types1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qvar"));
      addVar(variants, nonTerm("qcon"));
      grammar.put("qcname", new Rule("qcname", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(AS));
      addVar(variants, term(QUALIFIED));
      addVar(variants, term(HIDING));
      addVar(variants, term(EXPORT));
      addVar(variants, term(LABEL));
      addVar(variants, term(DYNAMIC));
      addVar(variants, term(STDCALLCONV));
      addVar(variants, term(CCALLCONV));
      addVar(variants, term(CAPICONV));
      addVar(variants, term(PRIMCALLCONV));
      addVar(variants, term(JAVASCRIPTCALLCONV));
      addVar(variants, term(GROUP));
      grammar.put("special_id", new Rule("special_id", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("comma_types1"));
      addVar(variants);
      grammar.put("comma_types0", new Rule("comma_types0", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("expdoclist"), term(COMMA), nonTerm("expdoclist"));
      addVar(variants, nonTerm("exportlist1"));
      grammar.put("exportlist", new Rule("exportlist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(LAM), nonTerm("apat"), nonTerm("apats"), nonTerm("opt_asig"), term(RARROW), nonTerm("exp")).setElementType(GrammarPackage.getLAMBDA_EXPRESSION());
      addVar(variants, term(LET), nonTerm("binds"), term(IN), nonTerm("exp")).setElementType(GrammarPackage.getLET_EXPRESSION());
      addVar(variants, term(LAM), term(LCASE), nonTerm("altslist"));
      addVar(variants, term(IF), nonTerm("exp"), nonTerm("optSemi"), term(THEN), nonTerm("exp"), nonTerm("optSemi"), term(ELSE), nonTerm("exp"));
      addVar(variants, term(IF), nonTerm("ifgdpats"));
      addVar(variants, term(CASE), nonTerm("exp"), term(OF), nonTerm("altslist")).setElementType(GrammarPackage.getCASE_EXPRESSION());
      addVar(variants, term(MINUS), nonTerm("fexp"));
      addVar(variants, term(DO), nonTerm("stmtlist")).setElementType(GrammarPackage.getDO_EXPRESSION());
      addVar(variants, term(MDO), nonTerm("stmtlist"));
      addVar(variants, nonTerm("scc_annot"), nonTerm("exp"));
      addVar(variants, nonTerm("hpc_annot"), nonTerm("exp"));
      addVar(variants, term(PROC), nonTerm("aexp"), term(RARROW), nonTerm("exp"));
      addVar(variants, term(CORE_PRAG), term(STRING), term(CLOSE_PRAG), nonTerm("exp"));
      addVar(variants, nonTerm("fexp"));
      grammar.put("exp10", new Rule("exp10", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(WHERE), term(OCURLY), nonTerm("gadt_constrs"), term(CCURLY));
      addVar(variants, term(WHERE), term(VOCURLY), nonTerm("gadt_constrs"), nonTerm("close"));
      addVar(variants);
      grammar.put("gadt_constrlist", new Rule("gadt_constrlist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("ipvar"), term(EQUAL), nonTerm("exp"));
      grammar.put("dbind", new Rule("dbind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, term(DCOLON), nonTerm("kind"));
      grammar.put("opt_kind_sig", new Rule("opt_kind_sig", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(WHERE), nonTerm("decllist_cls"));
      addVar(variants);
      grammar.put("where_cls", new Rule("where_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("exp"));
      addVar(variants, nonTerm("infixexp"), nonTerm("qop"));
      addVar(variants, nonTerm("qopm"), nonTerm("infixexp"));
      addVar(variants, nonTerm("exp"), term(RARROW), nonTerm("texp"));
      grammar.put("texp", new Rule("texp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(VBAR), nonTerm("guardquals"), term(RARROW), nonTerm("exp"));
      grammar.put("gdpat", new Rule("gdpat", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("varsym"));
      addVar(variants, term(BACKQUOTE), nonTerm("varid"), term(BACKQUOTE));
      grammar.put("varop", new Rule("varop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("gdpatssemi"), nonTerm("gdpat"), nonTerm("optSemi"));
      addVar(variants, nonTerm("gdpat"), nonTerm("optSemi"));
      grammar.put("gdpatssemi", new Rule("gdpatssemi", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("deprecations"), term(SEMI), nonTerm("deprecation"));
      addVar(left, nonTerm("deprecations"), term(SEMI));
      addVar(variants, nonTerm("deprecation"));
      addVar(variants);
      grammar.put("deprecations", new Rule("deprecations", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OBRACK), term(INTEGER), term(CBRACK));
      addVar(variants, term(OBRACK), term(TILDE), term(INTEGER), term(CBRACK));
      addVar(variants, term(OBRACK), term(TILDE), term(CBRACK));
      grammar.put("rule_explicit_activation", new Rule("rule_explicit_activation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(RARROW), nonTerm("exp"));
      addVar(variants, nonTerm("gdpats"));
      grammar.put("ralt", new Rule("ralt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(DATA), nonTerm("opt_family"), nonTerm("type"), nonTerm("opt_kind_sig"));
      addVar(variants, term(TYPE), nonTerm("type"), nonTerm("opt_kind_sig"));
      addVar(variants, term(TYPE), term(FAMILY), nonTerm("type"), nonTerm("opt_kind_sig"));
      addVar(variants, term(TYPE), nonTerm("ty_fam_inst_eqn"));
      addVar(variants, term(TYPE), term(INSTANCE), nonTerm("ty_fam_inst_eqn"));
      grammar.put("at_decl_cls", new Rule("at_decl_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OCURLY), nonTerm("alts"), term(CCURLY));
      addVar(variants, term(VOCURLY), nonTerm("alts"), nonTerm("close"));
      addVar(variants, term(OCURLY), term(CCURLY));
      addVar(variants, term(VOCURLY), nonTerm("close"));
      grammar.put("altslist", new Rule("altslist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(DOCCOMMENTNEXT));
      grammar.put("moduleheader", new Rule("moduleheader", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(WHERE), nonTerm("binds")).setElementType(GrammarPackage.getWHERE_BINDINGS());
      addVar(variants);
      grammar.put("wherebinds", new Rule("wherebinds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(QUASIQUOTE));
      addVar(variants, term(QQUASIQUOTE));
      grammar.put("quasiquote", new Rule("quasiquote", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("varid"));
      addVar(variants, term(OPAREN), nonTerm("varsym"), term(CPAREN));
      grammar.put("var", new Rule("var", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(STRING));
      addVar(variants, term(OBRACK), nonTerm("stringlist"), term(CBRACK));
      grammar.put("strings", new Rule("strings", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, term(VBAR), nonTerm("fds1"));
      grammar.put("fds", new Rule("fds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("sigtype"));
      addVar(variants, nonTerm("sigtype"), term(COMMA), nonTerm("sigtypes1"));
      grammar.put("sigtypes1", new Rule("sigtypes1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("stmt"));
      addVar(variants);
      grammar.put("maybe_stmt", new Rule("maybe_stmt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("tv_bndr"), nonTerm("tv_bndrs"));
      addVar(variants);
      grammar.put("tv_bndrs", new Rule("tv_bndrs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("infixexp"), term(DCOLON), nonTerm("sigtypedoc"));
      addVar(variants, nonTerm("var"), term(COMMA), nonTerm("sig_vars"), term(DCOLON), nonTerm("sigtypedoc"));
      addVar(variants, nonTerm("infix"), nonTerm("prec"), nonTerm("ops"));
      addVar(variants, term(INLINE_PRAG), nonTerm("activation"), nonTerm("qvar"), term(CLOSE_PRAG));
      addVar(variants, term(SPEC_PRAG), nonTerm("activation"), nonTerm("qvar"), term(DCOLON), nonTerm("sigtypes1"), term(CLOSE_PRAG));
      addVar(variants, term(SPEC_INLINE_PRAG), nonTerm("activation"), nonTerm("qvar"), term(DCOLON), nonTerm("sigtypes1"), term(CLOSE_PRAG));
      addVar(variants, term(SPEC_PRAG), term(INSTANCE), nonTerm("inst_type"), term(CLOSE_PRAG));
      addVar(variants, term(MINIMAL_PRAG), nonTerm("name_boolformula_opt"), term(CLOSE_PRAG));
      grammar.put("sigdecl", new Rule("sigdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(THEN), nonTerm("exp"));
      addVar(variants, term(THEN), nonTerm("exp"), term(BY), nonTerm("exp"));
      addVar(variants, term(THEN), term(GROUP), term(USING), nonTerm("exp"));
      addVar(variants, term(THEN), term(GROUP), term(BY), nonTerm("exp"), term(USING), nonTerm("exp"));
      grammar.put("transformqual", new Rule("transformqual", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("exp"));
      addVar(variants, term(BANG), nonTerm("aexp"));
      grammar.put("pat", new Rule("pat", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("pat"), nonTerm("opt_sig"), nonTerm("alt_rhs")).setElementType(GrammarPackage.getCASE_ALTERNATIVE());
      grammar.put("alt", new Rule("alt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(FORALL), nonTerm("rule_var_list"), term(DOT));
      addVar(variants);
      grammar.put("rule_forall", new Rule("rule_forall", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(STDCALLCONV));
      addVar(variants, term(CCALLCONV));
      addVar(variants, term(CAPICONV));
      addVar(variants, term(PRIMCALLCONV));
      addVar(variants, term(JAVASCRIPTCALLCONV));
      grammar.put("callconv", new Rule("callconv", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(STRING), nonTerm("var"), term(DCOLON), nonTerm("sigtypedoc"));
      addVar(variants, nonTerm("var"), term(DCOLON), nonTerm("sigtypedoc"));
      grammar.put("fspec", new Rule("fspec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("consym"));
      addVar(variants, term(BACKQUOTE), nonTerm("conid"), term(BACKQUOTE));
      grammar.put("conop", new Rule("conop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("importdecls"));
      addVar(variants, nonTerm("importdecls"), term(SEMI), nonTerm("cvtopdecls"));
      addVar(variants, nonTerm("cvtopdecls"));
      grammar.put("top", new Rule("top", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qcname_ext"), nonTerm("export_subspec"));
      addVar(variants, term(MODULE), nonTerm("modid"));
      addVar(variants, term(PATTERN), nonTerm("qcon"));
      grammar.put("export", new Rule("export", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OPAREN), nonTerm("exportlist"), term(CPAREN)).setElementType(GrammarPackage.getMODULE_EXPORTS());
      addVar(variants);
      grammar.put("maybeexports", new Rule("maybeexports", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("gdrhs"), nonTerm("gdrh"));
      addVar(variants, nonTerm("gdrh"));
      grammar.put("gdrhs", new Rule("gdrhs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qual"));
      addVar(variants, term(REC), nonTerm("stmtlist"));
      grammar.put("stmt", new Rule("stmt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(FORALL), nonTerm("tv_bndrs"), term(DOT), nonTerm("ctype"));
      addVar(variants, nonTerm("context"), term(DARROW), nonTerm("ctype"));
      addVar(variants, nonTerm("ipvar"), term(DCOLON), nonTerm("type"));
      addVar(variants, nonTerm("type"));
      grammar.put("ctype", new Rule("ctype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OPAREN), nonTerm("name_boolformula"), term(CPAREN));
      addVar(variants, nonTerm("name_var"));
      grammar.put("name_boolformula_atom", new Rule("name_boolformula_atom", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(IMPORT), nonTerm("callconv"), nonTerm("safety"), nonTerm("fspec"));
      addVar(variants, term(IMPORT), nonTerm("callconv"), nonTerm("fspec"));
      addVar(variants, term(EXPORT), nonTerm("callconv"), nonTerm("fspec"));
      grammar.put("fdecl", new Rule("fdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("inst_type"));
      addVar(variants, nonTerm("inst_type"), term(COMMA), nonTerm("inst_types1"));
      grammar.put("inst_types1", new Rule("inst_types1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("oqtycon"));
      addVar(variants, term(OPAREN), nonTerm("commas"), term(CPAREN));
      addVar(variants, term(OUBXPAREN), nonTerm("commas"), term(CUBXPAREN));
      addVar(variants, term(OPAREN), term(RARROW), term(CPAREN));
      addVar(variants, term(OBRACK), term(CBRACK));
      addVar(variants, term(OPABRACK), term(CPABRACK));
      addVar(variants, term(OPAREN), term(TILDEHSH), term(CPAREN));
      grammar.put("ntgtycon", new Rule("ntgtycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("decls_cls"), term(SEMI), nonTerm("decl_cls"));
      addVar(left, nonTerm("decls_cls"), term(SEMI));
      addVar(variants, nonTerm("decl_cls"));
      addVar(variants);
      grammar.put("decls_cls", new Rule("decls_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(TYPE), nonTerm("ty_fam_inst_eqn"));
      addVar(variants, nonTerm("data_or_newtype"), nonTerm("capi_ctype"), nonTerm("tycl_hdr"), nonTerm("constrs"), nonTerm("deriving")).setElementType(GrammarPackage.getDATA_DECLARATION());
      addVar(variants, nonTerm("data_or_newtype"), nonTerm("capi_ctype"), nonTerm("tycl_hdr"), nonTerm("opt_kind_sig"), nonTerm("gadt_constrlist"), nonTerm("deriving"));
      grammar.put("at_decl_inst", new Rule("at_decl_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(AS), nonTerm("modid")).setElementType(GrammarPackage.getIMPORT_AS_PART());
      addVar(variants);
      grammar.put("maybeas", new Rule("maybeas", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(STAR));
      addVar(variants, term(OPAREN), nonTerm("kind"), term(CPAREN));
      addVar(variants, nonTerm("pkind"));
      addVar(variants, nonTerm("tyvar"));
      grammar.put("akind", new Rule("akind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, nonTerm("roles"));
      grammar.put("maybe_roles", new Rule("maybe_roles", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(SCC_PRAG), term(STRING), term(CLOSE_PRAG));
      addVar(variants, term(SCC_PRAG), term(VARID), term(CLOSE_PRAG));
      grammar.put("scc_annot", new Rule("scc_annot", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(CLASS), nonTerm("tycl_hdr"), nonTerm("fds"), nonTerm("where_cls"));
      grammar.put("cl_decl", new Rule("cl_decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("conid"));
      addVar(variants, term(QCONID));
      addVar(variants, term(PREFIXQCONSYM));
      grammar.put("qconid", new Rule("qconid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(IMPORT), nonTerm("maybe_src"), nonTerm("maybe_safe"), nonTerm("optqualified"), nonTerm("maybe_pkg"), nonTerm("modid"), nonTerm("maybeas"), nonTerm("maybeimpspec")).setElementType(GrammarPackage.getIMPORT());
      grammar.put("importdecl", new Rule("importdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("texp"), nonTerm("commas_tup_tail"));
      addVar(variants, nonTerm("commas"), nonTerm("tup_tail"));
      grammar.put("tup_exprs", new Rule("tup_exprs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(DOCCOMMENTNAMED));
      grammar.put("docnamed", new Rule("docnamed", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(left, nonTerm("varids0"), nonTerm("tyvar"));
      grammar.put("varids0", new Rule("varids0", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("ty_fam_inst_eqns"), term(SEMI), nonTerm("ty_fam_inst_eqn"));
      addVar(left, nonTerm("ty_fam_inst_eqns"), term(SEMI));
      addVar(variants, nonTerm("ty_fam_inst_eqn"));
      grammar.put("ty_fam_inst_eqns", new Rule("ty_fam_inst_eqns", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qtycon"));
      addVar(variants, term(OPAREN), nonTerm("qtyconsym"), term(CPAREN));
      addVar(variants, term(OPAREN), term(TILDE), term(CPAREN));
      grammar.put("oqtycon", new Rule("oqtycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(ANN_PRAG), nonTerm("name_var"), nonTerm("aexp"), term(CLOSE_PRAG));
      addVar(variants, term(ANN_PRAG), term(TYPE), nonTerm("tycon"), nonTerm("aexp"), term(CLOSE_PRAG));
      addVar(variants, term(ANN_PRAG), term(MODULE), nonTerm("aexp"), term(CLOSE_PRAG));
      grammar.put("annotation", new Rule("annotation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("guardquals1"));
      grammar.put("guardquals", new Rule("guardquals", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(INFIX));
      addVar(variants, term(INFIXL));
      addVar(variants, term(INFIXR));
      grammar.put("infix", new Rule("infix", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("name_boolformula"));
      addVar(variants);
      grammar.put("name_boolformula_opt", new Rule("name_boolformula_opt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(PATTERN), nonTerm("pat"), term(EQUAL), nonTerm("pat"));
      addVar(variants, term(PATTERN), nonTerm("pat"), term(LARROW), nonTerm("pat"));
      addVar(variants, term(PATTERN), nonTerm("pat"), term(LARROW), nonTerm("pat"), nonTerm("where_decls"));
      grammar.put("pattern_synonym_decl", new Rule("pattern_synonym_decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("ctypedoc"));
      grammar.put("sigtypedoc", new Rule("sigtypedoc", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(CONSYM));
      addVar(variants, term(VARSYM));
      addVar(variants, term(STAR));
      addVar(variants, term(MINUS));
      grammar.put("tyconsym", new Rule("tyconsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OBRACK), term(INTEGER), term(CBRACK));
      addVar(variants, term(OBRACK), term(TILDE), term(INTEGER), term(CBRACK));
      grammar.put("explicit_activation", new Rule("explicit_activation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("ops"), term(COMMA), nonTerm("op"));
      addVar(variants, nonTerm("op"));
      grammar.put("ops", new Rule("ops", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("docprev"));
      addVar(variants);
      grammar.put("maybe_docprev", new Rule("maybe_docprev", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(WHERE), term(OCURLY), nonTerm("decls"), term(CCURLY));
      addVar(variants, term(WHERE), term(VOCURLY), nonTerm("decls"), nonTerm("close"));
      grammar.put("where_decls", new Rule("where_decls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(INSTANCE), nonTerm("overlap_pragma"), nonTerm("inst_type"), nonTerm("where_inst"));
      addVar(variants, term(TYPE), term(INSTANCE), nonTerm("ty_fam_inst_eqn"));
      addVar(variants, nonTerm("data_or_newtype"), term(INSTANCE), nonTerm("capi_ctype"), nonTerm("tycl_hdr"), nonTerm("constrs"), nonTerm("deriving"));
      addVar(variants, nonTerm("data_or_newtype"), term(INSTANCE), nonTerm("capi_ctype"), nonTerm("tycl_hdr"), nonTerm("opt_kind_sig"), nonTerm("gadt_constrlist"), nonTerm("deriving"));
      grammar.put("inst_decl", new Rule("inst_decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("con_list"), term(DCOLON), nonTerm("sigtype"));
      addVar(variants, nonTerm("oqtycon"), term(OCURLY), nonTerm("fielddecls"), term(CCURLY), term(DCOLON), nonTerm("sigtype"));
      grammar.put("gadt_constr", new Rule("gadt_constr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("varop"));
      addVar(variants, nonTerm("conop"));
      grammar.put("op", new Rule("op", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("sig_vars"), term(COMMA), nonTerm("var"));
      addVar(variants, nonTerm("var"));
      grammar.put("sig_vars", new Rule("sig_vars", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("varsym"));
      addVar(variants, nonTerm("qvarsym1"));
      grammar.put("qvarsym", new Rule("qvarsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("tyvarid"));
      grammar.put("tyvar", new Rule("tyvar", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("akind"));
      addVar(left, nonTerm("bkind"), nonTerm("akind"));
      grammar.put("bkind", new Rule("bkind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, term(DERIVING), nonTerm("qtycon"));
      addVar(variants, term(DERIVING), term(OPAREN), term(CPAREN));
      addVar(variants, term(DERIVING), term(OPAREN), nonTerm("inst_types1"), term(CPAREN));
      grammar.put("deriving", new Rule("deriving", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qvar"), term(EQUAL), nonTerm("texp")).setElementType(GrammarPackage.getFIELD_UPDATE());
      addVar(variants, nonTerm("qvar")).setElementType(GrammarPackage.getFIELD_UPDATE());
      grammar.put("fbind", new Rule("fbind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, term(DCOLON), nonTerm("sigtype"));
      grammar.put("opt_sig", new Rule("opt_sig", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("moduleheader"));
      addVar(variants);
      grammar.put("maybedocheader", new Rule("maybedocheader", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qvaropm"));
      addVar(variants, nonTerm("qconop"));
      grammar.put("qopm", new Rule("qopm", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("impspec"));
      addVar(variants);
      grammar.put("maybeimpspec", new Rule("maybeimpspec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("docnext"));
      addVar(variants, nonTerm("docprev"));
      addVar(variants, nonTerm("docnamed"));
      addVar(variants, nonTerm("docsection"));
      grammar.put("docdecld", new Rule("docdecld", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("decls_inst"), term(SEMI), nonTerm("decl_inst"));
      addVar(left, nonTerm("decls_inst"), term(SEMI));
      addVar(variants, nonTerm("decl_inst"));
      addVar(variants);
      grammar.put("decls_inst", new Rule("decls_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(VBAR), nonTerm("guardquals"), term(EQUAL), nonTerm("exp")).setElementType(GrammarPackage.getGUARD());
      grammar.put("gdrh", new Rule("gdrh", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("decllist"));
      addVar(variants, term(OCURLY), nonTerm("dbinds"), term(CCURLY));
      addVar(variants, term(VOCURLY), nonTerm("dbinds"), nonTerm("close"));
      grammar.put("binds", new Rule("binds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("ntgtycon")).setElementType(GrammarPackage.getTYPE_VARIABLE());
      addVar(variants, nonTerm("tyvar")).setElementType(GrammarPackage.getTYPE_VARIABLE());
      addVar(variants, nonTerm("strict_mark"), nonTerm("atype"));
      addVar(variants, term(OCURLY), nonTerm("fielddecls"), term(CCURLY));
      addVar(variants, term(OPAREN), term(CPAREN)).setElementType(GrammarPackage.getTUPLE_TYPE());
      addVar(variants, term(OPAREN), nonTerm("ctype"), term(COMMA), nonTerm("comma_types1"), term(CPAREN)).setElementType(GrammarPackage.getTUPLE_TYPE());
      addVar(variants, term(OUBXPAREN), term(CUBXPAREN));
      addVar(variants, term(OUBXPAREN), nonTerm("comma_types1"), term(CUBXPAREN));
      addVar(variants, term(OBRACK), nonTerm("ctype"), term(CBRACK)).setElementType(GrammarPackage.getLIST_TYPE());
      addVar(variants, term(OPABRACK), nonTerm("ctype"), term(CPABRACK));
      addVar(variants, term(OPAREN), nonTerm("ctype"), term(CPAREN)).setElementType(GrammarPackage.getTUPLE_TYPE());
      addVar(variants, term(OPAREN), nonTerm("ctype"), term(DCOLON), nonTerm("kind"), term(CPAREN)).setElementType(GrammarPackage.getTUPLE_TYPE());
      addVar(variants, nonTerm("quasiquote"));
      addVar(variants, term(PARENESCAPE), nonTerm("exp"), term(CPAREN));
      addVar(variants, term(IDESCAPE));
      addVar(variants, term(SIMPLEQUOTE), nonTerm("qcon"));
      addVar(variants, term(SIMPLEQUOTE), term(OPAREN), nonTerm("ctype"), term(COMMA), nonTerm("comma_types1"), term(CPAREN));
      addVar(variants, term(SIMPLEQUOTE), term(OBRACK), nonTerm("comma_types0"), term(CBRACK));
      addVar(variants, term(SIMPLEQUOTE), nonTerm("var"));
      addVar(variants, term(OBRACK), nonTerm("ctype"), term(COMMA), nonTerm("comma_types1"), term(CBRACK)).setElementType(GrammarPackage.getLIST_TYPE());
      addVar(variants, term(INTEGER));
      addVar(variants, term(STRING));
      grammar.put("atype", new Rule("atype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("ctype"));
      grammar.put("sigtype", new Rule("sigtype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(QCONID));
      addVar(variants, term(PREFIXQCONSYM));
      addVar(variants, nonTerm("tycon"));
      grammar.put("qtycon", new Rule("qtycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OVERLAPPABLE), term(CLOSE_PRAG));
      addVar(variants, term(OVERLAPPING), term(CLOSE_PRAG));
      addVar(variants, term(OVERLAPS), term(CLOSE_PRAG));
      addVar(variants, term(INCOHERENT), term(CLOSE_PRAG));
      addVar(variants);
      grammar.put("overlap_pragma", new Rule("overlap_pragma", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("var"));
      addVar(variants, nonTerm("con"));
      grammar.put("name_var", new Rule("name_var", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OCURLY), nonTerm("cvtopdecls0"), term(CCURLY));
      addVar(variants, term(VOCURLY), nonTerm("cvtopdecls0"), nonTerm("close"));
      grammar.put("cvtopbody", new Rule("cvtopbody", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("commas"), nonTerm("tup_tail"));
      grammar.put("commas_tup_tail", new Rule("commas_tup_tail", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OPAREN), term(CPAREN));
      addVar(variants, term(OPAREN), nonTerm("commas"), term(CPAREN));
      addVar(variants, term(OUBXPAREN), term(CUBXPAREN));
      addVar(variants, term(OUBXPAREN), nonTerm("commas"), term(CUBXPAREN));
      addVar(variants, term(OBRACK), term(CBRACK));
      grammar.put("sysdcon", new Rule("sysdcon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("bkind"));
      addVar(variants, nonTerm("bkind"), term(RARROW), nonTerm("kind"));
      grammar.put("kind", new Rule("kind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("aexp")).setElementType(GrammarPackage.getPATTERN());
      addVar(variants, term(BANG), nonTerm("aexp")).setElementType(GrammarPackage.getPATTERN());
      grammar.put("apat", new Rule("apat", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(BACKQUOTE), nonTerm("tyvarid"), term(BACKQUOTE));
      addVar(variants, term(DOT));
      grammar.put("tyvarop", new Rule("tyvarop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("at_decl_inst"));
      addVar(variants, nonTerm("decl"));
      grammar.put("decl_inst", new Rule("decl_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("fbinds1"));
      addVar(variants);
      grammar.put("fbinds", new Rule("fbinds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(FORALL), nonTerm("tv_bndrs"), term(DOT), nonTerm("ctypedoc"));
      addVar(variants, nonTerm("context"), term(DARROW), nonTerm("ctypedoc"));
      addVar(variants, nonTerm("ipvar"), term(DCOLON), nonTerm("type"));
      addVar(variants, nonTerm("typedoc"));
      grammar.put("ctypedoc", new Rule("ctypedoc", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("maybedocheader"), term(MODULE), nonTerm("modid"), nonTerm("maybemodwarning"), nonTerm("maybeexports"), term(WHERE), nonTerm("body")).setElementType(GrammarPackage.getMODULE());
      addVar(variants, nonTerm("body2")).setElementType(GrammarPackage.getMODULE());
      grammar.put("module", new Rule("module", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(CONSYM));
      addVar(variants, term(COLON));
      grammar.put("consym", new Rule("consym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("sigdecl")).setElementType(GrammarPackage.getSIGNATURE_DECLARATION());
      addVar(variants, term(BANG), nonTerm("aexp"), nonTerm("rhs"));
      addVar(variants, nonTerm("infixexp"), nonTerm("opt_sig"), nonTerm("rhs")).setElementType(GrammarPackage.getVALUE_DEFINITION());
      addVar(variants, nonTerm("pattern_synonym_decl"));
      addVar(variants, nonTerm("docdecl"));
      grammar.put("decl_no_th", new Rule("decl_no_th", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, term(WHERE), nonTerm("ty_fam_inst_eqn_list"));
      grammar.put("where_type_family", new Rule("where_type_family", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("consym"));
      addVar(variants, term(QCONSYM));
      grammar.put("qconsym", new Rule("qconsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("aexp2"));
      grammar.put("acmd", new Rule("acmd", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("maybe_docnext"), nonTerm("sig_vars"), term(DCOLON), nonTerm("ctype"), nonTerm("maybe_docprev")).setElementType(GrammarPackage.getFIELD_DECLARATION());
      grammar.put("fielddecl", new Rule("fielddecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("btype"));
      addVar(variants, nonTerm("btype"), nonTerm("conop"), nonTerm("btype"));
      grammar.put("constr_stuff", new Rule("constr_stuff", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(STRING));
      addVar(variants);
      grammar.put("maybe_pkg", new Rule("maybe_pkg", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(VARSYM));
      addVar(variants, nonTerm("special_sym"));
      grammar.put("varsym_no_minus", new Rule("varsym_no_minus", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(BANG));
      addVar(variants, term(DOT));
      addVar(variants, term(STAR));
      grammar.put("special_sym", new Rule("special_sym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("expdoclist"), nonTerm("export"), nonTerm("expdoclist"), term(COMMA), nonTerm("exportlist1"));
      addVar(variants, nonTerm("expdoclist"), nonTerm("export"), nonTerm("expdoclist"));
      addVar(variants, nonTerm("expdoclist"));
      grammar.put("exportlist1", new Rule("exportlist1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(VARID));
      addVar(variants, nonTerm("special_id"));
      addVar(variants, term(UNSAFE));
      addVar(variants, term(SAFE));
      addVar(variants, term(INTERRUPTIBLE));
      addVar(variants, term(FORALL));
      addVar(variants, term(FAMILY));
      addVar(variants, term(ROLE));
      grammar.put("varid", new Rule("varid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("importdecls"), term(SEMI), nonTerm("importdecl"));
      addVar(variants, nonTerm("importdecl"));
      addVar(variants);
      grammar.put("importdecls", new Rule("importdecls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(QUALIFIED));
      addVar(variants);
      grammar.put("optqualified", new Rule("optqualified", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("namelist"), nonTerm("strings"));
      grammar.put("warning", new Rule("warning", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("btype"));
      addVar(variants, nonTerm("btype"), nonTerm("docprev"));
      addVar(variants, nonTerm("btype"), nonTerm("qtyconop"), nonTerm("type"));
      addVar(variants, nonTerm("btype"), nonTerm("qtyconop"), nonTerm("type"), nonTerm("docprev"));
      addVar(variants, nonTerm("btype"), nonTerm("tyvarop"), nonTerm("type"));
      addVar(variants, nonTerm("btype"), nonTerm("tyvarop"), nonTerm("type"), nonTerm("docprev"));
      addVar(variants, nonTerm("btype"), term(RARROW), nonTerm("ctypedoc")).setElementType(GrammarPackage.getFUNCTION_TYPE());
      addVar(variants, nonTerm("btype"), nonTerm("docprev"), term(RARROW), nonTerm("ctypedoc"));
      addVar(variants, nonTerm("btype"), term(TILDE), nonTerm("btype"));
      addVar(variants, nonTerm("btype"), term(SIMPLEQUOTE), nonTerm("qconop"), nonTerm("type"));
      addVar(variants, nonTerm("btype"), term(SIMPLEQUOTE), nonTerm("varop"), nonTerm("type"));
      grammar.put("typedoc", new Rule("typedoc", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qvar"), term(AT), nonTerm("aexp"));
      addVar(variants, term(TILDE), nonTerm("aexp"));
      addVar(variants, nonTerm("aexp1"));
      grammar.put("aexp", new Rule("aexp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("stringlist"), term(COMMA), term(STRING));
      addVar(variants, term(STRING));
      grammar.put("stringlist", new Rule("stringlist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OCURLY), nonTerm("gdpatssemi"), term(CCURLY));
      addVar(variants, nonTerm("gdpatssemi"), nonTerm("close"));
      grammar.put("ifgdpats", new Rule("ifgdpats", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("btype"));
      addVar(variants, nonTerm("btype"), nonTerm("qtyconop"), nonTerm("type"));
      addVar(variants, nonTerm("btype"), nonTerm("tyvarop"), nonTerm("type"));
      addVar(variants, nonTerm("btype"), term(RARROW), nonTerm("ctype")).setElementType(GrammarPackage.getFUNCTION_TYPE());
      addVar(variants, nonTerm("btype"), term(TILDE), nonTerm("btype"));
      addVar(variants, nonTerm("btype"), term(SIMPLEQUOTE), nonTerm("qconop"), nonTerm("type"));
      addVar(variants, nonTerm("btype"), term(SIMPLEQUOTE), nonTerm("varop"), nonTerm("type"));
      grammar.put("type", new Rule("type", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("commas"), term(COMMA));
      addVar(variants, term(COMMA));
      grammar.put("commas", new Rule("commas", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("docdecld"));
      grammar.put("docdecl", new Rule("docdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(CONID));
      grammar.put("tycon", new Rule("tycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("apat"), nonTerm("apats"));
      addVar(variants);
      grammar.put("apats", new Rule("apats", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("varsym_no_minus"));
      addVar(variants, nonTerm("qvarsym1"));
      grammar.put("qvarsym_no_minus", new Rule("qvarsym_no_minus", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(DUPIPVARID));
      grammar.put("ipvar", new Rule("ipvar", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("varid"));
      addVar(variants, term(QVARID));
      addVar(variants, term(PREFIXQVARSYM));
      grammar.put("qvarid", new Rule("qvarid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(QCONSYM));
      addVar(variants, term(QVARSYM));
      addVar(variants, nonTerm("tyconsym"));
      grammar.put("qtyconsym", new Rule("qtyconsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(VARID));
      addVar(variants, nonTerm("special_id"));
      addVar(variants, term(UNSAFE));
      addVar(variants, term(SAFE));
      addVar(variants, term(INTERRUPTIBLE));
      grammar.put("tyvarid", new Rule("tyvarid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("btype"), nonTerm("atype")).setElementType(GrammarPackage.getAPPLICATION_TYPE());
      addVar(variants, nonTerm("atype"));
      grammar.put("btype", new Rule("btype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OPAREN), nonTerm("exportlist"), term(CPAREN));
      addVar(variants, term(HIDING), term(OPAREN), nonTerm("exportlist"), term(CPAREN));
      grammar.put("impspec", new Rule("impspec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(DERIVING), term(INSTANCE), nonTerm("overlap_pragma"), nonTerm("inst_type"));
      grammar.put("stand_alone_deriving", new Rule("stand_alone_deriving", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("ralt"), nonTerm("wherebinds"));
      grammar.put("alt_rhs", new Rule("alt_rhs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(FORALL), nonTerm("tv_bndrs"), term(DOT));
      addVar(variants);
      grammar.put("forall", new Rule("forall", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("con"));
      addVar(variants, nonTerm("con"), term(COMMA), nonTerm("con_list"));
      grammar.put("con_list", new Rule("con_list", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(DATA));
      addVar(variants, term(NEWTYPE));
      grammar.put("data_or_newtype", new Rule("data_or_newtype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("infixexp"), term(DCOLON), nonTerm("sigtype"));
      addVar(variants, nonTerm("infixexp"), term(LARROWTAIL), nonTerm("exp"));
      addVar(variants, nonTerm("infixexp"), term(RARROWTAIL), nonTerm("exp"));
      addVar(variants, nonTerm("infixexp"), term(LLARROWTAIL), nonTerm("exp"));
      addVar(variants, nonTerm("infixexp"), term(RRARROWTAIL), nonTerm("exp"));
      addVar(variants, nonTerm("infixexp"));
      grammar.put("exp", new Rule("exp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(IDESCAPE));
      addVar(variants, term(PARENESCAPE), nonTerm("exp"), term(CPAREN));
      addVar(variants, term(IDTYESCAPE));
      addVar(variants, term(PARENTYESCAPE), nonTerm("exp"), term(CPAREN));
      grammar.put("splice_exp", new Rule("splice_exp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("exp"));
      addVar(variants, term(BANG), nonTerm("aexp"));
      grammar.put("bindpat", new Rule("bindpat", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(DEPRECATED_PRAG), nonTerm("strings"), term(CLOSE_PRAG));
      addVar(variants, term(WARNING_PRAG), nonTerm("strings"), term(CLOSE_PRAG));
      addVar(variants);
      grammar.put("maybemodwarning", new Rule("maybemodwarning", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("name_boolformula_atom"));
      addVar(variants, nonTerm("name_boolformula_atom"), term(COMMA), nonTerm("name_boolformula_and"));
      grammar.put("name_boolformula_and", new Rule("name_boolformula_and", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(CTYPE), term(STRING), term(STRING), term(CLOSE_PRAG));
      addVar(variants, term(CTYPE), term(STRING), term(CLOSE_PRAG));
      addVar(variants);
      grammar.put("capi_ctype", new Rule("capi_ctype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("gadt_constr"), term(SEMI), nonTerm("gadt_constrs"));
      addVar(variants, nonTerm("gadt_constr"));
      addVar(variants);
      grammar.put("gadt_constrs", new Rule("gadt_constrs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OCURLY), nonTerm("stmts"), term(CCURLY));
      addVar(variants, term(VOCURLY), nonTerm("stmts"), nonTerm("close"));
      grammar.put("stmtlist", new Rule("stmtlist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(SAFE));
      addVar(variants);
      grammar.put("maybe_safe", new Rule("maybe_safe", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OCURLY), nonTerm("decls_cls"), term(CCURLY));
      addVar(variants, term(VOCURLY), nonTerm("decls_cls"), nonTerm("close"));
      grammar.put("decllist_cls", new Rule("decllist_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("dbinds"), term(SEMI), nonTerm("dbind"));
      addVar(left, nonTerm("dbinds"), term(SEMI));
      addVar(variants, nonTerm("dbind"));
      grammar.put("dbinds", new Rule("dbinds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("stmt"), nonTerm("stmts_help"));
      addVar(variants, term(SEMI), nonTerm("stmts"));
      addVar(variants);
      grammar.put("stmts", new Rule("stmts", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("qcname"));
      addVar(variants, term(TYPE), nonTerm("qcname"));
      grammar.put("qcname_ext", new Rule("qcname_ext", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("ipvar"));
      addVar(variants, nonTerm("qcname")).setElementType(GrammarPackage.getQ_NAME_EXPRESSION());
      addVar(variants, nonTerm("literal"));
      addVar(variants, term(INTEGER));
      addVar(variants, term(RATIONAL));
      addVar(variants, term(OPAREN), nonTerm("texp"), term(CPAREN)).setElementType(GrammarPackage.getPARENTHESIS_EXPRESSION());
      addVar(variants, term(OPAREN), nonTerm("tup_exprs"), term(CPAREN));
      addVar(variants, term(OUBXPAREN), nonTerm("texp"), term(CUBXPAREN));
      addVar(variants, term(OUBXPAREN), nonTerm("tup_exprs"), term(CUBXPAREN));
      addVar(variants, term(OBRACK), nonTerm("list"), term(CBRACK));
      addVar(variants, term(OPABRACK), nonTerm("parr"), term(CPABRACK));
      addVar(variants, term(UNDERSCORE));
      addVar(variants, nonTerm("splice_exp"));
      addVar(variants, term(SIMPLEQUOTE), nonTerm("qvar"));
      addVar(variants, term(SIMPLEQUOTE), nonTerm("qcon"));
      addVar(variants, term(TYQUOTE), nonTerm("tyvar"));
      addVar(variants, term(TYQUOTE), nonTerm("gtycon"));
      addVar(variants, term(OPENEXPQUOTE), nonTerm("exp"), term(CLOSEQUOTE));
      addVar(variants, term(OPENTEXPQUOTE), nonTerm("exp"), term(CLOSETEXPQUOTE));
      addVar(variants, term(OPENTYPQUOTE), nonTerm("ctype"), term(CLOSEQUOTE));
      addVar(variants, term(OPENPATQUOTE), nonTerm("infixexp"), term(CLOSEQUOTE));
      addVar(variants, term(OPENDECQUOTE), nonTerm("cvtopbody"), term(CLOSEQUOTE));
      addVar(variants, nonTerm("quasiquote"));
      addVar(variants, term(OPARENBAR), nonTerm("aexp2"), nonTerm("cmdargs"), term(CPARENBAR));
      grammar.put("aexp2", new Rule("aexp2", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("aexp1"), term(OCURLY), nonTerm("fbinds"), term(CCURLY));
      addVar(variants, nonTerm("aexp2"));
      grammar.put("aexp1", new Rule("aexp1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("ntgtycon"));
      addVar(variants, term(OPAREN), term(CPAREN));
      addVar(variants, term(OUBXPAREN), term(CUBXPAREN));
      grammar.put("gtycon", new Rule("gtycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("alts1"));
      addVar(variants, term(SEMI), nonTerm("alts"));
      grammar.put("alts", new Rule("alts", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("kind"));
      addVar(variants, nonTerm("kind"), term(COMMA), nonTerm("comma_kinds1"));
      grammar.put("comma_kinds1", new Rule("comma_kinds1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, nonTerm("rule_explicit_activation"));
      grammar.put("rule_activation", new Rule("rule_activation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(STRING), nonTerm("rule_activation"), nonTerm("rule_forall"), nonTerm("infixexp"), term(EQUAL), nonTerm("exp"));
      grammar.put("rule", new Rule("rule", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("cmdargs"), nonTerm("acmd"));
      addVar(variants);
      grammar.put("cmdargs", new Rule("cmdargs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, term(OPAREN), term(DOTDOT), term(CPAREN));
      addVar(variants, term(OPAREN), term(CPAREN));
      addVar(variants, term(OPAREN), nonTerm("qcnames"), term(CPAREN));
      grammar.put("export_subspec", new Rule("export_subspec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, nonTerm("explicit_activation"));
      grammar.put("activation", new Rule("activation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("maybe_docnext"), term(EQUAL), nonTerm("constrs1"));
      grammar.put("constrs", new Rule("constrs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, nonTerm("fielddecls1"));
      grammar.put("fielddecls", new Rule("fielddecls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      grammar.put("missing_module_keyword", new Rule("missing_module_keyword", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(OCURLY), nonTerm("importdecls"));
      addVar(variants, term(VOCURLY), nonTerm("importdecls"));
      grammar.put("header_body", new Rule("header_body", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("name_var"));
      addVar(variants, nonTerm("name_var"), term(COMMA), nonTerm("namelist"));
      grammar.put("namelist", new Rule("namelist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("alts1"), term(SEMI), nonTerm("alt"));
      addVar(left, nonTerm("alts1"), term(SEMI));
      addVar(variants, nonTerm("alt"));
      grammar.put("alts1", new Rule("alts1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("texp"));
      addVar(variants, nonTerm("lexps"));
      addVar(variants, nonTerm("texp"), term(DOTDOT));
      addVar(variants, nonTerm("texp"), term(COMMA), nonTerm("exp"), term(DOTDOT));
      addVar(variants, nonTerm("texp"), term(DOTDOT), nonTerm("exp"));
      addVar(variants, nonTerm("texp"), term(COMMA), nonTerm("exp"), term(DOTDOT), nonTerm("exp"));
      addVar(variants, nonTerm("texp"), term(VBAR), nonTerm("flattenedpquals"));
      grammar.put("list", new Rule("list", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(SEMI));
      addVar(variants);
      grammar.put("optSemi", new Rule("optSemi", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("rules"), term(SEMI), nonTerm("rule"));
      addVar(left, nonTerm("rules"), term(SEMI));
      addVar(variants, nonTerm("rule"));
      addVar(variants);
      grammar.put("rules", new Rule("rules", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(UNSAFE));
      addVar(variants, term(SAFE));
      addVar(variants, term(INTERRUPTIBLE));
      grammar.put("safety", new Rule("safety", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("fielddecl"), nonTerm("maybe_docnext"), term(COMMA), nonTerm("maybe_docprev"), nonTerm("fielddecls1"));
      addVar(variants, nonTerm("fielddecl"));
      grammar.put("fielddecls1", new Rule("fielddecls1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("guardquals1"), term(COMMA), nonTerm("qual"));
      addVar(variants, nonTerm("qual"));
      grammar.put("guardquals1", new Rule("guardquals1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("texp"), nonTerm("commas_tup_tail"));
      addVar(variants, nonTerm("texp"));
      addVar(variants);
      grammar.put("tup_tail", new Rule("tup_tail", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("lexps"), term(COMMA), nonTerm("texp"));
      addVar(variants, nonTerm("texp"), term(COMMA), nonTerm("texp"));
      grammar.put("lexps", new Rule("lexps", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, nonTerm("btype"), term(TILDE), nonTerm("btype"));
      addVar(variants, nonTerm("btype"));
      grammar.put("context", new Rule("context", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("gdpats"), nonTerm("gdpat"));
      addVar(variants, nonTerm("gdpat"));
      grammar.put("gdpats", new Rule("gdpats", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("decls"), term(SEMI), nonTerm("decl"));
      addVar(left, nonTerm("decls"), term(SEMI));
      addVar(variants, nonTerm("decl"));
      addVar(variants);
      grammar.put("decls", new Rule("decls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, nonTerm("constrs1"), nonTerm("maybe_docnext"), term(VBAR), nonTerm("maybe_docprev"), nonTerm("constr"));
      addVar(variants, nonTerm("constr"));
      grammar.put("constrs1", new Rule("constrs1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, term(CHAR));
      addVar(variants, term(STRING)).setElementType(GrammarPackage.getSTRING_LITERAL());
      addVar(variants, term(PRIMINT));
      addVar(variants, term(PRIMWORD));
      addVar(variants, term(PRIMCHAR));
      addVar(variants, term(PRIMSTRING));
      addVar(variants, term(PRIMFLOAT));
      addVar(variants, term(PRIMDOUBLE));
      grammar.put("literal", new Rule("literal", variants, left));
    }
    return grammar;
  }
}
