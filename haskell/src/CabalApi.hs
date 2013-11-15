module CabalApi where

import Distribution.PackageDescription.Parse
import Distribution.PackageDescription
import Distribution.Package
import Distribution.Verbosity
import SExpr


dependencyToName :: Dependency -> String
dependencyToName dep = ""

getCabalFile :: FilePath -> IO ()
getCabalFile path = do
    package <- readPackageDescription silent path
    putStrLn $ show $ map dependencyToName $ condTreeConstraints (snd ((condExecutables package) !! 0))
