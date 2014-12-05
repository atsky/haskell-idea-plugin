module DoNotation where

listOfTuples :: [(Int,Char)]
listOfTuples = do
    let nums = [1, 2]
    n <- nums
    ch <- ['a','b']
    return (n,ch)