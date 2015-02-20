module WhereCase where

list = [ br | br <- breaks_,
                 case x of Var1 -> True; _ -> False ]

