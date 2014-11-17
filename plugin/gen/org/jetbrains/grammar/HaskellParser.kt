package org.jetbrains.grammar

import org.jetbrains.grammar.HaskellTokens
import org.jetbrains.grammar.HaskellLexerTokens.*
import org.jetbrains.haskell.parser.rules.ParserState
import org.jetbrains.grammar.dumb.Rule


public class HaskellParser(state : ParserState?) : BaseHaskellParser(state) {
  override fun getGrammar() : Map<String, Rule> {
    return grammar {
      rule("strict_mark") {
        variant(BANG)
        variant(UNPACK_PRAG, CLOSE_PRAG)
        variant(NOUNPACK_PRAG, CLOSE_PRAG)
        variant(UNPACK_PRAG, CLOSE_PRAG, BANG)
        variant(NOUNPACK_PRAG, CLOSE_PRAG, BANG)
      }
      rule("where_inst") {
        variant(WHERE, "decllist_inst")
        variant()
      }
      rule("tv_bndr") {
        variant("tyvar")
        variant(OPAREN, "tyvar", DCOLON, "kind", CPAREN)
      }
      rule("qop") {
        variant("qvarop")
        variant("qconop")
      }
      rule("cvtopdecls0") {
        variant()
        variant("cvtopdecls")
      }
      rule("role") {
        variant(VARID)
        variant(UNDERSCORE)
      }
      rule("rhs") {
        variant(EQUAL, "exp", "wherebinds")
        variant("gdrhs", "wherebinds")
      }
      rule("decllist_inst") {
        variant(OCURLY, "decls_inst", CCURLY)
        variant(VOCURLY, "decls_inst", "close")
      }
      rule("qvaropm") {
        variant("qvarsym_no_minus")
        variant(BACKQUOTE, "qvarid", BACKQUOTE)
      }
      rule("qvarsym1") {
        variant(QVARSYM)
      }
      rule("ty_decl") {
        variant(TYPE, "type", EQUAL, "ctypedoc")
        variant(TYPE, FAMILY, "type", "opt_kind_sig", "where_type_family")
        variant("data_or_newtype", "capi_ctype", "tycl_hdr", "constrs", "deriving")
        variant("data_or_newtype", "capi_ctype", "tycl_hdr", "opt_kind_sig", "gadt_constrlist", "deriving")
        variant(DATA, FAMILY, "type", "opt_kind_sig")
      }
      rule("infixexp") {
        variant("exp10")
        variant("infixexp", "qop", "exp10")
      }
      rule("topdecls") {
        variant("topdecls", SEMI, "topdecl")
        variant("topdecls", SEMI)
        variant("topdecl")
      }
      rule("exp_doc") {
        variant("docsection")
        variant("docnamed")
        variant("docnext")
      }
      rule("header") {
        variant("maybedocheader", MODULE, "modid", "maybemodwarning", "maybeexports", WHERE, "header_body")
        variant("header_body2")
      }
      rule("opt_family") {
        variant()
        variant(FAMILY)
      }
      rule("docnext") {
        variant(DOCCOMMENTNEXT)
      }
      rule("ty_fam_inst_eqn_list") {
        variant(OCURLY, "ty_fam_inst_eqns", CCURLY)
        variant(VOCURLY, "ty_fam_inst_eqns", "close")
        variant(OCURLY, DOTDOT, CCURLY)
        variant(VOCURLY, DOTDOT, "close")
      }
      rule("hpc_annot") {
        variant(GENERATED_PRAG, STRING, INTEGER, COLON, INTEGER, MINUS, INTEGER, COLON, INTEGER, CLOSE_PRAG)
      }
      rule("roles") {
        variant("role")
        variant("roles", "role")
      }
      rule("constr") {
        variant("maybe_docnext", "forall", "context", DARROW, "constr_stuff", "maybe_docprev")
        variant("maybe_docnext", "forall", "constr_stuff", "maybe_docprev")
      }
      rule("qtyconop") {
        variant("qtyconsym")
        variant(BACKQUOTE, "qtycon", BACKQUOTE)
      }
      rule("maybe_src") {
        variant(SOURCE_PRAG, CLOSE_PRAG)
        variant()
      }
      rule("maybe_docnext") {
        variant("docnext")
        variant()
      }
      rule("prec") {
        variant()
        variant(INTEGER)
      }
      rule("topdecl") {
        variant("cl_decl")
        variant("ty_decl")
        variant("inst_decl")
        variant("stand_alone_deriving")
        variant("role_annot")
        variant(DEFAULT, OPAREN, "comma_types0", CPAREN)
        variant(FOREIGN, "fdecl")
        variant(DEPRECATED_PRAG, "deprecations", CLOSE_PRAG)
        variant(WARNING_PRAG, "warnings", CLOSE_PRAG)
        variant(RULES_PRAG, "rules", CLOSE_PRAG)
        variant(VECT_PRAG, "qvar", EQUAL, "exp", CLOSE_PRAG)
        variant(NOVECT_PRAG, "qvar", CLOSE_PRAG)
        variant(VECT_PRAG, TYPE, "gtycon", CLOSE_PRAG)
        variant(VECT_SCALAR_PRAG, TYPE, "gtycon", CLOSE_PRAG)
        variant(VECT_PRAG, TYPE, "gtycon", EQUAL, "gtycon", CLOSE_PRAG)
        variant(VECT_SCALAR_PRAG, TYPE, "gtycon", EQUAL, "gtycon", CLOSE_PRAG)
        variant(VECT_PRAG, CLASS, "gtycon", CLOSE_PRAG)
        variant("annotation")
        variant("decl_no_th")
        variant("infixexp")
      }
      rule("opt_asig") {
        variant()
        variant(DCOLON, "atype")
      }
      rule("cvtopdecls") {
        variant("topdecls")
      }
      rule("header_body2") {
        variant(OCURLY, "importdecls")
        variant("missing_module_keyword", "importdecls")
      }
      rule("varsym") {
        variant("varsym_no_minus")
        variant(MINUS)
      }
      rule("decl_cls") {
        variant("at_decl_cls")
        variant("decl")
        variant(DEFAULT, "infixexp", DCOLON, "sigtypedoc")
      }
      rule("inst_type") {
        variant("sigtype")
      }
      rule("role_annot") {
        variant(TYPE, ROLE, "oqtycon", "maybe_roles")
      }
      rule("qcnames") {
        variant("qcnames", COMMA, "qcname_ext")
        variant("qcname_ext")
      }
      rule("expdoclist") {
        variant("exp_doc", "expdoclist")
        variant()
      }
      rule("docprev") {
        variant(DOCCOMMENTPREV)
      }
      rule("decl") {
        variant("decl_no_th")
        variant("splice_exp")
      }
      rule("tycl_hdr") {
        variant("context", DARROW, "type")
        variant("type")
      }
      rule("fd") {
        variant("varids0", RARROW, "varids0")
      }
      rule("vars0") {
        variant()
        variant("varid", "vars0")
      }
      rule("qvar") {
        variant("qvarid")
        variant(OPAREN, "varsym", CPAREN)
        variant(OPAREN, "qvarsym1", CPAREN)
      }
      rule("docsection") {
        variant(DOCSECTION)
      }
      rule("body2") {
        variant(OCURLY, "top", CCURLY)
        variant("missing_module_keyword", "top", "close")
      }
      rule("pkind") {
        variant("qtycon")
        variant(OPAREN, CPAREN)
        variant(OPAREN, "kind", COMMA, "comma_kinds1", CPAREN)
        variant(OBRACK, "kind", CBRACK)
      }
      rule("ty_fam_inst_eqn") {
        variant("type", EQUAL, "ctype")
      }
      rule("decllist") {
        variant(OCURLY, "decls", CCURLY)
        variant(VOCURLY, "decls", "close")
      }
      rule("fds1") {
        variant("fds1", COMMA, "fd")
        variant("fd")
      }
      rule("qvarop") {
        variant("qvarsym")
        variant(BACKQUOTE, "qvarid", BACKQUOTE)
      }
      rule("modid") {
        variant(CONID)
        variant(QCONID)
      }
      rule("body") {
        variant(OCURLY, "top", CCURLY)
        variant(VOCURLY, "top", "close")
      }
      rule("close") {
        variant(VCCURLY)
      }
      rule("fexp") {
        variant("fexp", "aexp")
        variant("aexp")
      }
      rule("comma_types1") {
        variant("ctype")
        variant("ctype", COMMA, "comma_types1")
      }
      rule("qcname") {
        variant("qvar")
        variant("qcon")
      }
      rule("comma_types0") {
        variant("comma_types1")
        variant()
      }
      rule("special_id") {
        variant(AS)
        variant(QUALIFIED)
        variant(HIDING)
        variant(EXPORT)
        variant(LABEL)
        variant(DYNAMIC)
        variant(STDCALLCONV)
        variant(CCALLCONV)
        variant(CAPICONV)
        variant(PRIMCALLCONV)
        variant(JAVASCRIPTCALLCONV)
        variant(GROUP)
      }
      rule("exportlist") {
        variant("expdoclist", COMMA, "expdoclist")
        variant("exportlist1")
      }
      rule("exp10") {
        variant(LAM, "apat", "apats", "opt_asig", RARROW, "exp")
        variant(LET, "binds", IN, "exp")
        variant(LAM, LCASE, "altslist")
        variant(IF, "exp", "optSemi", THEN, "exp", "optSemi", ELSE, "exp")
        variant(IF, "ifgdpats")
        variant(CASE, "exp", OF, "altslist")
        variant(MINUS, "fexp")
        variant(DO, "stmtlist")
        variant(MDO, "stmtlist")
        variant("scc_annot", "exp")
        variant("hpc_annot", "exp")
        variant(PROC, "aexp", RARROW, "exp")
        variant(CORE_PRAG, STRING, CLOSE_PRAG, "exp")
        variant("fexp")
      }
      rule("gadt_constrlist") {
        variant(WHERE, OCURLY, "gadt_constrs", CCURLY)
        variant(WHERE, VOCURLY, "gadt_constrs", "close")
        variant()
      }
      rule("dbind") {
        variant("ipvar", EQUAL, "exp")
      }
      rule("opt_kind_sig") {
        variant()
        variant(DCOLON, "kind")
      }
      rule("where_cls") {
        variant(WHERE, "decllist_cls")
        variant()
      }
      rule("varop") {
        variant("varsym")
        variant(BACKQUOTE, "varid", BACKQUOTE)
      }
      rule("at_decl_cls") {
        variant(DATA, "opt_family", "type", "opt_kind_sig")
        variant(TYPE, "type", "opt_kind_sig")
        variant(TYPE, FAMILY, "type", "opt_kind_sig")
        variant(TYPE, "ty_fam_inst_eqn")
        variant(TYPE, INSTANCE, "ty_fam_inst_eqn")
      }
      rule("moduleheader") {
        variant(DOCCOMMENTNEXT)
      }
      rule("wherebinds") {
        variant(WHERE, "binds")
        variant()
      }
      rule("quasiquote") {
        variant(QUASIQUOTE)
        variant(QQUASIQUOTE)
      }
      rule("var") {
        variant("varid")
        variant(OPAREN, "varsym", CPAREN)
      }
      rule("strings") {
        variant(STRING)
        variant(OBRACK, "stringlist", CBRACK)
      }
      rule("fds") {
        variant()
        variant(VBAR, "fds1")
      }
      rule("sigtypes1") {
        variant("sigtype")
        variant("sigtype", COMMA, "sigtypes1")
      }
      rule("tv_bndrs") {
        variant("tv_bndr", "tv_bndrs")
        variant()
      }
      rule("sigdecl") {
        variant("infixexp", DCOLON, "sigtypedoc")
        variant("var", COMMA, "sig_vars", DCOLON, "sigtypedoc")
        variant("infix", "prec", "ops")
        variant(INLINE_PRAG, "activation", "qvar", CLOSE_PRAG)
        variant(SPEC_PRAG, "activation", "qvar", DCOLON, "sigtypes1", CLOSE_PRAG)
        variant(SPEC_INLINE_PRAG, "activation", "qvar", DCOLON, "sigtypes1", CLOSE_PRAG)
        variant(SPEC_PRAG, INSTANCE, "inst_type", CLOSE_PRAG)
        variant(MINIMAL_PRAG, "name_boolformula_opt", CLOSE_PRAG)
      }
      rule("top") {
        variant("importdecls")
        variant("importdecls", SEMI, "cvtopdecls")
        variant("cvtopdecls")
      }
      rule("export") {
        variant("qcname_ext", "export_subspec")
        variant(MODULE, "modid")
        variant(PATTERN, "qcon")
      }
      rule("maybeexports") {
        variant(OPAREN, "exportlist", CPAREN)
        variant()
      }
      rule("gdrhs") {
        variant("gdrhs", "gdrh")
        variant("gdrh")
      }
      rule("ctype") {
        variant(FORALL, "tv_bndrs", DOT, "ctype")
        variant("context", DARROW, "ctype")
        variant("ipvar", DCOLON, "type")
        variant("type")
      }
      rule("inst_types1") {
        variant("inst_type")
        variant("inst_type", COMMA, "inst_types1")
      }
      rule("ntgtycon") {
        variant("oqtycon")
        variant(OPAREN, "commas", CPAREN)
        variant(OUBXPAREN, "commas", CUBXPAREN)
        variant(OPAREN, RARROW, CPAREN)
        variant(OBRACK, CBRACK)
        variant(OPABRACK, CPABRACK)
        variant(OPAREN, TILDEHSH, CPAREN)
      }
      rule("decls_cls") {
        variant("decls_cls", SEMI, "decl_cls")
        variant("decls_cls", SEMI)
        variant("decl_cls")
        variant()
      }
      rule("at_decl_inst") {
        variant(TYPE, "ty_fam_inst_eqn")
        variant("data_or_newtype", "capi_ctype", "tycl_hdr", "constrs", "deriving")
        variant("data_or_newtype", "capi_ctype", "tycl_hdr", "opt_kind_sig", "gadt_constrlist", "deriving")
      }
      rule("maybeas") {
        variant(AS, "modid")
        variant()
      }
      rule("akind") {
        variant(STAR)
        variant(OPAREN, "kind", CPAREN)
        variant("pkind")
        variant("tyvar")
      }
      rule("maybe_roles") {
        variant()
        variant("roles")
      }
      rule("scc_annot") {
        variant(SCC_PRAG, STRING, CLOSE_PRAG)
        variant(SCC_PRAG, VARID, CLOSE_PRAG)
      }
      rule("cl_decl") {
        variant(CLASS, "tycl_hdr", "fds", "where_cls")
      }
      rule("importdecl") {
        variant(IMPORT, "maybe_src", "maybe_safe", "optqualified", "maybe_pkg", "modid", "maybeas", "maybeimpspec")
      }
      rule("varids0") {
        variant()
        variant("varids0", "tyvar")
      }
      rule("docnamed") {
        variant(DOCCOMMENTNAMED)
      }
      rule("ty_fam_inst_eqns") {
        variant("ty_fam_inst_eqns", SEMI, "ty_fam_inst_eqn")
        variant("ty_fam_inst_eqns", SEMI)
        variant("ty_fam_inst_eqn")
      }
      rule("oqtycon") {
        variant("qtycon")
        variant(OPAREN, "qtyconsym", CPAREN)
        variant(OPAREN, TILDE, CPAREN)
      }
      rule("infix") {
        variant(INFIX)
        variant(INFIXL)
        variant(INFIXR)
      }
      rule("pattern_synonym_decl") {
        variant(PATTERN, "pat", EQUAL, "pat")
        variant(PATTERN, "pat", LARROW, "pat")
        variant(PATTERN, "pat", LARROW, "pat", "where_decls")
      }
      rule("sigtypedoc") {
        variant("ctypedoc")
      }
      rule("tyconsym") {
        variant(CONSYM)
        variant(VARSYM)
        variant(STAR)
        variant(MINUS)
      }
      rule("explicit_activation") {
        variant(OBRACK, INTEGER, CBRACK)
        variant(OBRACK, TILDE, INTEGER, CBRACK)
      }
      rule("ops") {
        variant("ops", COMMA, "op")
        variant("op")
      }
      rule("where_decls") {
        variant(WHERE, OCURLY, "decls", CCURLY)
        variant(WHERE, VOCURLY, "decls", "close")
      }
      rule("maybe_docprev") {
        variant("docprev")
        variant()
      }
      rule("inst_decl") {
        variant(INSTANCE, "overlap_pragma", "inst_type", "where_inst")
        variant(TYPE, INSTANCE, "ty_fam_inst_eqn")
        variant("data_or_newtype", INSTANCE, "capi_ctype", "tycl_hdr", "constrs", "deriving")
        variant("data_or_newtype", INSTANCE, "capi_ctype", "tycl_hdr", "opt_kind_sig", "gadt_constrlist", "deriving")
      }
      rule("gadt_constr") {
        variant("con_list", DCOLON, "sigtype")
        variant("oqtycon", OCURLY, "fielddecls", CCURLY, DCOLON, "sigtype")
      }
      rule("op") {
        variant("varop")
        variant("conop")
      }
      rule("sig_vars") {
        variant("sig_vars", COMMA, "var")
        variant("var")
      }
      rule("bkind") {
        variant("akind")
        variant("bkind", "akind")
      }
      rule("tyvar") {
        variant("tyvarid")
      }
      rule("qvarsym") {
        variant("varsym")
        variant("qvarsym1")
      }
      rule("deriving") {
        variant()
        variant(DERIVING, "qtycon")
        variant(DERIVING, OPAREN, CPAREN)
        variant(DERIVING, OPAREN, "inst_types1", CPAREN)
      }
      rule("opt_sig") {
        variant()
        variant(DCOLON, "sigtype")
      }
      rule("maybedocheader") {
        variant("moduleheader")
        variant()
      }
      rule("maybeimpspec") {
        variant("impspec")
        variant()
      }
      rule("qopm") {
        variant("qvaropm")
        variant("qconop")
      }
      rule("docdecld") {
        variant("docnext")
        variant("docprev")
        variant("docnamed")
        variant("docsection")
      }
      rule("decls_inst") {
        variant("decls_inst", SEMI, "decl_inst")
        variant("decls_inst", SEMI)
        variant("decl_inst")
        variant()
      }
      rule("gdrh") {
        variant(VBAR, "guardquals", EQUAL, "exp")
      }
      rule("atype") {
        variant("ntgtycon")
        variant("tyvar")
        variant("strict_mark", "atype")
        variant(OCURLY, "fielddecls", CCURLY)
        variant(OPAREN, CPAREN)
        variant(OPAREN, "ctype", COMMA, "comma_types1", CPAREN)
        variant(OUBXPAREN, CUBXPAREN)
        variant(OUBXPAREN, "comma_types1", CUBXPAREN)
        variant(OBRACK, "ctype", CBRACK)
        variant(OPABRACK, "ctype", CPABRACK)
        variant(OPAREN, "ctype", CPAREN)
        variant(OPAREN, "ctype", DCOLON, "kind", CPAREN)
        variant("quasiquote")
        variant(PARENESCAPE, "exp", CPAREN)
        variant(IDESCAPE)
        variant(SIMPLEQUOTE, "qcon")
        variant(SIMPLEQUOTE, OPAREN, "ctype", COMMA, "comma_types1", CPAREN)
        variant(SIMPLEQUOTE, OBRACK, "comma_types0", CBRACK)
        variant(SIMPLEQUOTE, "var")
        variant(OBRACK, "ctype", COMMA, "comma_types1", CBRACK)
        variant(INTEGER)
        variant(STRING)
      }
      rule("binds") {
        variant("decllist")
        variant(OCURLY, "dbinds", CCURLY)
        variant(VOCURLY, "dbinds", "close")
      }
      rule("sigtype") {
        variant("ctype")
      }
      rule("qtycon") {
        variant(QCONID)
        variant(PREFIXQCONSYM)
        variant("tycon")
      }
      rule("overlap_pragma") {
        variant("{-# OVERLAPPABLE", CLOSE_PRAG)
        variant("{-# OVERLAPPING", CLOSE_PRAG)
        variant("{-# OVERLAPS", CLOSE_PRAG)
        variant("{-# INCOHERENT", CLOSE_PRAG)
        variant()
      }
      rule("cvtopbody") {
        variant(OCURLY, "cvtopdecls0", CCURLY)
        variant(VOCURLY, "cvtopdecls0", "close")
      }
      rule("kind") {
        variant("bkind")
        variant("bkind", RARROW, "kind")
      }
      rule("tyvarop") {
        variant(BACKQUOTE, "tyvarid", BACKQUOTE)
        variant(DOT)
      }
      rule("decl_inst") {
        variant("at_decl_inst")
        variant("decl")
      }
      rule("ctypedoc") {
        variant(FORALL, "tv_bndrs", DOT, "ctypedoc")
        variant("context", DARROW, "ctypedoc")
        variant("ipvar", DCOLON, "type")
        variant("typedoc")
      }
      rule("module") {
        variant("maybedocheader", MODULE, "modid", "maybemodwarning", "maybeexports", WHERE, "body")
        variant("body2")
      }
      rule("decl_no_th") {
        variant("sigdecl")
        variant(BANG, "aexp", "rhs")
        variant("infixexp", "opt_sig", "rhs")
        variant("pattern_synonym_decl")
        variant("docdecl")
      }
      rule("where_type_family") {
        variant()
        variant(WHERE, "ty_fam_inst_eqn_list")
      }
      rule("acmd") {
        variant("aexp2")
      }
      rule("fielddecl") {
        variant("maybe_docnext", "sig_vars", DCOLON, "ctype", "maybe_docprev")
      }
      rule("constr_stuff") {
        variant("btype")
        variant("btype", "conop", "btype")
      }
      rule("maybe_pkg") {
        variant(STRING)
        variant()
      }
      rule("varsym_no_minus") {
        variant(VARSYM)
        variant("special_sym")
      }
      rule("special_sym") {
        variant(BANG)
        variant(DOT)
        variant(STAR)
      }
      rule("exportlist1") {
        variant("expdoclist", "export", "expdoclist", COMMA, "exportlist1")
        variant("expdoclist", "export", "expdoclist")
        variant("expdoclist")
      }
      rule("varid") {
        variant(VARID)
        variant("special_id")
        variant(UNSAFE)
        variant(SAFE)
        variant(INTERRUPTIBLE)
        variant(FORALL)
        variant(FAMILY)
        variant(ROLE)
      }
      rule("importdecls") {
        variant("importdecls", SEMI, "importdecl")
        variant("importdecls", SEMI)
        variant("importdecl")
        variant()
      }
      rule("optqualified") {
        variant(QUALIFIED)
        variant()
      }
      rule("typedoc") {
        variant("btype")
        variant("btype", "docprev")
        variant("btype", "qtyconop", "type")
        variant("btype", "qtyconop", "type", "docprev")
        variant("btype", "tyvarop", "type")
        variant("btype", "tyvarop", "type", "docprev")
        variant("btype", RARROW, "ctypedoc")
        variant("btype", "docprev", RARROW, "ctypedoc")
        variant("btype", TILDE, "btype")
        variant("btype", SIMPLEQUOTE, "qconop", "type")
        variant("btype", SIMPLEQUOTE, "varop", "type")
      }
      rule("aexp") {
        variant("qvar", AT, "aexp")
        variant(TILDE, "aexp")
        variant("aexp1")
      }
      rule("stringlist") {
        variant("stringlist", COMMA, STRING)
        variant(STRING)
      }
      rule("type") {
        variant("btype")
        variant("btype", "qtyconop", "type")
        variant("btype", "tyvarop", "type")
        variant("btype", RARROW, "ctype")
        variant("btype", TILDE, "btype")
        variant("btype", SIMPLEQUOTE, "qconop", "type")
        variant("btype", SIMPLEQUOTE, "varop", "type")
      }
      rule("commas") {
        variant("commas", COMMA)
        variant(COMMA)
      }
      rule("docdecl") {
        variant("docdecld")
      }
      rule("tycon") {
        variant(CONID)
      }
      rule("qvarsym_no_minus") {
        variant("varsym_no_minus")
        variant("qvarsym1")
      }
      rule("ipvar") {
        variant(DUPIPVARID)
      }
      rule("qtyconsym") {
        variant(QCONSYM)
        variant(QVARSYM)
        variant("tyconsym")
      }
      rule("qvarid") {
        variant("varid")
        variant(QVARID)
        variant(PREFIXQVARSYM)
      }
      rule("tyvarid") {
        variant(VARID)
        variant("special_id")
        variant(UNSAFE)
        variant(SAFE)
        variant(INTERRUPTIBLE)
      }
      rule("btype") {
        variant("btype", "atype")
        variant("atype")
      }
      rule("impspec") {
        variant(OPAREN, "exportlist", CPAREN)
        variant(HIDING, OPAREN, "exportlist", CPAREN)
      }
      rule("stand_alone_deriving") {
        variant(DERIVING, INSTANCE, "overlap_pragma", "inst_type")
      }
      rule("forall") {
        variant(FORALL, "tv_bndrs", DOT)
        variant()
      }
      rule("data_or_newtype") {
        variant(DATA)
        variant(NEWTYPE)
      }
      rule("exp") {
        variant("infixexp", DCOLON, "sigtype")
        variant("infixexp", LARROWTAIL, "exp")
        variant("infixexp", RARROWTAIL, "exp")
        variant("infixexp", LLARROWTAIL, "exp")
        variant("infixexp", RRARROWTAIL, "exp")
        variant("infixexp")
      }
      rule("splice_exp") {
        variant(IDESCAPE)
        variant(PARENESCAPE, "exp", CPAREN)
        variant(IDTYESCAPE)
        variant(PARENTYESCAPE, "exp", CPAREN)
      }
      rule("maybemodwarning") {
        variant(DEPRECATED_PRAG, "strings", CLOSE_PRAG)
        variant(WARNING_PRAG, "strings", CLOSE_PRAG)
        variant()
      }
      rule("capi_ctype") {
        variant(CTYPE, STRING, STRING, CLOSE_PRAG)
        variant(CTYPE, STRING, CLOSE_PRAG)
        variant()
      }
      rule("gadt_constrs") {
        variant("gadt_constr", SEMI, "gadt_constrs")
        variant("gadt_constr")
        variant()
      }
      rule("maybe_safe") {
        variant(SAFE)
        variant()
      }
      rule("decllist_cls") {
        variant(OCURLY, "decls_cls", CCURLY)
        variant(VOCURLY, "decls_cls", "close")
      }
      rule("dbinds") {
        variant("dbinds", SEMI, "dbind")
        variant("dbinds", SEMI)
        variant("dbind")
      }
      rule("qcname_ext") {
        variant("qcname")
        variant(TYPE, "qcname")
      }
      rule("aexp2") {
        variant("ipvar")
        variant("qcname")
        variant("literal")
        variant(INTEGER)
        variant(RATIONAL)
        variant(OPAREN, "texp", CPAREN)
        variant(OPAREN, "tup_exprs", CPAREN)
        variant(OUBXPAREN, "texp", CUBXPAREN)
        variant(OUBXPAREN, "tup_exprs", CUBXPAREN)
        variant(OBRACK, "list", CBRACK)
        variant(OPABRACK, "parr", CPABRACK)
        variant(UNDERSCORE)
        variant("splice_exp")
        variant(SIMPLEQUOTE, "qvar")
        variant(SIMPLEQUOTE, "qcon")
        variant(TYQUOTE, "tyvar")
        variant(TYQUOTE, "gtycon")
        variant(OPENEXPQUOTE, "exp", CLOSEQUOTE)
        variant(OPENTEXPQUOTE, "exp", CLOSETEXPQUOTE)
        variant(OPENTYPQUOTE, "ctype", CLOSEQUOTE)
        variant(OPENPATQUOTE, "infixexp", CLOSEQUOTE)
        variant(OPENDECQUOTE, "cvtopbody", CLOSEQUOTE)
        variant("quasiquote")
        variant(OPARENBAR, "aexp2", "cmdargs", CPARENBAR)
      }
      rule("aexp1") {
        variant("aexp1", OCURLY, "fbinds", CCURLY)
        variant("aexp2")
      }
      rule("gtycon") {
        variant("ntgtycon")
        variant(OPAREN, CPAREN)
        variant(OUBXPAREN, CUBXPAREN)
      }
      rule("comma_kinds1") {
        variant("kind")
        variant("kind", COMMA, "comma_kinds1")
      }
      rule("cmdargs") {
        variant("cmdargs", "acmd")
        variant()
      }
      rule("export_subspec") {
        variant()
        variant(OPAREN, DOTDOT, CPAREN)
        variant(OPAREN, CPAREN)
        variant(OPAREN, "qcnames", CPAREN)
      }
      rule("activation") {
        variant()
        variant("explicit_activation")
      }
      rule("constrs") {
        variant("maybe_docnext", EQUAL, "constrs1")
      }
      rule("fielddecls") {
        variant()
        variant("fielddecls1")
      }
      rule("missing_module_keyword") {
        variant()
      }
      rule("header_body") {
        variant(OCURLY, "importdecls")
        variant(VOCURLY, "importdecls")
      }
      rule("optSemi") {
        variant(SEMI)
        variant()
      }
      rule("fielddecls1") {
        variant("fielddecl", "maybe_docnext", COMMA, "maybe_docprev", "fielddecls1")
        variant("fielddecl")
      }
      rule("context") {
        variant("btype", TILDE, "btype")
        variant("btype")
      }
      rule("decls") {
        variant("decls", SEMI, "decl")
        variant("decls", SEMI)
        variant("decl")
        variant()
      }
      rule("constrs1") {
        variant("constrs1", "maybe_docnext", VBAR, "maybe_docprev", "constr")
        variant("constr")
      }
      rule("literal") {
        variant(CHAR)
        variant(STRING)
        variant(PRIMINT)
        variant(PRIMWORD)
        variant(PRIMCHAR)
        variant(PRIMSTRING)
        variant(PRIMFLOAT)
        variant(PRIMDOUBLE)
      }
    }
  }
}
