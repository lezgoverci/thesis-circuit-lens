import class_feature as f
import numpy as np
import recognizer.feature_based_recognizer.feature_data_extractors.class_feature_processable_data_extractor as fpde
import common.class_basic_functions as bf
import cv2
import math
import class_feature_factory as ff

class BlackWhiteFeature(f.Feature):
    def __init__(self):
        self.__arguments = None
        self.__neededArguments = ['img', 'moments']
        self.__neededFeatureDataExtractors = []
        self.__calculatedFeature = None
    
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

    def calculate(self, recalculate=False, LOW=0, HIGH=255):
        if not self.argumentsMet():
            return None

        m = self.__arguments['moments']
        img = self.__arguments['img']

        self.__h, self.__w = img.shape[:2]
        centroid = [m['m10'] / m['m00'], m['m01'] / m['m00'], 0]

        maxHalfSide = int(min(img.shape[:2]) * 0.35)

        currentSide = 1
        totalNumPixels = 0

        if 0 == img[centroid[1]][centroid[0]]:
            self.__calculatedFeature = np.array([0.0, 0.0, 0.0])
        else:
            self.__calculatedFeature = np.array([0.0, 0.0, 5.0])

        while currentSide <= maxHalfSide:
            borders = bf.BasicFunctions.getBorders(centroid, currentSide, img.shape[:2])
            
            numChanged = 0
            for fromPos, toPos, prevLoc, stepper in borders:
                prevPixel = img[prevLoc[1]][prevLoc[0]]

                while True:
                    if not self.__validCoordinates(fromPos[0], fromPos[1]):
                        break

                    if prevPixel != img[fromPos[1]][fromPos[0]] and 0 == prevPixel:
                        numChanged += 1
                    
                    prevPixel = img[fromPos[1]][fromPos[0]]
                    
                    if toPos == fromPos:
                        break
                    
                    fromPos[0] += stepper[0]
                    fromPos[1] += stepper[1]

            if numChanged == 0:
                self.__calculatedFeature[2] += 1.3

            currentSide += 1
        
        bias = 10
        
        self.__calculatedFeature += np.array([totalNumPixels * bias / m['m00'], 0.0, 0.0])

        return self
    
    def __validCoordinates(self, x, y):
        return x >= 0 and y >= 0 and x < self.__w and y < self.__h
    
    def argumentsMet(self):
        return len(self.__arguments) > 0 and \
               all(neededArg in self.__arguments for neededArg in self.__neededArguments) and \
               all(isinstance(self.__arguments['feature_data_extractors'][featureDataExtractor], fpde.FeatureProcessableDataExtractor) \
                    for featureDataExtractor in self.__neededFeatureDataExtractors)