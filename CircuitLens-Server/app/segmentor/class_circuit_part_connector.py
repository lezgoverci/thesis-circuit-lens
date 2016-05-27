import cv2
import numpy as np
from class_basic_functions import BasicFunctions

class CircuitPartConnector:
    def __init__(self, edges, completeImage, elemMark=255):
        self.__edges = edges
        self.__completeImage = np.float32(completeImage)
        self.__elemMark = elemMark
        self.__equivalenceClass = {}
        self.__maxDst = 1
    
    def connect(self, iteration=1):
        if iteration < 1:
            return
        
        while iteration > 0:
            numComponents, self.__labeledImage = cv2.connectedComponents(self.__edges)
            
            h, w = self.__edges.shape[:2]
            
            y = 1
            while y < h - 1:
                x = 1
                while x < w - 1:
                    if 0 != self.__edges[y][x]:
                        self.__propagate((x, y))
                    x += 1
                y += 1
            
            iteration -= 1
        
    
    def getMaxDst(self):
        return self.__maxDst
        
    def __isADeadEnd(self, (x, y)):
        num_zeros = 0
        borders = BasicFunctions.getBorders((x, y), 1, self.__edges.shape[:2])
        
        import copy
        
        b = copy.deepcopy(borders)
        f = None
        try:
            for fromPos, toPos, _, step in borders:
                while True:
                    f = fromPos
                    if 0 == self.__edges[fromPos[1]][fromPos[0]]:
                        num_zeros += 1
                    
                    if fromPos == toPos:
                        break
                    
                    fromPos[0] += step[0]
                    fromPos[1] += step[1]
        except IndexError:
            print "at (%d, %d)" % (x, y)
            print "borders: %s" % (str(b))
            print "because of: %s" % (str(f))
            raise IndexError
        return num_zeros >= 7
    
    def __propagate(self, (x, y)):
        classValue = self.__labeledImage[y][x]
        
        halfSide = 1
        
        while True:
            borders = BasicFunctions.getBorders((x, y), halfSide, self.__edges.shape[:2])
        
            for fromPos, toPos, _, step in borders:
                while True:
                    currentClass = self.__labeledImage[fromPos[1]][fromPos[0]]
                    
                    
                    if self.__completeImage[fromPos[1]][fromPos[0]] != 0:
                        self.__completeImage[fromPos[1]][fromPos[0]] = classValue
                    
                    if 0 != currentClass and classValue != currentClass and self.__hasThisNeighbour((fromPos[0], fromPos[1]), classValue):
                        cv2.line(self.__edges, (x, y), (fromPos[0], fromPos[1]), self.__elemMark, 1)
                        
                        if self.__maxDst < halfSide:
                            self.__maxDst = halfSide
                        
                        return
                    
                    if fromPos == toPos:
                        break
                    
                    fromPos[0] += step[0]
                    fromPos[1] += step[1]
                    
            halfSide += 1
    
    def __equalClasses(self, firstClass, secondClass):
        return self.__equivalenceClass.get(str(firstClass) + str(secondClass), False)
    
    def __hasThisNeighbour(self, (x, y), classValue):
        borders = BasicFunctions.getBorders((x, y), 1, self.__edges.shape[:2])
        
        for fromPos, toPos, _, step in borders:
            while True:
                currentClass = self.__completeImage[fromPos[1]][fromPos[0]]
                
                if classValue == currentClass:
                    return True
                
                if fromPos == toPos:
                    break
                
                fromPos[0] += step[0]
                fromPos[1] += step[1]
        
        return False
    
    def __mapClasses(self, firstClass, secondClass):
        firstClassStr = str(firstClass)
        secondClassStr = str(secondClass)
        
        self.__equivalenceClass[firstClassStr + secondClassStr] = True
        self.__equivalenceClass[secondClassStr + firstClassStr] = True
    