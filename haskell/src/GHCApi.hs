module Main where

--invoke: ghci -package ghc A.hs

import GHC
import Outputable
import Data.Maybe
import Data.Typeable.Internal

import DynFlags
import GHC.Paths ( libdir )

main = do
   name <- return "Prelude" --getLine
   res <- getNames name
   putStrLn $ showSDoc tracingDynFlags ( ppr res )

getNames name = runGhc (Just libdir) $ do
    dflags <- getSessionDynFlags
    let dflags' = foldl xopt_set dflags
                                [Opt_Cpp, Opt_ImplicitPrelude, Opt_MagicHash]
    setSessionDynFlags dflags'
    mod <- findModule (mkModuleName name) Nothing
    info <- getModuleInfo mod
    return $ modInfoExports (fromJust info)