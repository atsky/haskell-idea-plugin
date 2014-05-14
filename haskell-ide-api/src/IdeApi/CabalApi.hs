module IdeApi.CabalApi where

import Distribution.PackageDescription.Parse
import Distribution.PackageDescription
import Distribution.Package
import Distribution.Verbosity
import qualified Distribution.Hackage.DB as DB
import Distribution.Text ( display )
import System.Directory
import System.FilePath
import Data.List
import IdeApi.SExpr


dependencyToName :: Dependency -> String
dependencyToName dep = ""

getCabalFile :: FilePath -> IO ()
getCabalFile path = do
    package <- readPackageDescription silent path
    case condExecutables package of
     []      -> return () -- Libraries might not have executable sections in
                          -- the cabal file.
     (x : _) -> print $ map dependencyToName $ condTreeConstraints (snd x)

getDefaultCabalDir :: IO FilePath
getDefaultCabalDir = getAppUserDataDirectory "cabal"

listPackages :: IO [String]
listPackages = do
   cabalDir <- getDefaultCabalDir
   db <- DB.readHackage' (joinPath [cabalDir, "packages", "hackage.haskell.org", "00-index.tar"])
   let packages = concat $ map DB.elems (DB.elems db)
   let grouppedPackages = groupBy (\a b -> pkgName a == pkgName b) (map (package . packageDescription) packages)
   let packageVersions = map (\x -> (pkgName $ head x, map pkgVersion x)) grouppedPackages
   return (map show packageVersions)


