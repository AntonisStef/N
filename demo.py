#!/usr/bin/python3
#
import numpy as np
import pandas as pd
import bcg
#
#------------------------------
#

bc=bcg.BolometryTable('data/bc_dr3_feh_all.txt')
print(bc.where((2500,2,-1,0)))
print(bc.nearestBc((2510,2,-1,0)))


