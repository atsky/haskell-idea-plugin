module Main where

import GHC
import Outputable
import Data.Maybe
import Data.Typeable.Internal
import System.Environment
import Control.Monad
import IdeApi.CabalApi
import Type
import Id
import DataCon


import DynFlags
import GHC.Paths (libdir)

data Args =
    ListImports String |
    ParseFile String |
    ParseCabalFile String |
    CabalList |
    Error String

parseCommandArgs :: [String] -> Args
parseCommandArgs ["packages-list"] = CabalList
parseCommandArgs (command : args)  = case (args, command) of
    (arg : _, "list") -> ListImports arg
    (arg : _, "parse") -> ParseFile arg
    (arg : _, "cabal") -> ParseCabalFile arg
    _ -> Error ("Unknown command: " ++ command)

parseCommandArgs _ = Error "Arguments required"

main :: IO ()
main = do
    args <- getArgs
    run (parseCommandArgs args)

run :: Args -> IO ()
run (ListImports name) = do
    names <- getNames name
    forM_ names (\n -> putStrLn $ showSDoc tracingDynFlags n)
run (ParseFile name) = do
    mod <- parseFile name
    putStrLn $ showSDoc tracingDynFlags (ppr $ pm_parsed_source mod)
run CabalList = do
    packages <- listPackages
    forM_ packages putStrLn
run (ParseCabalFile name) = do
    getCabalFile name


run (Error text) = putStrLn $ "Error: " ++ text ++ helpMessage

getTyThingInfo :: TyThing -> SDoc
getTyThingInfo (AnId c)     = ppr $ idType c
getTyThingInfo (ADataCon c) = ppr $ dataConRepType c
getTyThingInfo (ATyCon c)   = ppr c
getTyThingInfo (ACoAxiom c) = ppr c

getNames :: String -> IO [SDoc]
getNames name = runGhc (Just libdir) $ do
    dflags <- getSessionDynFlags
    let dflags' = foldl xopt_set dflags
                                [Opt_Cpp, Opt_ImplicitPrelude, Opt_MagicHash]
    setSessionDynFlags dflags'
    mod <- findModule (mkModuleName name) Nothing

    info <- getModuleInfo mod
    let names = (modInfoExports (fromJust info))
    t <- sequence $ map (modInfoLookupName (fromJust info))  names
    let t2 = map (getTyThingInfo . fromJust) t
    return t2


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

-- | The help message displayed when given unknown parameters.
helpMessage :: String
helpMessage = unlines
  [""
  , "Usage:"
  , "\thaskell-ide-api list <file.hs>\t\tList imports for file."
  , "\thaskell-ide-api parse <file.hs>\t\tPrint a parse tree for file."
  , "\thaskell-ide-api packages-list\t\tList Cabal packages."
  , "\thaskell-ide-api cabal <file.cabal>\tPrint a parse tree of the cabal file."
  ]