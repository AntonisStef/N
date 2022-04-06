import bcg

import unittest


class TestBcg(unittest.TestCase):

	def setUp(self):
		self.__bc=bcg.BolometryTable('data/test/ascii_bcg.txt')
		self.__testVal=[2600.0,5.0,-0.50,0.2]
		
	def test_where(self):
		expected = 358
		result = self.__bc.where(self.__testVal)
		self.assertEqual(expected,result)
		
	def test_previous(self):
		expectedIndexes = [171,342,-1,357]
		
		for i in range(len(self.__testVal)):
			self.assertEqual(expectedIndexes[i],self.__bc.previousNode(self.__testVal,i))
			
	def test_next(self):
		expectedIndexes = [746,-1,-1,-1]
		
		for i in range(len(self.__testVal)):
			self.assertEqual(expectedIndexes[i],self.__bc.nextNode(self.__testVal,i))
			
	def test_interpolate(self):
		result = self.__bc.interpolate([2550.,5.0 ,-0.5  ,0.2])
		expected = -2.1935
		self.assertAlmostEqual(expected, result, places=4)
		
	def test_interpolate2(self):
		result = self.__bc.interpolate([4200,1,1,0.1])
		expected = -0.5282
		self.assertAlmostEqual(expected, result, places=4)
		
	def test_computeBc(self):
		offset = 0.12
		test=[2550.,5.0 ,-0.5  ,0.2]
		expected = -2.0735
		result = self.__bc.computeBc(test,offset)
		self.assertAlmostEqual(expected, result, places=4)
		
		
	if __name__ == '__main__':
		unittest.main()
