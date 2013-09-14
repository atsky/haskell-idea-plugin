package org.jetbrains.haskell.parser.token;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.haskell.parser.HaskellCompositeElementType;
import org.jetbrains.haskell.parser.HaskellToken;
import org.jetbrains.haskell.psi.ImportDecl;
import org.jetbrains.haskell.psi.Module;

public interface HaskellTokenTypes {
    IElementType ARROW = new HaskellToken("->");
    IElementType AS_KEYWORD = new HaskellToken("as");
    IElementType ASSIGNMENT = new HaskellToken("=");
    IElementType CASE_KEYWORD = new HaskellToken("case");
    IElementType CHARACTER = new HaskellToken("character");
    IElementType CLASS_KEYWORD = new HaskellToken("class");
    IElementType COLON = new HaskellToken(":");
    IElementType COMMA = new HaskellToken(",");
    IElementType COMMENT = new HaskellToken("COMMENT");
    IElementType CONSTRUCTOR_KEYWORD = new HaskellToken("constructor");
    IElementType DATA_KEYWORD = new HaskellToken("data");
    IElementType DO_KEYWORD = new HaskellToken("do");
    IElementType DOT = new HaskellToken(".");
    IElementType END_OF_LINE_COMMENT = new HaskellToken("--");
    IElementType FIELD_KEYWORD = new HaskellToken("field");
    IElementType FORALL = new HaskellToken("forall");
    IElementType HIDING_KEYWORD = new HaskellToken("hiding");
    IElementType ID = new HaskellToken("id");
    IElementType IMPORT_KEYWORD = new HaskellToken("import");
    IElementType IN_KEYWORD = new HaskellToken("in");
    IElementType INSTANCE_KEYWORD = new HaskellToken("instance");
    IElementType LAMBDA = new HaskellToken("\\");
    IElementType LEFT_BRACE = new HaskellToken("{");
    IElementType LEFT_BRACKET = new HaskellToken("[");
    IElementType LEFT_PAREN = new HaskellToken("(");
    IElementType LET_KEYWORD = new HaskellToken("let");
    IElementType MODULE_KEYWORD = new HaskellToken("module");
    IElementType MUTUAL_KEYWORD = new HaskellToken("mutual");
    IElementType NUMBER = new HaskellToken("number");
    IElementType OPEN_KEYWORD = new HaskellToken("open");
    IElementType POSTULATE_KEYWORD = new HaskellToken("postulate");
    IElementType PRAGMA = new HaskellToken("pragma");
    IElementType PUBLIC_KEYWORD = new HaskellToken("public");
    IElementType RECORD_KEYWORD = new HaskellToken("record");
    IElementType RENAMING_KEYWORD = new HaskellToken("renaming");
    IElementType RIGHT_BRACE = new HaskellToken("}");
    IElementType RIGHT_BRACKET = new HaskellToken("]");
    IElementType RIGHT_PAREN = new HaskellToken(")");
    IElementType SEMICOLON = new HaskellToken(";");
    IElementType STRING = new HaskellToken("string");
    IElementType TYPE_CONS = new HaskellToken("type_cons");
    IElementType TYPE_KEYWORD = new HaskellToken("type");
    IElementType THREE_DOTS = new HaskellToken("...");
    IElementType USING_KEYWORD = new HaskellToken("using");
    IElementType VERTICAL_BAR = new HaskellToken("|");
    IElementType VIRTUAL_LEFT_PAREN = new HaskellToken("VIRTUAL_LEFT_PAREN");
    IElementType VIRTUAL_RIGHT_PAREN = new HaskellToken("VIRTUAL_RIGHT_PAREN");
    IElementType VIRTUAL_SEMICOLON = new HaskellToken("VIRTUAL_SEMICOLON");
    IElementType WITH_KEYWORD = new HaskellToken("with");
    IElementType WHERE_KEYWORD = new HaskellToken("where");

    HaskellCompositeElementType CLASS_DECL = new HaskellCompositeElementType("ClassDecl");
    HaskellCompositeElementType DATA_DECL = new HaskellCompositeElementType("DataDecl");
    HaskellCompositeElementType IDENT =  new HaskellCompositeElementType("Ident");
    HaskellCompositeElementType IMPORT_DECL = new HaskellCompositeElementType("ImportDecl");
    HaskellCompositeElementType INST_DECL = new HaskellCompositeElementType("InstDecl");
    HaskellCompositeElementType FUN_BIND = new HaskellCompositeElementType("FunBind");
    HaskellCompositeElementType MATCH = new HaskellCompositeElementType("Match");
    HaskellCompositeElementType MODULE = new HaskellCompositeElementType("Module");
    HaskellCompositeElementType MODULE_NAME = new HaskellCompositeElementType("ModuleName");
    HaskellCompositeElementType PAT_BIND = new HaskellCompositeElementType("PatBind");
    HaskellCompositeElementType TYPE_SIG = new HaskellCompositeElementType("TypeSig");
    HaskellCompositeElementType UN_GUARDED_RHD = new HaskellCompositeElementType("UnGuardedRhs");
    HaskellCompositeElementType VAR = new HaskellCompositeElementType("Var");
    HaskellCompositeElementType HASKELL_TOKEN = new HaskellCompositeElementType("TOKEN");

    HaskellCompositeElementType[] TOKENS = new HaskellCompositeElementType[] {
            CLASS_DECL,
            DATA_DECL,
            IDENT,
            IMPORT_DECL,
            INST_DECL,
            FUN_BIND,
            MATCH,
            PAT_BIND,
            MODULE,
            MODULE_NAME,
            TYPE_SIG,
            UN_GUARDED_RHD,
            VAR
    };

    class Factory {
        public static PsiElement createElement(ASTNode node) {
            if (node.getElementType() == MODULE) {
                return new Module(node);
            }
            if (node.getElementType() == IMPORT_DECL) {
                return new ImportDecl(node);
            }
            return new ASTWrapperPsiElement(node);
        }
    }

}
