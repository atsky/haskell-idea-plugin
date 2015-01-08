module Imports (
    Block(..), parseDocstring, renderDocstring, emptyDocstring, nullDocstring, noDocs,
  ) where

import qualified Cheapskate as C
import Cheapskate.Html (renderDoc)
import SomeModule (OutputAnnotation(..), TextFormatting(..), Name, Term, Err)
import Data.Traversable (Traversable)