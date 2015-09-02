module OpenBraceThenClose where

import Language.CSPM.AST

instance Pretty Pattern where
  pPrint = case pattern of
    IntPat -> n
    where
      nestedDotPat = case unLabel of
        DotPat {} -> p
        x -> x