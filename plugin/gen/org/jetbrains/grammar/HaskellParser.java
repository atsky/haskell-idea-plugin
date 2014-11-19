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
      addVar(variants, new Terminal(BANG));
      addVar(variants, new Terminal(UNPACK_PRAG), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(NOUNPACK_PRAG), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(UNPACK_PRAG), new Terminal(CLOSE_PRAG), new Terminal(BANG));
      addVar(variants, new Terminal(NOUNPACK_PRAG), new Terminal(CLOSE_PRAG), new Terminal(BANG));
      grammar.put("strict_mark", new Rule("strict_mark", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(WHERE), new NonTerminal("decllist_inst"));
      addVar(variants);
      grammar.put("where_inst", new Rule("where_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("tyvar"));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("tyvar"), new Terminal(DCOLON), new NonTerminal("kind"), new Terminal(CPAREN));
      grammar.put("tv_bndr", new Rule("tv_bndr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("squals"), new Terminal(COMMA), new NonTerminal("transformqual"));
      addVar(left, new NonTerminal("squals"), new Terminal(COMMA), new NonTerminal("qual"));
      addVar(variants, new NonTerminal("transformqual"));
      addVar(variants, new NonTerminal("qual"));
      grammar.put("squals", new Rule("squals", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qvarop"));
      addVar(variants, new NonTerminal("qconop"));
      grammar.put("qop", new Rule("qop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new NonTerminal("cvtopdecls"));
      grammar.put("cvtopdecls0", new Rule("cvtopdecls0", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("rule_var"));
      addVar(variants, new NonTerminal("rule_var"), new NonTerminal("rule_var_list"));
      grammar.put("rule_var_list", new Rule("rule_var_list", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(SEMI), new NonTerminal("stmts"));
      addVar(variants);
      grammar.put("stmts_help", new Rule("stmts_help", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(VARID));
      addVar(variants, new Terminal(UNDERSCORE));
      grammar.put("role", new Rule("role", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(EQUAL), new NonTerminal("exp"), new NonTerminal("wherebinds"));
      addVar(variants, new NonTerminal("gdrhs"), new NonTerminal("wherebinds"));
      grammar.put("rhs", new Rule("rhs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OCURLY), new NonTerminal("decls_inst"), new Terminal(CCURLY));
      addVar(variants, new Terminal(VOCURLY), new NonTerminal("decls_inst"), new NonTerminal("close"));
      grammar.put("decllist_inst", new Rule("decllist_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qvarsym_no_minus"));
      addVar(variants, new Terminal(BACKQUOTE), new NonTerminal("qvarid"), new Terminal(BACKQUOTE));
      grammar.put("qvaropm", new Rule("qvaropm", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(QVARSYM));
      grammar.put("qvarsym1", new Rule("qvarsym1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(TYPE), new NonTerminal("type"), new Terminal(EQUAL), new NonTerminal("ctypedoc"));
      addVar(variants, new Terminal(TYPE), new Terminal(FAMILY), new NonTerminal("type"), new NonTerminal("opt_kind_sig"), new NonTerminal("where_type_family"));
      addVar(variants, new NonTerminal("data_or_newtype"), new NonTerminal("capi_ctype"), new NonTerminal("tycl_hdr"), new NonTerminal("constrs"), new NonTerminal("deriving"));
      addVar(variants, new NonTerminal("data_or_newtype"), new NonTerminal("capi_ctype"), new NonTerminal("tycl_hdr"), new NonTerminal("opt_kind_sig"), new NonTerminal("gadt_constrlist"), new NonTerminal("deriving"));
      addVar(variants, new Terminal(DATA), new Terminal(FAMILY), new NonTerminal("type"), new NonTerminal("opt_kind_sig"));
      grammar.put("ty_decl", new Rule("ty_decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("exp10"));
      addVar(left, new NonTerminal("infixexp"), new NonTerminal("qop"), new NonTerminal("exp10"));
      grammar.put("infixexp", new Rule("infixexp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("topdecls"), new Terminal(SEMI), new NonTerminal("topdecl"));
      addVar(left, new NonTerminal("topdecls"), new Terminal(SEMI));
      addVar(variants, new NonTerminal("topdecl"));
      grammar.put("topdecls", new Rule("topdecls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("docsection"));
      addVar(variants, new NonTerminal("docnamed"));
      addVar(variants, new NonTerminal("docnext"));
      grammar.put("exp_doc", new Rule("exp_doc", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("maybedocheader"), new Terminal(MODULE), new NonTerminal("modid"), new NonTerminal("maybemodwarning"), new NonTerminal("maybeexports"), new Terminal(WHERE), new NonTerminal("header_body"));
      addVar(variants, new NonTerminal("header_body2"));
      grammar.put("header", new Rule("header", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new Terminal(FAMILY));
      grammar.put("opt_family", new Rule("opt_family", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(CONID));
      grammar.put("conid", new Rule("conid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(DOCCOMMENTNEXT));
      grammar.put("docnext", new Rule("docnext", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OCURLY), new NonTerminal("ty_fam_inst_eqns"), new Terminal(CCURLY));
      addVar(variants, new Terminal(VOCURLY), new NonTerminal("ty_fam_inst_eqns"), new NonTerminal("close"));
      addVar(variants, new Terminal(OCURLY), new Terminal(DOTDOT), new Terminal(CCURLY));
      addVar(variants, new Terminal(VOCURLY), new Terminal(DOTDOT), new NonTerminal("close"));
      grammar.put("ty_fam_inst_eqn_list", new Rule("ty_fam_inst_eqn_list", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(GENERATED_PRAG), new Terminal(STRING), new Terminal(INTEGER), new Terminal(COLON), new Terminal(INTEGER), new Terminal(MINUS), new Terminal(INTEGER), new Terminal(COLON), new Terminal(INTEGER), new Terminal(CLOSE_PRAG));
      grammar.put("hpc_annot", new Rule("hpc_annot", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("role"));
      addVar(left, new NonTerminal("roles"), new NonTerminal("role"));
      grammar.put("roles", new Rule("roles", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qtyconsym"));
      addVar(variants, new Terminal(BACKQUOTE), new NonTerminal("qtycon"), new Terminal(BACKQUOTE));
      grammar.put("qtyconop", new Rule("qtyconop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("maybe_docnext"), new NonTerminal("forall"), new NonTerminal("context"), new Terminal(DARROW), new NonTerminal("constr_stuff"), new NonTerminal("maybe_docprev"));
      addVar(variants, new NonTerminal("maybe_docnext"), new NonTerminal("forall"), new NonTerminal("constr_stuff"), new NonTerminal("maybe_docprev"));
      grammar.put("constr", new Rule("constr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("docnext"));
      addVar(variants);
      grammar.put("maybe_docnext", new Rule("maybe_docnext", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(SOURCE_PRAG), new Terminal(CLOSE_PRAG));
      addVar(variants);
      grammar.put("maybe_src", new Rule("maybe_src", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new Terminal(INTEGER));
      grammar.put("prec", new Rule("prec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("cl_decl")).setElementType(GrammarPackage.getCLASS_DECLARATION());
      addVar(variants, new NonTerminal("ty_decl"));
      addVar(variants, new NonTerminal("inst_decl"));
      addVar(variants, new NonTerminal("stand_alone_deriving"));
      addVar(variants, new NonTerminal("role_annot"));
      addVar(variants, new Terminal(DEFAULT), new Terminal(OPAREN), new NonTerminal("comma_types0"), new Terminal(CPAREN));
      addVar(variants, new Terminal(FOREIGN), new NonTerminal("fdecl"));
      addVar(variants, new Terminal(DEPRECATED_PRAG), new NonTerminal("deprecations"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(WARNING_PRAG), new NonTerminal("warnings"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(RULES_PRAG), new NonTerminal("rules"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(VECT_PRAG), new NonTerminal("qvar"), new Terminal(EQUAL), new NonTerminal("exp"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(NOVECT_PRAG), new NonTerminal("qvar"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(VECT_PRAG), new Terminal(TYPE), new NonTerminal("gtycon"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(VECT_SCALAR_PRAG), new Terminal(TYPE), new NonTerminal("gtycon"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(VECT_PRAG), new Terminal(TYPE), new NonTerminal("gtycon"), new Terminal(EQUAL), new NonTerminal("gtycon"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(VECT_SCALAR_PRAG), new Terminal(TYPE), new NonTerminal("gtycon"), new Terminal(EQUAL), new NonTerminal("gtycon"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(VECT_PRAG), new Terminal(CLASS), new NonTerminal("gtycon"), new Terminal(CLOSE_PRAG));
      addVar(variants, new NonTerminal("annotation"));
      addVar(variants, new NonTerminal("decl_no_th"));
      addVar(variants, new NonTerminal("infixexp"));
      grammar.put("topdecl", new Rule("topdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new Terminal(DCOLON), new NonTerminal("atype"));
      grammar.put("opt_asig", new Rule("opt_asig", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qconid"));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("qconsym"), new Terminal(CPAREN));
      addVar(variants, new NonTerminal("sysdcon"));
      grammar.put("qcon", new Rule("qcon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qconsym"));
      addVar(variants, new Terminal(BACKQUOTE), new NonTerminal("qconid"), new Terminal(BACKQUOTE));
      grammar.put("qconop", new Rule("qconop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("namelist"), new NonTerminal("strings"));
      grammar.put("deprecation", new Rule("deprecation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("topdecls"));
      grammar.put("cvtopdecls", new Rule("cvtopdecls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("varsym_no_minus"));
      addVar(variants, new Terminal(MINUS));
      grammar.put("varsym", new Rule("varsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OCURLY), new NonTerminal("importdecls"));
      addVar(variants, new NonTerminal("missing_module_keyword"), new NonTerminal("importdecls"));
      grammar.put("header_body2", new Rule("header_body2", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("at_decl_cls"));
      addVar(variants, new NonTerminal("decl"));
      addVar(variants, new Terminal(DEFAULT), new NonTerminal("infixexp"), new Terminal(DCOLON), new NonTerminal("sigtypedoc"));
      grammar.put("decl_cls", new Rule("decl_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(TYPE), new Terminal(ROLE), new NonTerminal("oqtycon"), new NonTerminal("maybe_roles"));
      grammar.put("role_annot", new Rule("role_annot", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("sigtype"));
      grammar.put("inst_type", new Rule("inst_type", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("qcnames"), new Terminal(COMMA), new NonTerminal("qcname_ext"));
      addVar(variants, new NonTerminal("qcname_ext"));
      grammar.put("qcnames", new Rule("qcnames", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("exp_doc"), new NonTerminal("expdoclist"));
      addVar(variants);
      grammar.put("expdoclist", new Rule("expdoclist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(DOCCOMMENTPREV));
      grammar.put("docprev", new Rule("docprev", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("warnings"), new Terminal(SEMI), new NonTerminal("warning"));
      addVar(left, new NonTerminal("warnings"), new Terminal(SEMI));
      addVar(variants, new NonTerminal("warning"));
      addVar(variants);
      grammar.put("warnings", new Rule("warnings", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("decl_no_th"));
      addVar(variants, new NonTerminal("splice_exp"));
      grammar.put("decl", new Rule("decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("varid"));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("varid"), new Terminal(DCOLON), new NonTerminal("ctype"), new Terminal(CPAREN));
      grammar.put("rule_var", new Rule("rule_var", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("context"), new Terminal(DARROW), new NonTerminal("type"));
      addVar(variants, new NonTerminal("type"));
      grammar.put("tycl_hdr", new Rule("tycl_hdr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new NonTerminal("varid"), new NonTerminal("vars0"));
      grammar.put("vars0", new Rule("vars0", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("varids0"), new Terminal(RARROW), new NonTerminal("varids0"));
      grammar.put("fd", new Rule("fd", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(DOCSECTION));
      grammar.put("docsection", new Rule("docsection", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qvarid"));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("varsym"), new Terminal(CPAREN));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("qvarsym1"), new Terminal(CPAREN));
      grammar.put("qvar", new Rule("qvar", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OCURLY), new NonTerminal("top"), new Terminal(CCURLY));
      addVar(variants, new NonTerminal("missing_module_keyword"), new NonTerminal("top"), new NonTerminal("close"));
      grammar.put("body2", new Rule("body2", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qtycon"));
      addVar(variants, new Terminal(OPAREN), new Terminal(CPAREN));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("kind"), new Terminal(COMMA), new NonTerminal("comma_kinds1"), new Terminal(CPAREN));
      addVar(variants, new Terminal(OBRACK), new NonTerminal("kind"), new Terminal(CBRACK));
      grammar.put("pkind", new Rule("pkind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("type"), new Terminal(EQUAL), new NonTerminal("ctype"));
      grammar.put("ty_fam_inst_eqn", new Rule("ty_fam_inst_eqn", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OCURLY), new NonTerminal("decls"), new Terminal(CCURLY));
      addVar(variants, new Terminal(VOCURLY), new NonTerminal("decls"), new NonTerminal("close"));
      grammar.put("decllist", new Rule("decllist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("fbind"), new Terminal(COMMA), new NonTerminal("fbinds1"));
      addVar(variants, new NonTerminal("fbind"));
      addVar(variants, new Terminal(DOTDOT));
      grammar.put("fbinds1", new Rule("fbinds1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("name_boolformula_and"));
      addVar(variants, new NonTerminal("name_boolformula_and"), new Terminal(VBAR), new NonTerminal("name_boolformula"));
      grammar.put("name_boolformula", new Rule("name_boolformula", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qvarsym"));
      addVar(variants, new Terminal(BACKQUOTE), new NonTerminal("qvarid"), new Terminal(BACKQUOTE));
      grammar.put("qvarop", new Rule("qvarop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("fds1"), new Terminal(COMMA), new NonTerminal("fd"));
      addVar(variants, new NonTerminal("fd"));
      grammar.put("fds1", new Rule("fds1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("bindpat"), new Terminal(LARROW), new NonTerminal("exp"));
      addVar(variants, new NonTerminal("exp"));
      addVar(variants, new Terminal(LET), new NonTerminal("binds"));
      grammar.put("qual", new Rule("qual", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(CONID)).setElementType(GrammarPackage.getMODULE_NAME());
      addVar(variants, new Terminal(QCONID)).setElementType(GrammarPackage.getMODULE_NAME());
      grammar.put("modid", new Rule("modid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new NonTerminal("texp"));
      addVar(variants, new NonTerminal("lexps"));
      addVar(variants, new NonTerminal("texp"), new Terminal(DOTDOT), new NonTerminal("exp"));
      addVar(variants, new NonTerminal("texp"), new Terminal(COMMA), new NonTerminal("exp"), new Terminal(DOTDOT), new NonTerminal("exp"));
      addVar(variants, new NonTerminal("texp"), new Terminal(VBAR), new NonTerminal("flattenedpquals"));
      grammar.put("parr", new Rule("parr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("conid"));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("consym"), new Terminal(CPAREN));
      addVar(variants, new NonTerminal("sysdcon"));
      grammar.put("con", new Rule("con", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OCURLY), new NonTerminal("top"), new Terminal(CCURLY));
      addVar(variants, new Terminal(VOCURLY), new NonTerminal("top"), new NonTerminal("close"));
      grammar.put("body", new Rule("body", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("squals"), new Terminal(VBAR), new NonTerminal("pquals"));
      addVar(variants, new NonTerminal("squals"));
      grammar.put("pquals", new Rule("pquals", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(VCCURLY));
      grammar.put("close", new Rule("close", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("pquals"));
      grammar.put("flattenedpquals", new Rule("flattenedpquals", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("fexp"), new NonTerminal("aexp"));
      addVar(variants, new NonTerminal("aexp"));
      grammar.put("fexp", new Rule("fexp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("ctype"));
      addVar(variants, new NonTerminal("ctype"), new Terminal(COMMA), new NonTerminal("comma_types1"));
      grammar.put("comma_types1", new Rule("comma_types1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qvar"));
      addVar(variants, new NonTerminal("qcon"));
      grammar.put("qcname", new Rule("qcname", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(AS));
      addVar(variants, new Terminal(QUALIFIED));
      addVar(variants, new Terminal(HIDING));
      addVar(variants, new Terminal(EXPORT));
      addVar(variants, new Terminal(LABEL));
      addVar(variants, new Terminal(DYNAMIC));
      addVar(variants, new Terminal(STDCALLCONV));
      addVar(variants, new Terminal(CCALLCONV));
      addVar(variants, new Terminal(CAPICONV));
      addVar(variants, new Terminal(PRIMCALLCONV));
      addVar(variants, new Terminal(JAVASCRIPTCALLCONV));
      addVar(variants, new Terminal(GROUP));
      grammar.put("special_id", new Rule("special_id", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("comma_types1"));
      addVar(variants);
      grammar.put("comma_types0", new Rule("comma_types0", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("expdoclist"), new Terminal(COMMA), new NonTerminal("expdoclist"));
      addVar(variants, new NonTerminal("exportlist1"));
      grammar.put("exportlist", new Rule("exportlist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(LAM), new NonTerminal("apat"), new NonTerminal("apats"), new NonTerminal("opt_asig"), new Terminal(RARROW), new NonTerminal("exp"));
      addVar(variants, new Terminal(LET), new NonTerminal("binds"), new Terminal(IN), new NonTerminal("exp"));
      addVar(variants, new Terminal(LAM), new Terminal(LCASE), new NonTerminal("altslist"));
      addVar(variants, new Terminal(IF), new NonTerminal("exp"), new NonTerminal("optSemi"), new Terminal(THEN), new NonTerminal("exp"), new NonTerminal("optSemi"), new Terminal(ELSE), new NonTerminal("exp"));
      addVar(variants, new Terminal(IF), new NonTerminal("ifgdpats"));
      addVar(variants, new Terminal(CASE), new NonTerminal("exp"), new Terminal(OF), new NonTerminal("altslist"));
      addVar(variants, new Terminal(MINUS), new NonTerminal("fexp"));
      addVar(variants, new Terminal(DO), new NonTerminal("stmtlist"));
      addVar(variants, new Terminal(MDO), new NonTerminal("stmtlist"));
      addVar(variants, new NonTerminal("scc_annot"), new NonTerminal("exp"));
      addVar(variants, new NonTerminal("hpc_annot"), new NonTerminal("exp"));
      addVar(variants, new Terminal(PROC), new NonTerminal("aexp"), new Terminal(RARROW), new NonTerminal("exp"));
      addVar(variants, new Terminal(CORE_PRAG), new Terminal(STRING), new Terminal(CLOSE_PRAG), new NonTerminal("exp"));
      addVar(variants, new NonTerminal("fexp"));
      grammar.put("exp10", new Rule("exp10", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(WHERE), new Terminal(OCURLY), new NonTerminal("gadt_constrs"), new Terminal(CCURLY));
      addVar(variants, new Terminal(WHERE), new Terminal(VOCURLY), new NonTerminal("gadt_constrs"), new NonTerminal("close"));
      addVar(variants);
      grammar.put("gadt_constrlist", new Rule("gadt_constrlist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("ipvar"), new Terminal(EQUAL), new NonTerminal("exp"));
      grammar.put("dbind", new Rule("dbind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new Terminal(DCOLON), new NonTerminal("kind"));
      grammar.put("opt_kind_sig", new Rule("opt_kind_sig", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(WHERE), new NonTerminal("decllist_cls"));
      addVar(variants);
      grammar.put("where_cls", new Rule("where_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("exp"));
      addVar(variants, new NonTerminal("infixexp"), new NonTerminal("qop"));
      addVar(variants, new NonTerminal("qopm"), new NonTerminal("infixexp"));
      addVar(variants, new NonTerminal("exp"), new Terminal(RARROW), new NonTerminal("texp"));
      grammar.put("texp", new Rule("texp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(VBAR), new NonTerminal("guardquals"), new Terminal(RARROW), new NonTerminal("exp"));
      grammar.put("gdpat", new Rule("gdpat", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("varsym"));
      addVar(variants, new Terminal(BACKQUOTE), new NonTerminal("varid"), new Terminal(BACKQUOTE));
      grammar.put("varop", new Rule("varop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("gdpatssemi"), new NonTerminal("gdpat"), new NonTerminal("optSemi"));
      addVar(variants, new NonTerminal("gdpat"), new NonTerminal("optSemi"));
      grammar.put("gdpatssemi", new Rule("gdpatssemi", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("deprecations"), new Terminal(SEMI), new NonTerminal("deprecation"));
      addVar(left, new NonTerminal("deprecations"), new Terminal(SEMI));
      addVar(variants, new NonTerminal("deprecation"));
      addVar(variants);
      grammar.put("deprecations", new Rule("deprecations", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OBRACK), new Terminal(INTEGER), new Terminal(CBRACK));
      addVar(variants, new Terminal(OBRACK), new Terminal(TILDE), new Terminal(INTEGER), new Terminal(CBRACK));
      addVar(variants, new Terminal(OBRACK), new Terminal(TILDE), new Terminal(CBRACK));
      grammar.put("rule_explicit_activation", new Rule("rule_explicit_activation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(RARROW), new NonTerminal("exp"));
      addVar(variants, new NonTerminal("gdpats"));
      grammar.put("ralt", new Rule("ralt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(DATA), new NonTerminal("opt_family"), new NonTerminal("type"), new NonTerminal("opt_kind_sig"));
      addVar(variants, new Terminal(TYPE), new NonTerminal("type"), new NonTerminal("opt_kind_sig"));
      addVar(variants, new Terminal(TYPE), new Terminal(FAMILY), new NonTerminal("type"), new NonTerminal("opt_kind_sig"));
      addVar(variants, new Terminal(TYPE), new NonTerminal("ty_fam_inst_eqn"));
      addVar(variants, new Terminal(TYPE), new Terminal(INSTANCE), new NonTerminal("ty_fam_inst_eqn"));
      grammar.put("at_decl_cls", new Rule("at_decl_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OCURLY), new NonTerminal("alts"), new Terminal(CCURLY));
      addVar(variants, new Terminal(VOCURLY), new NonTerminal("alts"), new NonTerminal("close"));
      addVar(variants, new Terminal(OCURLY), new Terminal(CCURLY));
      addVar(variants, new Terminal(VOCURLY), new NonTerminal("close"));
      grammar.put("altslist", new Rule("altslist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(DOCCOMMENTNEXT));
      grammar.put("moduleheader", new Rule("moduleheader", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(WHERE), new NonTerminal("binds"));
      addVar(variants);
      grammar.put("wherebinds", new Rule("wherebinds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(QUASIQUOTE));
      addVar(variants, new Terminal(QQUASIQUOTE));
      grammar.put("quasiquote", new Rule("quasiquote", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("varid"));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("varsym"), new Terminal(CPAREN));
      grammar.put("var", new Rule("var", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(STRING));
      addVar(variants, new Terminal(OBRACK), new NonTerminal("stringlist"), new Terminal(CBRACK));
      grammar.put("strings", new Rule("strings", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new Terminal(VBAR), new NonTerminal("fds1"));
      grammar.put("fds", new Rule("fds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("sigtype"));
      addVar(variants, new NonTerminal("sigtype"), new Terminal(COMMA), new NonTerminal("sigtypes1"));
      grammar.put("sigtypes1", new Rule("sigtypes1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("stmt"));
      addVar(variants);
      grammar.put("maybe_stmt", new Rule("maybe_stmt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("tv_bndr"), new NonTerminal("tv_bndrs"));
      addVar(variants);
      grammar.put("tv_bndrs", new Rule("tv_bndrs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("infixexp"), new Terminal(DCOLON), new NonTerminal("sigtypedoc"));
      addVar(variants, new NonTerminal("var"), new Terminal(COMMA), new NonTerminal("sig_vars"), new Terminal(DCOLON), new NonTerminal("sigtypedoc"));
      addVar(variants, new NonTerminal("infix"), new NonTerminal("prec"), new NonTerminal("ops"));
      addVar(variants, new Terminal(INLINE_PRAG), new NonTerminal("activation"), new NonTerminal("qvar"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(SPEC_PRAG), new NonTerminal("activation"), new NonTerminal("qvar"), new Terminal(DCOLON), new NonTerminal("sigtypes1"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(SPEC_INLINE_PRAG), new NonTerminal("activation"), new NonTerminal("qvar"), new Terminal(DCOLON), new NonTerminal("sigtypes1"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(SPEC_PRAG), new Terminal(INSTANCE), new NonTerminal("inst_type"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(MINIMAL_PRAG), new NonTerminal("name_boolformula_opt"), new Terminal(CLOSE_PRAG));
      grammar.put("sigdecl", new Rule("sigdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(THEN), new NonTerminal("exp"));
      addVar(variants, new Terminal(THEN), new NonTerminal("exp"), new Terminal(BY), new NonTerminal("exp"));
      addVar(variants, new Terminal(THEN), new Terminal(GROUP), new Terminal(USING), new NonTerminal("exp"));
      addVar(variants, new Terminal(THEN), new Terminal(GROUP), new Terminal(BY), new NonTerminal("exp"), new Terminal(USING), new NonTerminal("exp"));
      grammar.put("transformqual", new Rule("transformqual", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("exp"));
      addVar(variants, new Terminal(BANG), new NonTerminal("aexp"));
      grammar.put("pat", new Rule("pat", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("pat"), new NonTerminal("opt_sig"), new NonTerminal("alt_rhs"));
      grammar.put("alt", new Rule("alt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(FORALL), new NonTerminal("rule_var_list"), new Terminal(DOT));
      addVar(variants);
      grammar.put("rule_forall", new Rule("rule_forall", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(STDCALLCONV));
      addVar(variants, new Terminal(CCALLCONV));
      addVar(variants, new Terminal(CAPICONV));
      addVar(variants, new Terminal(PRIMCALLCONV));
      addVar(variants, new Terminal(JAVASCRIPTCALLCONV));
      grammar.put("callconv", new Rule("callconv", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(STRING), new NonTerminal("var"), new Terminal(DCOLON), new NonTerminal("sigtypedoc"));
      addVar(variants, new NonTerminal("var"), new Terminal(DCOLON), new NonTerminal("sigtypedoc"));
      grammar.put("fspec", new Rule("fspec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("consym"));
      addVar(variants, new Terminal(BACKQUOTE), new NonTerminal("conid"), new Terminal(BACKQUOTE));
      grammar.put("conop", new Rule("conop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("importdecls"));
      addVar(variants, new NonTerminal("importdecls"), new Terminal(SEMI), new NonTerminal("cvtopdecls"));
      addVar(variants, new NonTerminal("cvtopdecls"));
      grammar.put("top", new Rule("top", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qcname_ext"), new NonTerminal("export_subspec"));
      addVar(variants, new Terminal(MODULE), new NonTerminal("modid"));
      addVar(variants, new Terminal(PATTERN), new NonTerminal("qcon"));
      grammar.put("export", new Rule("export", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OPAREN), new NonTerminal("exportlist"), new Terminal(CPAREN));
      addVar(variants);
      grammar.put("maybeexports", new Rule("maybeexports", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("gdrhs"), new NonTerminal("gdrh"));
      addVar(variants, new NonTerminal("gdrh"));
      grammar.put("gdrhs", new Rule("gdrhs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qual"));
      addVar(variants, new Terminal(REC), new NonTerminal("stmtlist"));
      grammar.put("stmt", new Rule("stmt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(FORALL), new NonTerminal("tv_bndrs"), new Terminal(DOT), new NonTerminal("ctype"));
      addVar(variants, new NonTerminal("context"), new Terminal(DARROW), new NonTerminal("ctype"));
      addVar(variants, new NonTerminal("ipvar"), new Terminal(DCOLON), new NonTerminal("type"));
      addVar(variants, new NonTerminal("type"));
      grammar.put("ctype", new Rule("ctype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OPAREN), new NonTerminal("name_boolformula"), new Terminal(CPAREN));
      addVar(variants, new NonTerminal("name_var"));
      grammar.put("name_boolformula_atom", new Rule("name_boolformula_atom", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(IMPORT), new NonTerminal("callconv"), new NonTerminal("safety"), new NonTerminal("fspec"));
      addVar(variants, new Terminal(IMPORT), new NonTerminal("callconv"), new NonTerminal("fspec"));
      addVar(variants, new Terminal(EXPORT), new NonTerminal("callconv"), new NonTerminal("fspec"));
      grammar.put("fdecl", new Rule("fdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("inst_type"));
      addVar(variants, new NonTerminal("inst_type"), new Terminal(COMMA), new NonTerminal("inst_types1"));
      grammar.put("inst_types1", new Rule("inst_types1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("oqtycon"));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("commas"), new Terminal(CPAREN));
      addVar(variants, new Terminal(OUBXPAREN), new NonTerminal("commas"), new Terminal(CUBXPAREN));
      addVar(variants, new Terminal(OPAREN), new Terminal(RARROW), new Terminal(CPAREN));
      addVar(variants, new Terminal(OBRACK), new Terminal(CBRACK));
      addVar(variants, new Terminal(OPABRACK), new Terminal(CPABRACK));
      addVar(variants, new Terminal(OPAREN), new Terminal(TILDEHSH), new Terminal(CPAREN));
      grammar.put("ntgtycon", new Rule("ntgtycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("decls_cls"), new Terminal(SEMI), new NonTerminal("decl_cls"));
      addVar(left, new NonTerminal("decls_cls"), new Terminal(SEMI));
      addVar(variants, new NonTerminal("decl_cls"));
      addVar(variants);
      grammar.put("decls_cls", new Rule("decls_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(TYPE), new NonTerminal("ty_fam_inst_eqn"));
      addVar(variants, new NonTerminal("data_or_newtype"), new NonTerminal("capi_ctype"), new NonTerminal("tycl_hdr"), new NonTerminal("constrs"), new NonTerminal("deriving"));
      addVar(variants, new NonTerminal("data_or_newtype"), new NonTerminal("capi_ctype"), new NonTerminal("tycl_hdr"), new NonTerminal("opt_kind_sig"), new NonTerminal("gadt_constrlist"), new NonTerminal("deriving"));
      grammar.put("at_decl_inst", new Rule("at_decl_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(AS), new NonTerminal("modid"));
      addVar(variants);
      grammar.put("maybeas", new Rule("maybeas", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(STAR));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("kind"), new Terminal(CPAREN));
      addVar(variants, new NonTerminal("pkind"));
      addVar(variants, new NonTerminal("tyvar"));
      grammar.put("akind", new Rule("akind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new NonTerminal("roles"));
      grammar.put("maybe_roles", new Rule("maybe_roles", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(SCC_PRAG), new Terminal(STRING), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(SCC_PRAG), new Terminal(VARID), new Terminal(CLOSE_PRAG));
      grammar.put("scc_annot", new Rule("scc_annot", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(CLASS), new NonTerminal("tycl_hdr"), new NonTerminal("fds"), new NonTerminal("where_cls"));
      grammar.put("cl_decl", new Rule("cl_decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("conid"));
      addVar(variants, new Terminal(QCONID));
      addVar(variants, new Terminal(PREFIXQCONSYM));
      grammar.put("qconid", new Rule("qconid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(IMPORT), new NonTerminal("maybe_src"), new NonTerminal("maybe_safe"), new NonTerminal("optqualified"), new NonTerminal("maybe_pkg"), new NonTerminal("modid"), new NonTerminal("maybeas"), new NonTerminal("maybeimpspec"));
      grammar.put("importdecl", new Rule("importdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("texp"), new NonTerminal("commas_tup_tail"));
      addVar(variants, new NonTerminal("commas"), new NonTerminal("tup_tail"));
      grammar.put("tup_exprs", new Rule("tup_exprs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(DOCCOMMENTNAMED));
      grammar.put("docnamed", new Rule("docnamed", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(left, new NonTerminal("varids0"), new NonTerminal("tyvar"));
      grammar.put("varids0", new Rule("varids0", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("ty_fam_inst_eqns"), new Terminal(SEMI), new NonTerminal("ty_fam_inst_eqn"));
      addVar(left, new NonTerminal("ty_fam_inst_eqns"), new Terminal(SEMI));
      addVar(variants, new NonTerminal("ty_fam_inst_eqn"));
      grammar.put("ty_fam_inst_eqns", new Rule("ty_fam_inst_eqns", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qtycon"));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("qtyconsym"), new Terminal(CPAREN));
      addVar(variants, new Terminal(OPAREN), new Terminal(TILDE), new Terminal(CPAREN));
      grammar.put("oqtycon", new Rule("oqtycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(ANN_PRAG), new NonTerminal("name_var"), new NonTerminal("aexp"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(ANN_PRAG), new Terminal(TYPE), new NonTerminal("tycon"), new NonTerminal("aexp"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(ANN_PRAG), new Terminal(MODULE), new NonTerminal("aexp"), new Terminal(CLOSE_PRAG));
      grammar.put("annotation", new Rule("annotation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("guardquals1"));
      grammar.put("guardquals", new Rule("guardquals", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(INFIX));
      addVar(variants, new Terminal(INFIXL));
      addVar(variants, new Terminal(INFIXR));
      grammar.put("infix", new Rule("infix", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("name_boolformula"));
      addVar(variants);
      grammar.put("name_boolformula_opt", new Rule("name_boolformula_opt", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(PATTERN), new NonTerminal("pat"), new Terminal(EQUAL), new NonTerminal("pat"));
      addVar(variants, new Terminal(PATTERN), new NonTerminal("pat"), new Terminal(LARROW), new NonTerminal("pat"));
      addVar(variants, new Terminal(PATTERN), new NonTerminal("pat"), new Terminal(LARROW), new NonTerminal("pat"), new NonTerminal("where_decls"));
      grammar.put("pattern_synonym_decl", new Rule("pattern_synonym_decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("ctypedoc"));
      grammar.put("sigtypedoc", new Rule("sigtypedoc", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(CONSYM));
      addVar(variants, new Terminal(VARSYM));
      addVar(variants, new Terminal(STAR));
      addVar(variants, new Terminal(MINUS));
      grammar.put("tyconsym", new Rule("tyconsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OBRACK), new Terminal(INTEGER), new Terminal(CBRACK));
      addVar(variants, new Terminal(OBRACK), new Terminal(TILDE), new Terminal(INTEGER), new Terminal(CBRACK));
      grammar.put("explicit_activation", new Rule("explicit_activation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("ops"), new Terminal(COMMA), new NonTerminal("op"));
      addVar(variants, new NonTerminal("op"));
      grammar.put("ops", new Rule("ops", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("docprev"));
      addVar(variants);
      grammar.put("maybe_docprev", new Rule("maybe_docprev", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(WHERE), new Terminal(OCURLY), new NonTerminal("decls"), new Terminal(CCURLY));
      addVar(variants, new Terminal(WHERE), new Terminal(VOCURLY), new NonTerminal("decls"), new NonTerminal("close"));
      grammar.put("where_decls", new Rule("where_decls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(INSTANCE), new NonTerminal("overlap_pragma"), new NonTerminal("inst_type"), new NonTerminal("where_inst"));
      addVar(variants, new Terminal(TYPE), new Terminal(INSTANCE), new NonTerminal("ty_fam_inst_eqn"));
      addVar(variants, new NonTerminal("data_or_newtype"), new Terminal(INSTANCE), new NonTerminal("capi_ctype"), new NonTerminal("tycl_hdr"), new NonTerminal("constrs"), new NonTerminal("deriving"));
      addVar(variants, new NonTerminal("data_or_newtype"), new Terminal(INSTANCE), new NonTerminal("capi_ctype"), new NonTerminal("tycl_hdr"), new NonTerminal("opt_kind_sig"), new NonTerminal("gadt_constrlist"), new NonTerminal("deriving"));
      grammar.put("inst_decl", new Rule("inst_decl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("con_list"), new Terminal(DCOLON), new NonTerminal("sigtype"));
      addVar(variants, new NonTerminal("oqtycon"), new Terminal(OCURLY), new NonTerminal("fielddecls"), new Terminal(CCURLY), new Terminal(DCOLON), new NonTerminal("sigtype"));
      grammar.put("gadt_constr", new Rule("gadt_constr", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("varop"));
      addVar(variants, new NonTerminal("conop"));
      grammar.put("op", new Rule("op", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("sig_vars"), new Terminal(COMMA), new NonTerminal("var"));
      addVar(variants, new NonTerminal("var"));
      grammar.put("sig_vars", new Rule("sig_vars", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("varsym"));
      addVar(variants, new NonTerminal("qvarsym1"));
      grammar.put("qvarsym", new Rule("qvarsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("tyvarid"));
      grammar.put("tyvar", new Rule("tyvar", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("akind"));
      addVar(left, new NonTerminal("bkind"), new NonTerminal("akind"));
      grammar.put("bkind", new Rule("bkind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new Terminal(DERIVING), new NonTerminal("qtycon"));
      addVar(variants, new Terminal(DERIVING), new Terminal(OPAREN), new Terminal(CPAREN));
      addVar(variants, new Terminal(DERIVING), new Terminal(OPAREN), new NonTerminal("inst_types1"), new Terminal(CPAREN));
      grammar.put("deriving", new Rule("deriving", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qvar"), new Terminal(EQUAL), new NonTerminal("texp"));
      addVar(variants, new NonTerminal("qvar"));
      grammar.put("fbind", new Rule("fbind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new Terminal(DCOLON), new NonTerminal("sigtype"));
      grammar.put("opt_sig", new Rule("opt_sig", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("moduleheader"));
      addVar(variants);
      grammar.put("maybedocheader", new Rule("maybedocheader", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qvaropm"));
      addVar(variants, new NonTerminal("qconop"));
      grammar.put("qopm", new Rule("qopm", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("impspec"));
      addVar(variants);
      grammar.put("maybeimpspec", new Rule("maybeimpspec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("docnext"));
      addVar(variants, new NonTerminal("docprev"));
      addVar(variants, new NonTerminal("docnamed"));
      addVar(variants, new NonTerminal("docsection"));
      grammar.put("docdecld", new Rule("docdecld", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("decls_inst"), new Terminal(SEMI), new NonTerminal("decl_inst"));
      addVar(left, new NonTerminal("decls_inst"), new Terminal(SEMI));
      addVar(variants, new NonTerminal("decl_inst"));
      addVar(variants);
      grammar.put("decls_inst", new Rule("decls_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(VBAR), new NonTerminal("guardquals"), new Terminal(EQUAL), new NonTerminal("exp"));
      grammar.put("gdrh", new Rule("gdrh", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("decllist"));
      addVar(variants, new Terminal(OCURLY), new NonTerminal("dbinds"), new Terminal(CCURLY));
      addVar(variants, new Terminal(VOCURLY), new NonTerminal("dbinds"), new NonTerminal("close"));
      grammar.put("binds", new Rule("binds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("ntgtycon"));
      addVar(variants, new NonTerminal("tyvar"));
      addVar(variants, new NonTerminal("strict_mark"), new NonTerminal("atype"));
      addVar(variants, new Terminal(OCURLY), new NonTerminal("fielddecls"), new Terminal(CCURLY));
      addVar(variants, new Terminal(OPAREN), new Terminal(CPAREN));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("ctype"), new Terminal(COMMA), new NonTerminal("comma_types1"), new Terminal(CPAREN));
      addVar(variants, new Terminal(OUBXPAREN), new Terminal(CUBXPAREN));
      addVar(variants, new Terminal(OUBXPAREN), new NonTerminal("comma_types1"), new Terminal(CUBXPAREN));
      addVar(variants, new Terminal(OBRACK), new NonTerminal("ctype"), new Terminal(CBRACK));
      addVar(variants, new Terminal(OPABRACK), new NonTerminal("ctype"), new Terminal(CPABRACK));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("ctype"), new Terminal(CPAREN));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("ctype"), new Terminal(DCOLON), new NonTerminal("kind"), new Terminal(CPAREN));
      addVar(variants, new NonTerminal("quasiquote"));
      addVar(variants, new Terminal(PARENESCAPE), new NonTerminal("exp"), new Terminal(CPAREN));
      addVar(variants, new Terminal(IDESCAPE));
      addVar(variants, new Terminal(SIMPLEQUOTE), new NonTerminal("qcon"));
      addVar(variants, new Terminal(SIMPLEQUOTE), new Terminal(OPAREN), new NonTerminal("ctype"), new Terminal(COMMA), new NonTerminal("comma_types1"), new Terminal(CPAREN));
      addVar(variants, new Terminal(SIMPLEQUOTE), new Terminal(OBRACK), new NonTerminal("comma_types0"), new Terminal(CBRACK));
      addVar(variants, new Terminal(SIMPLEQUOTE), new NonTerminal("var"));
      addVar(variants, new Terminal(OBRACK), new NonTerminal("ctype"), new Terminal(COMMA), new NonTerminal("comma_types1"), new Terminal(CBRACK));
      addVar(variants, new Terminal(INTEGER));
      addVar(variants, new Terminal(STRING));
      grammar.put("atype", new Rule("atype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("ctype"));
      grammar.put("sigtype", new Rule("sigtype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(QCONID));
      addVar(variants, new Terminal(PREFIXQCONSYM));
      addVar(variants, new NonTerminal("tycon"));
      grammar.put("qtycon", new Rule("qtycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OVERLAPPABLE), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(OVERLAPPING), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(OVERLAPS), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(INCOHERENT), new Terminal(CLOSE_PRAG));
      addVar(variants);
      grammar.put("overlap_pragma", new Rule("overlap_pragma", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("var"));
      addVar(variants, new NonTerminal("con"));
      grammar.put("name_var", new Rule("name_var", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OCURLY), new NonTerminal("cvtopdecls0"), new Terminal(CCURLY));
      addVar(variants, new Terminal(VOCURLY), new NonTerminal("cvtopdecls0"), new NonTerminal("close"));
      grammar.put("cvtopbody", new Rule("cvtopbody", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("commas"), new NonTerminal("tup_tail"));
      grammar.put("commas_tup_tail", new Rule("commas_tup_tail", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OPAREN), new Terminal(CPAREN));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("commas"), new Terminal(CPAREN));
      addVar(variants, new Terminal(OUBXPAREN), new Terminal(CUBXPAREN));
      addVar(variants, new Terminal(OUBXPAREN), new NonTerminal("commas"), new Terminal(CUBXPAREN));
      addVar(variants, new Terminal(OBRACK), new Terminal(CBRACK));
      grammar.put("sysdcon", new Rule("sysdcon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("bkind"));
      addVar(variants, new NonTerminal("bkind"), new Terminal(RARROW), new NonTerminal("kind"));
      grammar.put("kind", new Rule("kind", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("aexp"));
      addVar(variants, new Terminal(BANG), new NonTerminal("aexp"));
      grammar.put("apat", new Rule("apat", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(BACKQUOTE), new NonTerminal("tyvarid"), new Terminal(BACKQUOTE));
      addVar(variants, new Terminal(DOT));
      grammar.put("tyvarop", new Rule("tyvarop", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("at_decl_inst"));
      addVar(variants, new NonTerminal("decl"));
      grammar.put("decl_inst", new Rule("decl_inst", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("fbinds1"));
      addVar(variants);
      grammar.put("fbinds", new Rule("fbinds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(FORALL), new NonTerminal("tv_bndrs"), new Terminal(DOT), new NonTerminal("ctypedoc"));
      addVar(variants, new NonTerminal("context"), new Terminal(DARROW), new NonTerminal("ctypedoc"));
      addVar(variants, new NonTerminal("ipvar"), new Terminal(DCOLON), new NonTerminal("type"));
      addVar(variants, new NonTerminal("typedoc"));
      grammar.put("ctypedoc", new Rule("ctypedoc", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("maybedocheader"), new Terminal(MODULE), new NonTerminal("modid"), new NonTerminal("maybemodwarning"), new NonTerminal("maybeexports"), new Terminal(WHERE), new NonTerminal("body")).setElementType(GrammarPackage.getMODULE());
      addVar(variants, new NonTerminal("body2")).setElementType(GrammarPackage.getMODULE());
      grammar.put("module", new Rule("module", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(CONSYM));
      addVar(variants, new Terminal(COLON));
      grammar.put("consym", new Rule("consym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("sigdecl"));
      addVar(variants, new Terminal(BANG), new NonTerminal("aexp"), new NonTerminal("rhs"));
      addVar(variants, new NonTerminal("infixexp"), new NonTerminal("opt_sig"), new NonTerminal("rhs")).setElementType(GrammarPackage.getVALUE_BODY());
      addVar(variants, new NonTerminal("pattern_synonym_decl"));
      addVar(variants, new NonTerminal("docdecl"));
      grammar.put("decl_no_th", new Rule("decl_no_th", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new Terminal(WHERE), new NonTerminal("ty_fam_inst_eqn_list"));
      grammar.put("where_type_family", new Rule("where_type_family", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("consym"));
      addVar(variants, new Terminal(QCONSYM));
      grammar.put("qconsym", new Rule("qconsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("aexp2"));
      grammar.put("acmd", new Rule("acmd", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("maybe_docnext"), new NonTerminal("sig_vars"), new Terminal(DCOLON), new NonTerminal("ctype"), new NonTerminal("maybe_docprev"));
      grammar.put("fielddecl", new Rule("fielddecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("btype"));
      addVar(variants, new NonTerminal("btype"), new NonTerminal("conop"), new NonTerminal("btype"));
      grammar.put("constr_stuff", new Rule("constr_stuff", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(STRING));
      addVar(variants);
      grammar.put("maybe_pkg", new Rule("maybe_pkg", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(VARSYM));
      addVar(variants, new NonTerminal("special_sym"));
      grammar.put("varsym_no_minus", new Rule("varsym_no_minus", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(BANG));
      addVar(variants, new Terminal(DOT));
      addVar(variants, new Terminal(STAR));
      grammar.put("special_sym", new Rule("special_sym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("expdoclist"), new NonTerminal("export"), new NonTerminal("expdoclist"), new Terminal(COMMA), new NonTerminal("exportlist1"));
      addVar(variants, new NonTerminal("expdoclist"), new NonTerminal("export"), new NonTerminal("expdoclist"));
      addVar(variants, new NonTerminal("expdoclist"));
      grammar.put("exportlist1", new Rule("exportlist1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(VARID));
      addVar(variants, new NonTerminal("special_id"));
      addVar(variants, new Terminal(UNSAFE));
      addVar(variants, new Terminal(SAFE));
      addVar(variants, new Terminal(INTERRUPTIBLE));
      addVar(variants, new Terminal(FORALL));
      addVar(variants, new Terminal(FAMILY));
      addVar(variants, new Terminal(ROLE));
      grammar.put("varid", new Rule("varid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("importdecls"), new Terminal(SEMI), new NonTerminal("importdecl"));
      addVar(left, new NonTerminal("importdecls"), new Terminal(SEMI));
      addVar(variants, new NonTerminal("importdecl"));
      addVar(variants);
      grammar.put("importdecls", new Rule("importdecls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(QUALIFIED));
      addVar(variants);
      grammar.put("optqualified", new Rule("optqualified", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("namelist"), new NonTerminal("strings"));
      grammar.put("warning", new Rule("warning", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("btype"));
      addVar(variants, new NonTerminal("btype"), new NonTerminal("docprev"));
      addVar(variants, new NonTerminal("btype"), new NonTerminal("qtyconop"), new NonTerminal("type"));
      addVar(variants, new NonTerminal("btype"), new NonTerminal("qtyconop"), new NonTerminal("type"), new NonTerminal("docprev"));
      addVar(variants, new NonTerminal("btype"), new NonTerminal("tyvarop"), new NonTerminal("type"));
      addVar(variants, new NonTerminal("btype"), new NonTerminal("tyvarop"), new NonTerminal("type"), new NonTerminal("docprev"));
      addVar(variants, new NonTerminal("btype"), new Terminal(RARROW), new NonTerminal("ctypedoc"));
      addVar(variants, new NonTerminal("btype"), new NonTerminal("docprev"), new Terminal(RARROW), new NonTerminal("ctypedoc"));
      addVar(variants, new NonTerminal("btype"), new Terminal(TILDE), new NonTerminal("btype"));
      addVar(variants, new NonTerminal("btype"), new Terminal(SIMPLEQUOTE), new NonTerminal("qconop"), new NonTerminal("type"));
      addVar(variants, new NonTerminal("btype"), new Terminal(SIMPLEQUOTE), new NonTerminal("varop"), new NonTerminal("type"));
      grammar.put("typedoc", new Rule("typedoc", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qvar"), new Terminal(AT), new NonTerminal("aexp"));
      addVar(variants, new Terminal(TILDE), new NonTerminal("aexp"));
      addVar(variants, new NonTerminal("aexp1"));
      grammar.put("aexp", new Rule("aexp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("stringlist"), new Terminal(COMMA), new Terminal(STRING));
      addVar(variants, new Terminal(STRING));
      grammar.put("stringlist", new Rule("stringlist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OCURLY), new NonTerminal("gdpatssemi"), new Terminal(CCURLY));
      addVar(variants, new NonTerminal("gdpatssemi"), new NonTerminal("close"));
      grammar.put("ifgdpats", new Rule("ifgdpats", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("btype"));
      addVar(variants, new NonTerminal("btype"), new NonTerminal("qtyconop"), new NonTerminal("type"));
      addVar(variants, new NonTerminal("btype"), new NonTerminal("tyvarop"), new NonTerminal("type"));
      addVar(variants, new NonTerminal("btype"), new Terminal(RARROW), new NonTerminal("ctype"));
      addVar(variants, new NonTerminal("btype"), new Terminal(TILDE), new NonTerminal("btype"));
      addVar(variants, new NonTerminal("btype"), new Terminal(SIMPLEQUOTE), new NonTerminal("qconop"), new NonTerminal("type"));
      addVar(variants, new NonTerminal("btype"), new Terminal(SIMPLEQUOTE), new NonTerminal("varop"), new NonTerminal("type"));
      grammar.put("type", new Rule("type", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("commas"), new Terminal(COMMA));
      addVar(variants, new Terminal(COMMA));
      grammar.put("commas", new Rule("commas", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("docdecld"));
      grammar.put("docdecl", new Rule("docdecl", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(CONID));
      grammar.put("tycon", new Rule("tycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("apat"), new NonTerminal("apats"));
      addVar(variants);
      grammar.put("apats", new Rule("apats", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("varsym_no_minus"));
      addVar(variants, new NonTerminal("qvarsym1"));
      grammar.put("qvarsym_no_minus", new Rule("qvarsym_no_minus", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(DUPIPVARID));
      grammar.put("ipvar", new Rule("ipvar", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("varid"));
      addVar(variants, new Terminal(QVARID));
      addVar(variants, new Terminal(PREFIXQVARSYM));
      grammar.put("qvarid", new Rule("qvarid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(QCONSYM));
      addVar(variants, new Terminal(QVARSYM));
      addVar(variants, new NonTerminal("tyconsym"));
      grammar.put("qtyconsym", new Rule("qtyconsym", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(VARID));
      addVar(variants, new NonTerminal("special_id"));
      addVar(variants, new Terminal(UNSAFE));
      addVar(variants, new Terminal(SAFE));
      addVar(variants, new Terminal(INTERRUPTIBLE));
      grammar.put("tyvarid", new Rule("tyvarid", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("btype"), new NonTerminal("atype"));
      addVar(variants, new NonTerminal("atype"));
      grammar.put("btype", new Rule("btype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OPAREN), new NonTerminal("exportlist"), new Terminal(CPAREN));
      addVar(variants, new Terminal(HIDING), new Terminal(OPAREN), new NonTerminal("exportlist"), new Terminal(CPAREN));
      grammar.put("impspec", new Rule("impspec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(DERIVING), new Terminal(INSTANCE), new NonTerminal("overlap_pragma"), new NonTerminal("inst_type"));
      grammar.put("stand_alone_deriving", new Rule("stand_alone_deriving", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("ralt"), new NonTerminal("wherebinds"));
      grammar.put("alt_rhs", new Rule("alt_rhs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(FORALL), new NonTerminal("tv_bndrs"), new Terminal(DOT));
      addVar(variants);
      grammar.put("forall", new Rule("forall", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("con"));
      addVar(variants, new NonTerminal("con"), new Terminal(COMMA), new NonTerminal("con_list"));
      grammar.put("con_list", new Rule("con_list", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(DATA));
      addVar(variants, new Terminal(NEWTYPE));
      grammar.put("data_or_newtype", new Rule("data_or_newtype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("infixexp"), new Terminal(DCOLON), new NonTerminal("sigtype"));
      addVar(variants, new NonTerminal("infixexp"), new Terminal(LARROWTAIL), new NonTerminal("exp"));
      addVar(variants, new NonTerminal("infixexp"), new Terminal(RARROWTAIL), new NonTerminal("exp"));
      addVar(variants, new NonTerminal("infixexp"), new Terminal(LLARROWTAIL), new NonTerminal("exp"));
      addVar(variants, new NonTerminal("infixexp"), new Terminal(RRARROWTAIL), new NonTerminal("exp"));
      addVar(variants, new NonTerminal("infixexp"));
      grammar.put("exp", new Rule("exp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(IDESCAPE));
      addVar(variants, new Terminal(PARENESCAPE), new NonTerminal("exp"), new Terminal(CPAREN));
      addVar(variants, new Terminal(IDTYESCAPE));
      addVar(variants, new Terminal(PARENTYESCAPE), new NonTerminal("exp"), new Terminal(CPAREN));
      grammar.put("splice_exp", new Rule("splice_exp", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("exp"));
      addVar(variants, new Terminal(BANG), new NonTerminal("aexp"));
      grammar.put("bindpat", new Rule("bindpat", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(DEPRECATED_PRAG), new NonTerminal("strings"), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(WARNING_PRAG), new NonTerminal("strings"), new Terminal(CLOSE_PRAG));
      addVar(variants);
      grammar.put("maybemodwarning", new Rule("maybemodwarning", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("name_boolformula_atom"));
      addVar(variants, new NonTerminal("name_boolformula_atom"), new Terminal(COMMA), new NonTerminal("name_boolformula_and"));
      grammar.put("name_boolformula_and", new Rule("name_boolformula_and", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(CTYPE), new Terminal(STRING), new Terminal(STRING), new Terminal(CLOSE_PRAG));
      addVar(variants, new Terminal(CTYPE), new Terminal(STRING), new Terminal(CLOSE_PRAG));
      addVar(variants);
      grammar.put("capi_ctype", new Rule("capi_ctype", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("gadt_constr"), new Terminal(SEMI), new NonTerminal("gadt_constrs"));
      addVar(variants, new NonTerminal("gadt_constr"));
      addVar(variants);
      grammar.put("gadt_constrs", new Rule("gadt_constrs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OCURLY), new NonTerminal("stmts"), new Terminal(CCURLY));
      addVar(variants, new Terminal(VOCURLY), new NonTerminal("stmts"), new NonTerminal("close"));
      grammar.put("stmtlist", new Rule("stmtlist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(SAFE));
      addVar(variants);
      grammar.put("maybe_safe", new Rule("maybe_safe", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(OCURLY), new NonTerminal("decls_cls"), new Terminal(CCURLY));
      addVar(variants, new Terminal(VOCURLY), new NonTerminal("decls_cls"), new NonTerminal("close"));
      grammar.put("decllist_cls", new Rule("decllist_cls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("dbinds"), new Terminal(SEMI), new NonTerminal("dbind"));
      addVar(left, new NonTerminal("dbinds"), new Terminal(SEMI));
      addVar(variants, new NonTerminal("dbind"));
      grammar.put("dbinds", new Rule("dbinds", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("stmt"), new NonTerminal("stmts_help"));
      addVar(variants, new Terminal(SEMI), new NonTerminal("stmts"));
      addVar(variants);
      grammar.put("stmts", new Rule("stmts", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("qcname"));
      addVar(variants, new Terminal(TYPE), new NonTerminal("qcname"));
      grammar.put("qcname_ext", new Rule("qcname_ext", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("ipvar"));
      addVar(variants, new NonTerminal("qcname"));
      addVar(variants, new NonTerminal("literal"));
      addVar(variants, new Terminal(INTEGER));
      addVar(variants, new Terminal(RATIONAL));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("texp"), new Terminal(CPAREN));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("tup_exprs"), new Terminal(CPAREN));
      addVar(variants, new Terminal(OUBXPAREN), new NonTerminal("texp"), new Terminal(CUBXPAREN));
      addVar(variants, new Terminal(OUBXPAREN), new NonTerminal("tup_exprs"), new Terminal(CUBXPAREN));
      addVar(variants, new Terminal(OBRACK), new NonTerminal("list"), new Terminal(CBRACK));
      addVar(variants, new Terminal(OPABRACK), new NonTerminal("parr"), new Terminal(CPABRACK));
      addVar(variants, new Terminal(UNDERSCORE));
      addVar(variants, new NonTerminal("splice_exp"));
      addVar(variants, new Terminal(SIMPLEQUOTE), new NonTerminal("qvar"));
      addVar(variants, new Terminal(SIMPLEQUOTE), new NonTerminal("qcon"));
      addVar(variants, new Terminal(TYQUOTE), new NonTerminal("tyvar"));
      addVar(variants, new Terminal(TYQUOTE), new NonTerminal("gtycon"));
      addVar(variants, new Terminal(OPENEXPQUOTE), new NonTerminal("exp"), new Terminal(CLOSEQUOTE));
      addVar(variants, new Terminal(OPENTEXPQUOTE), new NonTerminal("exp"), new Terminal(CLOSETEXPQUOTE));
      addVar(variants, new Terminal(OPENTYPQUOTE), new NonTerminal("ctype"), new Terminal(CLOSEQUOTE));
      addVar(variants, new Terminal(OPENPATQUOTE), new NonTerminal("infixexp"), new Terminal(CLOSEQUOTE));
      addVar(variants, new Terminal(OPENDECQUOTE), new NonTerminal("cvtopbody"), new Terminal(CLOSEQUOTE));
      addVar(variants, new NonTerminal("quasiquote"));
      addVar(variants, new Terminal(OPARENBAR), new NonTerminal("aexp2"), new NonTerminal("cmdargs"), new Terminal(CPARENBAR));
      grammar.put("aexp2", new Rule("aexp2", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("aexp1"), new Terminal(OCURLY), new NonTerminal("fbinds"), new Terminal(CCURLY));
      addVar(variants, new NonTerminal("aexp2"));
      grammar.put("aexp1", new Rule("aexp1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("ntgtycon"));
      addVar(variants, new Terminal(OPAREN), new Terminal(CPAREN));
      addVar(variants, new Terminal(OUBXPAREN), new Terminal(CUBXPAREN));
      grammar.put("gtycon", new Rule("gtycon", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("alts1"));
      addVar(variants, new Terminal(SEMI), new NonTerminal("alts"));
      grammar.put("alts", new Rule("alts", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("kind"));
      addVar(variants, new NonTerminal("kind"), new Terminal(COMMA), new NonTerminal("comma_kinds1"));
      grammar.put("comma_kinds1", new Rule("comma_kinds1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new NonTerminal("rule_explicit_activation"));
      grammar.put("rule_activation", new Rule("rule_activation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(STRING), new NonTerminal("rule_activation"), new NonTerminal("rule_forall"), new NonTerminal("infixexp"), new Terminal(EQUAL), new NonTerminal("exp"));
      grammar.put("rule", new Rule("rule", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("cmdargs"), new NonTerminal("acmd"));
      addVar(variants);
      grammar.put("cmdargs", new Rule("cmdargs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new Terminal(OPAREN), new Terminal(DOTDOT), new Terminal(CPAREN));
      addVar(variants, new Terminal(OPAREN), new Terminal(CPAREN));
      addVar(variants, new Terminal(OPAREN), new NonTerminal("qcnames"), new Terminal(CPAREN));
      grammar.put("export_subspec", new Rule("export_subspec", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new NonTerminal("explicit_activation"));
      grammar.put("activation", new Rule("activation", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("maybe_docnext"), new Terminal(EQUAL), new NonTerminal("constrs1"));
      grammar.put("constrs", new Rule("constrs", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants);
      addVar(variants, new NonTerminal("fielddecls1"));
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
      addVar(variants, new Terminal(OCURLY), new NonTerminal("importdecls"));
      addVar(variants, new Terminal(VOCURLY), new NonTerminal("importdecls"));
      grammar.put("header_body", new Rule("header_body", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("name_var"));
      addVar(variants, new NonTerminal("name_var"), new Terminal(COMMA), new NonTerminal("namelist"));
      grammar.put("namelist", new Rule("namelist", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("alts1"), new Terminal(SEMI), new NonTerminal("alt"));
      addVar(left, new NonTerminal("alts1"), new Terminal(SEMI));
      addVar(variants, new NonTerminal("alt"));
      grammar.put("alts1", new Rule("alts1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("texp"));
      addVar(variants, new NonTerminal("lexps"));
      addVar(variants, new NonTerminal("texp"), new Terminal(DOTDOT));
      addVar(variants, new NonTerminal("texp"), new Terminal(COMMA), new NonTerminal("exp"), new Terminal(DOTDOT));
      addVar(variants, new NonTerminal("texp"), new Terminal(DOTDOT), new NonTerminal("exp"));
      addVar(variants, new NonTerminal("texp"), new Terminal(COMMA), new NonTerminal("exp"), new Terminal(DOTDOT), new NonTerminal("exp"));
      addVar(variants, new NonTerminal("texp"), new Terminal(VBAR), new NonTerminal("flattenedpquals"));
      grammar.put("list", new Rule("list", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(SEMI));
      addVar(variants);
      grammar.put("optSemi", new Rule("optSemi", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("rules"), new Terminal(SEMI), new NonTerminal("rule"));
      addVar(left, new NonTerminal("rules"), new Terminal(SEMI));
      addVar(variants, new NonTerminal("rule"));
      addVar(variants);
      grammar.put("rules", new Rule("rules", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(UNSAFE));
      addVar(variants, new Terminal(SAFE));
      addVar(variants, new Terminal(INTERRUPTIBLE));
      grammar.put("safety", new Rule("safety", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("fielddecl"), new NonTerminal("maybe_docnext"), new Terminal(COMMA), new NonTerminal("maybe_docprev"), new NonTerminal("fielddecls1"));
      addVar(variants, new NonTerminal("fielddecl"));
      grammar.put("fielddecls1", new Rule("fielddecls1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("guardquals1"), new Terminal(COMMA), new NonTerminal("qual"));
      addVar(variants, new NonTerminal("qual"));
      grammar.put("guardquals1", new Rule("guardquals1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("texp"), new NonTerminal("commas_tup_tail"));
      addVar(variants, new NonTerminal("texp"));
      addVar(variants);
      grammar.put("tup_tail", new Rule("tup_tail", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("lexps"), new Terminal(COMMA), new NonTerminal("texp"));
      addVar(variants, new NonTerminal("texp"), new Terminal(COMMA), new NonTerminal("texp"));
      grammar.put("lexps", new Rule("lexps", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new NonTerminal("btype"), new Terminal(TILDE), new NonTerminal("btype"));
      addVar(variants, new NonTerminal("btype"));
      grammar.put("context", new Rule("context", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("gdpats"), new NonTerminal("gdpat"));
      addVar(variants, new NonTerminal("gdpat"));
      grammar.put("gdpats", new Rule("gdpats", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("decls"), new Terminal(SEMI), new NonTerminal("decl"));
      addVar(left, new NonTerminal("decls"), new Terminal(SEMI));
      addVar(variants, new NonTerminal("decl"));
      addVar(variants);
      grammar.put("decls", new Rule("decls", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(left, new NonTerminal("constrs1"), new NonTerminal("maybe_docnext"), new Terminal(VBAR), new NonTerminal("maybe_docprev"), new NonTerminal("constr"));
      addVar(variants, new NonTerminal("constr"));
      grammar.put("constrs1", new Rule("constrs1", variants, left));
    }
    {
      List<Variant> variants = new ArrayList<Variant>();
      List<Variant> left = new ArrayList<Variant>();
      addVar(variants, new Terminal(CHAR));
      addVar(variants, new Terminal(STRING));
      addVar(variants, new Terminal(PRIMINT));
      addVar(variants, new Terminal(PRIMWORD));
      addVar(variants, new Terminal(PRIMCHAR));
      addVar(variants, new Terminal(PRIMSTRING));
      addVar(variants, new Terminal(PRIMFLOAT));
      addVar(variants, new Terminal(PRIMDOUBLE));
      grammar.put("literal", new Rule("literal", variants, left));
    }
    return grammar;
  }
}
