import cv2
import numpy as np
import math
from class_circuit_element_finder import CircuitElementFinder
from class_basic_functions import BasicFunctions
from class_matrix_looper import MatrixLooper
from class_pixel_processor import PixelProcessor
from class_four_connectedness_remover import FourConnectednessRemover
from class_propagator import Propagator
from class_curve_connector import CurveConnector
from class_line_connector import LineConnector

class WiresRemover2(CircuitElementFinder):
    def find(self, img, orig):
        h, w = img.shape[:2]
        kernelHalfSize = 1
        
        criterion = lambda current_pixel, prev_pixel: 255 == current_pixel and 0 == prev_pixel or 0 == current_pixel and 255 == prev_pixel

        result = img.copy()
        
        y = 0
        while y < h:
            x = 0
            while x < w:
                if 0 != result[y][x]:
                    while self.__isFilled(result, (x, y), kernelHalfSize):
                        kernelHalfSize += 1
                x += 1
            y += 1
        
        return kernelHalfSize
    
    def __isALine(self, matrix, center, kernelHalfSize, intercepts):
        fromPos, toPos = self.__getSubMatrixCoordinates(matrix.shape[:2], center, kernelHalfSize)
        
        s1 = self.__getDistance(intercepts[0], intercepts[1])
        s2 = self.__getDistance(intercepts[1], intercepts[2])
        
        area = s1 * s2
        
        num_pixels = self.__getSum(matrix, (fromPos, toPos))
        
        ave = (num_pixels + area) / 2
        
        return 20 >= (num_pixels * 100) / ave
        
    def __getDistance(self, p1, p2):
        return math.sqrt(math.pow(p2[1] - p1[1], 2) + math.pow(p2[0] - p1[0], 2))
    
    def __fill(self, matrix, center, kernelHalfSize, intensity):
        fromPos, toPos = self.__getSubMatrixCoordinates(matrix.shape[:2], center, kernelHalfSize)
        
        w, h = toPos
        
        y = fromPos[1]
        while y <= h:
            x = fromPos[0]
            while x <= w:
                matrix[y][x] = intensity
                x += 1
            y += 1

    def __isFilled(self, matrix, center, kernelHalfSize):
        fromPos, toPos = self.__getSubMatrixCoordinates(matrix.shape[:2], center, kernelHalfSize)
        
        h = toPos[1] - fromPos[1] + 1
        w = toPos[0] - fromPos[0] + 1
        
        fullSize = h * w
        
        realSum = self.__getSum(matrix, (fromPos, toPos))
        
        return fullSize == realSum
    
    def __getSubMatrixCoordinates(self, (h, w), (x, y), kernelHalfSize):
        x_low = x - kernelHalfSize if x - kernelHalfSize >= 0 else 0
        y_low = y - kernelHalfSize if y - kernelHalfSize >= 0 else 0
        
        x_high = x + kernelHalfSize if x + kernelHalfSize < w else w - 1
        y_high = y + kernelHalfSize if y + kernelHalfSize < h else h - 1
        
        return ((x_low, y_low), (x_high, y_high))
    
    def __getSum(self, matrix, (fromPos, toPos)):
        w, h = toPos
        
        sum_pixels = 0
        
        y = fromPos[1]
        while y <= h:
            x = fromPos[0]
            while x <= w:
                if 0 != matrix[y][x]:
                    sum_pixels += 1
                x += 1
            y += 1
        
        return sum_pixels
    
    def __getIntercepts(self, matrix, center, kernelHalfSize, criterion):
        h, w = matrix.shape[:2]

        borders = BasicFunctions.getBorders(center, kernelHalfSize, matrix.shape[:2])

        intercepts = []

        for from_pos, to_pos, prev_pixel_loc, step in borders:
            prev_pixel = matrix[prev_pixel_loc[1]][prev_pixel_loc[0]]
            while True:
                if criterion(matrix[from_pos[1]][from_pos[0]], prev_pixel):
                        intercepts.append((from_pos[0], from_pos[1]))
                
                prev_pixel = matrix[from_pos[1]][from_pos[0]]
                
                if to_pos == from_pos:
                    break
                
                from_pos[0] += step[0]
                from_pos[1] += step[1]
        
        return intercepts