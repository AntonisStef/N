#!/usr/bin/python3
import numpy as np
import pandas as pd
from scipy.spatial import KDTree
import numpy as np




class BolometryTable:
        # file is the bolometric table file -> pandas dataframe
	def __init__(self,file):
		table=pd.read_table(file, delim_whitespace=True, names=["teff", "logg",\ 			"metal","alpha","bc"],dtype={'teff':'float32','logg':'float32','metal':'float32','alpha':'float32','bc':'float32'})
	    self.__bolometry=table.sort_values(by=['teff', 'logg','metal','alpha'])
	    self.__param=self.__bolometry.values[:,0:4]
	    self.__bc = self.__bolometry.values[5]
	    self.__kdtree = KDTree(self.__param)
	    
	def where(self,g):
	    bas=0
	    haut=len(self.__param)-1
	
	    while True:
                milieu =(int)((bas+haut)/2)
                elem = self.__param[milieu]
               
                if self.compareTo(elem,g)==0:
               	    return milieu
                		
                elif self.compareTo(elem,g)==-1:
               	    bas = milieu + 1
                		
                else:
               	    haut = milieu -1
               	    
               	if bas > haut:
               		return -1
               		
	def compareTo(self,p1,p2):
		tol=1e-6
		if p1[0] > p2[0]:
			return 1
		if (p1[0] < p2[0]):
			return -1
		
		if abs(round(p1[0] -p2[0],7)) < tol:
			if p1[1] > p2[1]:
				return 1
			if p1[1] < p2[1]:
				return -1
				
			if abs(round(p1[1] -p2[1],7)) < tol:
				if p1[2] > p2[2]:
					return 1
					
				if p1[2] < p2[2]:
					return -1
					
				if abs(round(p1[2] -p2[2],7)) < tol:
					if p1[3] > p2[3]:
						return 1
					if p1[3] < p2[3]:
						return -1
		return 0
		

	def nearestIndex(self,value):
		d, i = self.__kdtree.query(value)
		return i
		
	def interpolate(self,value):
		pos = self.where(value)
		
		if pos!=-1:
			return self.__bc[pos]
			
		index = self.nearestIndex(value)
		nearGridNode = self__param[index]
