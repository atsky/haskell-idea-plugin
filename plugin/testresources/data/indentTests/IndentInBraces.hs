module IndentInBraces where

ctxt_lookup :: Name -> Field (Ctxt a) (Maybe a)
ctxt_lookup n = Field
    { fget = lookupCtxtExact n
    , fset = \newVal -> case newVal of
        Just x  -> addDef n x
        Nothing -> deleteDefExact n
    }

maybe_default dflt = Field (fromMaybe dflt) (const . Just)
