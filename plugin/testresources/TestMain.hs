module Main where

qsort [] = []
qsort (a:as) = qsort left ++ [a] ++ qsort right
  where (left,right) = (filter (<=a) as, filter (>a) as)

main :: IO ()
main = print $ qsort [1, 4, 7, 2, 9, 8, 10, 3]

failingMain :: IO ()
failingMain = (print $ qsort [1, 4, 7, 2, 9, 8, 10, 3]) >> undefined