module Parenthesis where


main = startDebugger (do
          modulePath <- do
              case (mainFile st) of
                  Just path -> return path
                  Nothing   -> error "Main module not specified"
          setupContext modulePath "Main"
       )
