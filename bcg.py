import numpy as np
import pandas as pd
from scipy.spatial import KDTree
"""

"""
class BolometryTable:
	"""
	   constructor. Take as argument the ascii table file.
	"""
	def __init__(self,file):
	    table=pd.read_table(file, delim_whitespace=True, names=["teff", "logg","metal","alpha","bc"],dtype={'teff':'float32','logg':'float32','metal':'float32','alpha':'float32','bc':'float32'})
	    self.__bolometry=table.sort_values(by=['teff', 'logg','metal','alpha'])
	    self.__param=self.__bolometry.values[:,0:4]
	    self.__bc=self.__bolometry.values[:,4]
	    self.__kdtree = KDTree(self.__param)
	    self.__paramkeys=[]
	    
	    for i in range(4):
	    	self.__paramkeys.append(np.unique(self.__param[:,i]))

	
	"""
	   main method: uses 'value' as input : [teff, logg, metal, alphaFe] and an optional offset
	   returns the bolometric correction at this point
	"""
	def computeBc(self,value,offset=0):
		
		try:
			bc=self.interpolate(value)
		except ValueError:
			bc=self.__bc[self.nearestIndex(value)]
		
		return bc+offset
				
	"""
	   find the index location of pouint g (array) by dichotomy
	"""
	def where(self,g):
	    bas=0
	    haut=len(self.__param)-1
	
	    while True:
                milieu =(int)((bas+haut)/2)
                elem = self.__param[milieu]
                
                if self.equals(elem,g):
                    return milieu           		
                elif self.compareTo(elem,g)==-1:
               	    bas = milieu + 1        		
                else:
               	    haut = milieu -1       	    
               	if bas > haut:
               		return -1

	"""
		checks equality of 2 points
	"""
	def equals(self,p1,p2):
		tol=1e-6
		
		if abs(round(p1[0]-p2[0],7))<tol and abs(round(p1[1]-p2[1],7))<tol and abs(round(p1[2]-p2[2],7))<tol and abs(round(p1[3]-p2[3],7))<tol:
			return True
		return False
		
	"""
		define comparaison between 2 points (-1 if before, 1 after)
	"""
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
		
	"""
	   performs a quick interpolation into the grid
	"""
	def interpolate(self,value):
		pos = self.where(value)
		x=value.copy()
		nParams=len(value)
		
		if pos!=-1:
			return self.__bc[pos]
			
		index = self.nearestIndex(value)
		nearGridNode = self.__param[index]
		
		for i in range(nParams):
			ww=round(value[i]*1e8,0)/1e8
			xx=round(nearGridNode[i]*1e8,0)/1e8
			
			if ww < xx:
				prevIndex=self.previousNode(nearGridNode,i)
				
				if prevIndex==-1:
					raise ValueError()
				nearGridNode[i] = self.__param[prevIndex][i]
		
		nElems = 2**nParams
		coeff=np.zeros((nElems,nParams), dtype = int)
		v={}	
		hypercubeNodes=[]
		hypercubeBc=[]
		
		for i in range(nElems):
			val=i
			nodeCopy=nearGridNode.copy()
			
			try:
				for j in range(nParams):
					
					if val%2==1:
						coeff[i][j]=1
						
						if not j in v:
							nextNode=self.nextNode(nearGridNode,j)
						
							if nextNode==-1:
								nextNode=self.previousNode(nearGridNode,j)
								
								if nextNode==-1:
									raise ValueError()
									
							v[j]=nextNode
						nodeCopy[j]=self.__param[v[j]][j]
					val=val//2	
				index=self.where(nodeCopy)
					
				if index==-1:
					raise ValueError()
					
				hypercubeBc.append(self.__bc[index])
				hypercubeNodes.append(nodeCopy)
				
			except ValueError:
				index=self.where(nearGridNode)
				
				if index==-1:
					raise ValueError()
				hypercubeNodes.append(nearGridNode)
				hypercubeBc.append(self.__bc[index])
			
		interpolated=0
		x=np.zeros(nParams, dtype = float)
		
		for i in range(nParams):
				
			if hypercubeNodes[2**i][i]==nearGridNode[i]:
				x[i]=0
			else:
				x[i]=(value[i]-nearGridNode[i]) / (hypercubeNodes[2**i][i]-nearGridNode[i])
		
		for i in range(coeff.shape[0]):
			c=1
			
			for j in range(coeff.shape[1]):
				c=c*((1-x[j]) + coeff[i][j]* (2*x[j]-1))
				
			interpolated=interpolated+hypercubeBc[i]*c
		return interpolated


	"""
	   find the kth previous or kth next value in one given dimension
	"""
	def find_value(self,value,axis,k):
		count=0
		
		for i in self.__paramkeys[axis]:
		
			if abs(value-i) < 1e-5:
				return self.__paramkeys[axis][count+k]
			count=count+1
		return None
		
	"""
  	   check bounds : False if (previous or next) point from 'val' is outside grid
  	"""
	def check(self,val,axis,direction):
	
		if direction>0 and val>=self.__paramkeys[axis].max():
			return False
			
		if direction<0 and val <=self.__paramkeys[axis].min():
			return False
			
		return True

	def search(self, val, axis, direction):
		k=0
		index=-1

		while True:
			x=val.copy()
			
			if direction<0:
				k=k-1
			else:
				k=k+1
				
			paramValSearch=self.find_value(val[axis],axis,k)
			
			if paramValSearch is None:
				return -1
				
			x[axis]=paramValSearch
			index=self.where(x)
			
			if index !=-1 or not self.check(paramValSearch,axis,k):
				return index

	def previousNode(self,val,axis):
		return self.search(val,axis,-1)

	def nextNode(self,val,axis):
		return self.search(val,axis,1)
			
			


		
