package org.jetbrains.grammar

import org.jetbrains.grammar.HaskellTokens
import org.jetbrains.grammar.HaskellLexerTokens.*
import org.jetbrains.haskell.parser.rules.ParserState
import org.jetbrains.grammar.dumb.Rule


public class HaskellParser(state : ParserState?) : BaseHaskellParser(state) {
  override fun getGrammar() : Map<String, Rule> {
    return grammar {
      rule("importdecl") {
        variant(IMPORT, "maybe_src", "maybe_safe", "optqualified", "maybe_pkg", "modid", "maybeas", "maybeimpspec")
      }
      rule("where_inst") {
        variant(WHERE, "decllist_inst")
        variant()
      }
      rule("ty_fam_inst_eqns") {
        variant("ty_fam_inst_eqns", SEMI, "ty_fam_inst_eqn")
        variant("ty_fam_inst_eqns", SEMI)
        variant("ty_fam_inst_eqn")
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
      rule("role") {
        variant(VARID)
        variant(UNDERSCORE)
      }
      rule("ops") {
        variant("ops", COMMA, "op")
        variant("op")
      }
      rule("decllist_inst") {
        variant(OCURLY, "decls_inst", CCURLY)
        variant(VOCURLY, "decls_inst", "close")
      }
      rule("where_decls") {
        variant(WHERE, OCURLY, "decls", CCURLY)
        variant(WHERE, VOCURLY, "decls", "close")
      }
      rule("inst_decl") {
        variant(INSTANCE, "overlap_pragma", "inst_type", "where_inst")
        variant(TYPE, INSTANCE, "ty_fam_inst_eqn")
        variant("data_or_newtype", INSTANCE, "capi_ctype", "tycl_hdr", "constrs", "deriving")
        variant("data_or_newtype", INSTANCE, "capi_ctype", "tycl_hdr", "opt_kind_sig", "gadt_constrlist", "deriving")
      }
      rule("maybedocheader") {
        variant("moduleheader")
        variant()
      }
      rule("ty_decl") {
        variant(TYPE, "type", EQUAL, "ctypedoc")
        variant(TYPE, FAMILY, "type", "opt_kind_sig", "where_type_family")
        variant("data_or_newtype", "capi_ctype", "tycl_hdr", "constrs", "deriving")
        variant("data_or_newtype", "capi_ctype", "tycl_hdr", "opt_kind_sig", "gadt_constrlist", "deriving")
        variant(DATA, FAMILY, "type", "opt_kind_sig")
      }
      rule("maybeimpspec") {
        variant("impspec")
        variant()
      }
      rule("topdecls") {
        variant("topdecls", SEMI, "topdecl")
        variant("topdecls", SEMI)
        variant("topdecl")
      }
      rule("opt_family") {
        variant()
        variant(FAMILY)
      }
      rule("header") {
        variant("maybedocheader", MODULE, "modid", "maybemodwarning", "maybeexports", WHERE, "header_body")
        variant("header_body2")
      }
      rule("exp_doc") {
        variant("docsection")
        variant("docnamed")
        variant("docnext")
      }
      rule("decls_inst") {
        variant("decls_inst", SEMI, "decl_inst")
        variant("decls_inst", SEMI)
        variant("decl_inst")
        variant()
      }
      rule("ty_fam_inst_eqn_list") {
        variant(OCURLY, "ty_fam_inst_eqns", CCURLY)
        variant(VOCURLY, "ty_fam_inst_eqns", "close")
        variant(OCURLY, DOTDOT, CCURLY)
        variant(VOCURLY, DOTDOT, "close")
      }
      rule("overlap_pragma") {
        variant("{-# OVERLAPPABLE", CLOSE_PRAG)
        variant("{-# OVERLAPPING", CLOSE_PRAG)
        variant("{-# OVERLAPS", CLOSE_PRAG)
        variant("{-# INCOHERENT", CLOSE_PRAG)
        variant()
      }
      rule("roles") {
        variant("role")
        variant("roles", "role")
      }
      rule("maybe_src") {
        variant(SOURCE_PRAG, CLOSE_PRAG)
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
      rule("cvtopdecls") {
        variant("topdecls")
      }
      rule("header_body2") {
        variant(OCURLY, "importdecls")
        variant("missing_module_keyword", "importdecls")
      }
      rule("decl_cls") {
        variant("at_decl_cls")
        variant("decl")
        variant(DEFAULT, "infixexp", DCOLON, "sigtypedoc")
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
      rule("decl_inst") {
        variant("at_decl_inst")
        variant("decl")
      }
      rule("module") {
        variant("maybedocheader", MODULE, "modid", "maybemodwarning", "maybeexports", WHERE, "body")
        variant("body2")
      }
      rule("tycl_hdr") {
        variant("context", DARROW, "type")
        variant("type")
      }
      rule("where_type_family") {
        variant()
        variant(WHERE, "ty_fam_inst_eqn_list")
      }
      rule("vars0") {
        variant()
        variant("varid", "vars0")
      }
      rule("body2") {
        variant(OCURLY, "top", CCURLY)
        variant("missing_module_keyword", "top", "close")
      }
      rule("maybe_pkg") {
        variant(STRING)
        variant()
      }
      rule("ty_fam_inst_eqn") {
        variant("type", EQUAL, "ctype")
      }
      rule("exportlist1") {
        variant("expdoclist", "export", "expdoclist", COMMA, "exportlist1")
        variant("expdoclist", "export", "expdoclist")
        variant("expdoclist")
      }
      rule("decllist") {
        variant(OCURLY, "decls", CCURLY)
        variant(VOCURLY, "decls", "close")
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
      rule("modid") {
        variant(CONID)
        variant(QCONID)
      }
      rule("body") {
        variant(OCURLY, "top", CCURLY)
        variant(VOCURLY, "top", "close")
      }
      rule("stringlist") {
        variant("stringlist", COMMA, STRING)
        variant(STRING)
      }
      rule("commas") {
        variant("commas", COMMA)
        variant(COMMA)
      }
      rule("close") {
        variant(VCCURLY)
      }
      rule("qcname") {
        variant("qvar")
        variant("qcon")
      }
      rule("exportlist") {
        variant("expdoclist", COMMA, "expdoclist")
        variant("exportlist1")
      }
      rule("opt_kind_sig") {
        variant()
        variant(DCOLON, "kind")
      }
      rule("where_cls") {
        variant(WHERE, "decllist_cls")
        variant()
      }
      rule("impspec") {
        variant(OPAREN, "exportlist", CPAREN)
        variant(HIDING, OPAREN, "exportlist", CPAREN)
      }
      rule("stand_alone_deriving") {
        variant(DERIVING, INSTANCE, "overlap_pragma", "inst_type")
      }
      rule("data_or_newtype") {
        variant(DATA)
        variant(NEWTYPE)
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
      rule("at_decl_cls") {
        variant(DATA, "opt_family", "type", "opt_kind_sig")
        variant(TYPE, "type", "opt_kind_sig")
        variant(TYPE, FAMILY, "type", "opt_kind_sig")
        variant(TYPE, "ty_fam_inst_eqn")
        variant(TYPE, INSTANCE, "ty_fam_inst_eqn")
      }
      rule("maybe_safe") {
        variant(SAFE)
        variant()
      }
      rule("moduleheader") {
        variant(DOCCOMMENTNEXT)
      }
      rule("decllist_cls") {
        variant(OCURLY, "decls_cls", CCURLY)
        variant(VOCURLY, "decls_cls", "close")
      }
      rule("strings") {
        variant(STRING)
        variant(OBRACK, "stringlist", CBRACK)
      }
      rule("qcname_ext") {
        variant("qcname")
        variant(TYPE, "qcname")
      }
      rule("export_subspec") {
        variant()
        variant(OPAREN, DOTDOT, CPAREN)
        variant(OPAREN, CPAREN)
        variant(OPAREN, "qcnames", CPAREN)
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
      rule("missing_module_keyword") {
        variant()
      }
      rule("header_body") {
        variant(OCURLY, "importdecls")
        variant(VOCURLY, "importdecls")
      }
      rule("maybeexports") {
        variant(OPAREN, "exportlist", CPAREN)
        variant()
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
      rule("maybe_roles") {
        variant()
        variant("roles")
      }
      rule("decls") {
        variant("decls", SEMI, "decl")
        variant("decls", SEMI)
        variant("decl")
        variant()
      }
      rule("cl_decl") {
        variant(CLASS, "tycl_hdr", "fds", "where_cls")
      }
    }
  }
}
