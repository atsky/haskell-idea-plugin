module Test where

t = let as = findAllUsedArgs t args in
        length as == length (nub as) &&
        termsize n t < 10
