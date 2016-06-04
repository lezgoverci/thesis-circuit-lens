import class_feature as f
import numpy as np
import class_feature_processable_data_extractor as fpde
import common.class_basic_functions as bf
import cv2

class ConstellationnessFeature(f.Feature):
    def __init__(self):
        self.__arguments = None
        self.__neededArguments = ['img']
        self.__neededFeatureDataExtractors = []
        self.__calculatedFeature = None
        self.__constellationVectors = {}
    
    #-----------------------------------------
    # Setters
    #-----------------------------------------

    def setArguments(self, args):
        self.__arguments = args
        
        return self

    #-----------------------------------------
    # Getters
    #-----------------------------------------
    
    def getCalculatedFeature(self, recalculate=False):
        if self.__calculatedFeature is None or recalculate:
            self.calculate(True)
        
        return self.__calculatedFeature
    
    def getNeededFeatureDataExtractors(self):
        return self.__neededFeatureDataExtractors
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def calculate(self, recalculate=False):
        if not self.argumentsMet():
            return None

        img = self.__arguments['img']
        self.__h, self.__w = img.shape[:2]

        edges = cv2.Canny(img,100,200)
        # edges = bf.BasicFunctions.removeFourConnectedness(edges)

        numClass, connectedComponents = cv2.connectedComponents(edges)

        self.__collectConstellationVectors(connectedComponents)
        
        numVectors = 0
        sumOfProjections = 0

        for k, vs in self.__constellationVectors.iteritems():
            numVectors += len(vs)
            
            if len(vs) == 0:
                continue
            
            prevV = vs[0]
            
            i = 0
            while i < len(vs):
                sumOfProjections += np.dot(prevV, vs[i])
                
                prevV = vs[i]
                i += 1
                
            
        self.__calculatedFeature = np.array([numVectors, sumOfProjections, 0.0])
        
        return self
    
    def __collectConstellationVectors(self, connectedComponents):
        stopperX = 0
        stopperY = self.__h - 1
        
        xy = 0
        yx = 0
        
        while stopperX < self.__w or stopperY >= 0:
            x = self.__w - 1
            
            while x >= stopperX and xy < self.__h:
                if 0 != connectedComponents[xy][x]:
                    self.__propagate((x, xy), connectedComponents)
                x -= 1
            
            xy += 1
            stopperX += 1
            
            y = 0
            
            while y <= stopperY and yx < self.__w:
                if 0 != connectedComponents[y][yx]:
                    self.__propagate((yx, y), connectedComponents)
                y += 1
            
            yx += 1
            stopperY -= 1
    
    def __propagate(self, (x, y), connectedComponents):
        lastPoint = [x, y]
        classVal = connectedComponents[y][x]
        
        if self.__constellationVectors.get(classVal, None) is None:
            self.__constellationVectors[classVal] = []
        
        hasConnection = True
        runnerX = x
        runnerY = y
        
        connectedComponents[y][x] = 0
        
        while hasConnection:
            steppers = [[(1, 0), (runnerX + 1, runnerY - 1)], 
                        [(0, 1), (runnerX + 1, runnerY + 1)], 
                        [(-1, 0), (runnerX - 1, runnerY + 1)], 
                        [(0, -1), (runnerX - 1, runnerY - 1)]]
            
            runnerX -= 1
            runnerY -= 1
            
            hasConnection = False
            
            for stepper in steppers:
                while True:
                    if self.__validCoordinates(runnerX, runnerY) and classVal == connectedComponents[runnerY][runnerX]:
                        connectedComponents[runnerY][runnerX] = 0
                        
                        v = np.array([runnerX - lastPoint[0], runnerY - lastPoint[1]])
                        
                        if len(self.__constellationVectors[classVal]) == 0:
                            self.__constellationVectors[classVal].append(v)
                        elif self.__isANewVector(self.__constellationVectors[classVal][-1], v):
                            self.__constellationVectors[classVal].append(v)
                        
                        lastPoint = [runnerY, runnerX]

                        hasConnection = True
                        break
                    
                    if (runnerX, runnerY) == stepper[1]:
                        break
                    
                    runnerX += stepper[0][0]
                    runnerY += stepper[0][1]
                
                if hasConnection:
                    break


    def __isANewVector(self, prevVector, currentVector):
        angle = bf.BasicFunctions.calculateAngle(prevVector, currentVector, False)
        
        return angle >= 45 and angle <= 120
    
    def __validCoordinates(self, x, y):
        return x >= 0 and y >= 0 and x < self.__w and y < self.__h
    
    def argumentsMet(self):
        import class_feature_processable_data_extractor as fpde
        
        return len(self.__arguments) > 0 and \
               all(neededArg in self.__arguments for neededArg in self.__neededArguments) and \
               all(isinstance(self.__arguments['feature_data_extractors'][featureDataExtractor], fpde.FeatureProcessableDataExtractor) \
                    for featureDataExtractor in self.__neededFeatureDataExtractors)