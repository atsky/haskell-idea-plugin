module CabalApi where

import Distribution.PackageDescription.Parse
import Distribution.PackageDescription
import Distribution.Package
import Distribution.Verbosity
import qualified Distribution.Hackage.DB as DB
import Distribution.Text ( display )
import Data.List
import SExpr


dependencyToName :: Dependency -> String
dependencyToName dep = ""

getCabalFile :: FilePath -> IO ()
getCabalFile path = do
    package <- readPackageDescription silent path
    putStrLn $ show $ map dependencyToName $ condTreeConstraints (snd ((condExecutables package) !! 0))

listPackages :: IO [String]
listPackages = do
   db <- DB.readHackage
   let packages = concat $ map DB.elems (DB.elems db)
   let grouppedPackages = groupBy (\a b -> pkgName a == pkgName b) (map (package . packageDescription) packages)
   let packageVersions = map (\x -> (pkgName $ head x, map pkgVersion x)) grouppedPackages
   return (map show packageVersions)

