module Main where

import GHC
import Outputable
import Data.Maybe
import Data.Typeable.Internal()
import System.Environment
import Control.Monad
import CabalApi


import DynFlags
import GHC.Paths (libdir)

data Args =
    ListImports String |
    ParseFile String |
    ParseCabalFile String |
    CabalList |
    Error String

parseCommandArgs :: [String] -> Args
parseCommandArgs (command : args) = case command of
    "list" -> ListImports (args !! 0)
    "parse" -> ParseFile (args !! 0)
    "packages-list" -> CabalList
    "cabal" -> ParseCabalFile (args !! 0)
    _ -> Error ("Unknown command: " ++ command)

parseCommandArgs _ = Error "Arguments required"

main :: IO ()
main = do
    args <- getArgs
    run (parseCommandArgs args)

run :: Args -> IO ()
run (ListImports name) = do
    names <- getNames name
    forM_ names (\n -> putStrLn $ showSDoc tracingDynFlags (ppr n))
run (ParseFile name) = do
    mod <- parseFile name
    putStrLn $ showSDoc tracingDynFlags (ppr $ pm_parsed_source mod)
run CabalList = do
    packages <- listPackages
    forM_ packages putStrLn
run (ParseCabalFile name) = do
    getCabalFile name


run (Error text) = putStrLn $ "Error: " ++ text

getNames :: String -> IO [Name]
getNames name = runGhc (Just libdir) $ do
    dflags <- getSessionDynFlags
    let dflags' = foldl xopt_set dflags
                                [Opt_Cpp, Opt_ImplicitPrelude, Opt_MagicHash]
    setSessionDynFlags dflags'
    mod <- findModule (mkModuleName name) Nothing
    info <- getModuleInfo mod
    return $ modInfoExports (fromJust info)


parseFile :: String -> IO ParsedModule
parseFile name = runGhc (Just libdir) $ do
    --dflags <- getSessionDynFlags
    --let dflags' = foldl xopt_set dflags [Opt_Cpp, Opt_ImplicitPrelude, Opt_MagicHash]
    --_ <- setSessionDynFlags dflags'
    target <- guessTarget name Nothing
    setTargets [target]
    --load LoadAllTargets
    modSum <- getModSummary $ mkModuleName "Test"
    p <- parseModule modSum
    return p