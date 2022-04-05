import numpy as np
import pandas as pd
import bcg
#
#------------------------------
#
#bc=bcg.BolometryTable('data/bc_dr3_feh_all.txt')
bc=bcg.BolometryTable('data/test/ascii_bcg.txt')
testVal=[2600,5,-0.5,0.2]
print(bc.where(testVal))
print(bc.previousNode(testVal,0))
print(bc.previousNode(testVal,1))
print(bc.previousNode(testVal,2))
print(bc.previousNode(testVal,3))
print(bc.nearestIndex((2510,2,-1,0)))
print(bc.find_value(2500,0,1))
print(bc.search( [2750.00,4.50000,-1.50000,0.00000],0,1))
print(bc.interpolate([2755.00,4.50000,-1.50000,0.00000]))

#2750.00      4.50000     -1.00000      0.00000     -1.59300
print(bc.interpolate([2751,4.50000,-1.00000,0.00000]))

