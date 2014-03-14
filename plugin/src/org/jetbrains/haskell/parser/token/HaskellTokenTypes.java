package org.jetbrains.haskell.parser.token;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.haskell.parser.HaskellCompositeElementType;
import org.jetbrains.haskell.parser.HaskellToken;
import org.jetbrains.haskell.parser.lexer.LexerPackage;
import org.jetbrains.haskell.psi.Import;
import org.jetbrains.haskell.psi.Module;

public interface HaskellTokenTypes {

    IElementType LEFT_BRACE    = LexerPackage.getLEFT_BRACE();
    IElementType LEFT_BRACKET  = LexerPackage.getLEFT_BRACKET();
    IElementType LEFT_PAREN    = LexerPackage.getLEFT_PAREN();
    IElementType RIGHT_BRACE   = LexerPackage.getRIGHT_BRACE();
    IElementType RIGHT_BRACKET = LexerPackage.getRIGHT_BRACKET();
    IElementType RIGHT_PAREN   = LexerPackage.getRIGHT_PAREN();


    HaskellCompositeElementType CLASS_DECL = new HaskellCompositeElementType("ClassDecl");
    HaskellCompositeElementType DATA_DECL = new HaskellCompositeElementType("DataDecl");
    HaskellCompositeElementType IDENT =  new HaskellCompositeElementType("Ident");
    HaskellCompositeElementType IMPORT = new HaskellCompositeElementType("Import");
    HaskellCompositeElementType INST_DECL = new HaskellCompositeElementType("InstDecl");
    HaskellCompositeElementType FQ_NAME = new HaskellCompositeElementType("fqName");
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
            IMPORT,
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
            if (node.getElementType() == IMPORT) {
                return new Import(node);
            }
            return new ASTWrapperPsiElement(node);
        }
    }

}
