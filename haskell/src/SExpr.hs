module SExpr where

import Data.List

data SExpression =
    SAtom String  |
    SString String |
    SList [SExpression]

printS :: SExpression -> String
printS (SAtom str)       = str
printS (SString str)     = "\"" ++ str ++ "\""
printS (SList children)  = "(" ++ (intercalate " " (map printS children)) ++ ")"

class SExpr a where
    toSExpr :: a -> SExpression
