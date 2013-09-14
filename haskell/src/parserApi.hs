{-# LANGUAGE FlexibleInstances #-}

module Main where

import Data.List
import Language.Haskell.Exts.Annotated
import SExpr

instance SExpr SrcSpanInfo where
    toSExpr info = let t = srcInfoSpan info in
            SList [SAtom "SrcSpan",
                   (SString $ srcSpanFilename t),
                   (SAtom $ show $ srcSpanStartLine t),
                   (SAtom $ show $ srcSpanStartColumn t),
                   (SAtom $ show $ srcSpanEndLine t),
                   (SAtom $ show $ srcSpanEndColumn t)]


instance (SExpr l) => SExpr [l] where
    toSExpr values = SList (map toSExpr values)

instance (SExpr l) => SExpr (Match l) where
    toSExpr (Match l name pats rhs binds) = SList [SAtom "Match", toSExpr l, toSExpr name]
    toSExpr (InfixMatch l pat1 name pats rhs binds) = SList [SAtom "InfixMatch", toSExpr l, toSExpr name]

instance (SExpr l) => SExpr (Name l) where
    toSExpr (Ident l _)	= SList [SAtom "Ident", toSExpr l]
    toSExpr (Symbol l _) = SList [SAtom "Symbol", toSExpr l]

instance (SExpr l) => SExpr (ImportDecl l) where
    toSExpr (ImportDecl l importModul importQualified importSrc importPkg importAs importSpecs) =
            SList [SAtom "ImportDecl", toSExpr l, toSExpr importModul]

instance (SExpr l) => SExpr (ModuleName l) where
    toSExpr (ModuleName l _) = SList [SAtom "ModuleName", toSExpr l]

instance (SExpr l) => SExpr (Exp l) where
    toSExpr (App l e1 e2)   = SList [SAtom "App", toSExpr l, toSExpr e1, toSExpr e2]
    toSExpr (Var l qName)   = SList [SAtom "Var", toSExpr l, toSExpr qName]
    toSExpr (Lit l literal) = SList [SAtom "Lit", toSExpr l]
    toSExpr _               = SAtom "undefined"

instance (SExpr l) => SExpr (QName l) where
    toSExpr (Qual l _ _) = SList [SAtom "Qual"]
    toSExpr (UnQual l name) = SList [SAtom "UnQual", toSExpr name]
    toSExpr (Special l _) =	SList [SAtom "Special"]


instance (SExpr l) => SExpr (Rhs l) where
    toSExpr (UnGuardedRhs l expr) = SList [SAtom "UnGuardedRhs", toSExpr l, toSExpr expr]
    toSExpr _ = SAtom "undefined"

instance (SExpr l) => SExpr (Type l) where
    toSExpr (TyForall l _ _ t) =         SList [SAtom "TyForall", toSExpr l]
    toSExpr (TyFun l t1 t2) =            SList [SAtom "TyFun", toSExpr l, toSExpr t1, toSExpr t2]
    toSExpr (TyTuple l bodex types) =    SList [SAtom "TyTuple", toSExpr l, toSExpr types]
    toSExpr (TyList l t) =               SList [SAtom "TyList", toSExpr l, toSExpr t]
    toSExpr (TyApp l t1 t2) =            SList [SAtom "TyApp", toSExpr l, toSExpr t1, toSExpr t2]
    toSExpr (TyVar l name) =             SList [SAtom "TyVar", toSExpr l, toSExpr name]
--    toSExpr (TyCon l (QName l) = SList [SAtom "TypeDecl", toSExpr l]
--    toSExpr (TyParen l (Type l) = SList [SAtom "TypeDecl", toSExpr l]
--    toSExpr (TyInfix l (Type l) (QName l) (Type l) = SList [SAtom "TypeDecl", toSExpr l]
--    toSExpr (TyKind l (Type l) (Kind l) =SList [SAtom "TypeDecl", toSExpr l]
    toSExpr _ = SAtom "undefined"

instance (SExpr a) => SExpr (Decl a) where
    toSExpr (TypeDecl l _ atype)   = SList [SAtom "TypeDecl", toSExpr l, toSExpr atype]
    toSExpr (DataDecl l _ _ _ _ _) = SList [SAtom "DataDecl", toSExpr l]
    toSExpr (ClassDecl l _ _ _ _)  = SList [SAtom "ClassDecl", toSExpr l]
    toSExpr (InstDecl l _ _ _)     = SList [SAtom "InstDecl", toSExpr l]
    toSExpr (TypeSig l names atype) = SList [SAtom "TypeSig", toSExpr l, SList (map toSExpr names), toSExpr atype]
    toSExpr (FunBind l matchList)  = SList [SAtom "FunBind", toSExpr l, SList (map toSExpr matchList)]
    toSExpr (PatBind l pat tp rhs binds) = SList [SAtom "PatBind", toSExpr l, (toSExpr  rhs)]
    toSExpr _ = SAtom "undefined"

instance SExpr (Module SrcSpanInfo) where
    toSExpr (Module srcSpan head plagma imp decls) = SList [
              SAtom "Module",
              toSExpr srcSpan,
              (SList $ map toSExpr imp),
              (SList $ map toSExpr decls)]
    toSExpr _ = SAtom "undefined"

instance (SExpr a) => SExpr (ParseResult a) where
    toSExpr (ParseOk m)               = SList [SAtom "ParseOk", (toSExpr m)]
    toSExpr (ParseFailed loc message) = undefined


main = do
    m <- getContents
    putStrLn $ printS (toSExpr (parseModule m))
