module Main where

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


parseFile name = runGhc (Just libdir) $ do
    dflags <- getSessionDynFlags
    let dflags' = foldl xopt_set dflags [Opt_Cpp, Opt_ImplicitPrelude, Opt_MagicHash]
    setSessionDynFlags dflags'
    target <- guessTarget name Nothing
    setTargets [target]
    load LoadAllTargets
    modSum <- getModSummary $ mkModuleName "Main"
    p <- parseModule modSum
    return p

getNames name = runGhc (Just libdir) $ do
    dflags <- getSessionDynFlags
    let dflags' = foldl xopt_set dflags
                                [Opt_Cpp, Opt_ImplicitPrelude, Opt_MagicHash]
    setSessionDynFlags dflags'
    mod <- findModule (mkModuleName name) Nothing
    info <- getModuleInfo mod
    return $ modInfoExports (fromJust info)