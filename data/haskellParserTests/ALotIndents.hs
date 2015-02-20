module ALotIndents where

handleArguments = do
      mapM_ handle' args where
        handle' x = do
            let arg = x
            case arg of
                Main m -> x

