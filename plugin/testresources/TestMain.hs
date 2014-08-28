module Main where

import Control.Exception

qsort [] = []
qsort (a:as) = qsort left ++ [a] ++ qsort right
  where (left,right) = (filter (<=a) as, filter (>a) as)

main :: IO ()
main = print $ qsort [1, 4, 7, 2, 9, 8, 10, 3]

steplocaltest :: IO ()
steplocaltest = main

uncaughtMain :: IO ()
uncaughtMain = main >> undefined

caughtMain :: IO ()
caughtMain = (main >> undefined) `catch` (const $ print "caught" :: SomeException -> IO ())

expression :: Int
expression = 2 + 2 * 2
