import class_feature as f
import numpy as np
import common.class_basic_functions as bf
import recognizer.feature_based_recognizer.feature_data_extractors.class_feature_processable_data_extractor as fpde
import cv2

class NumContoursFeature(f.Feature):
    def __init__(self):
        self.__arguments = None
        self.__neededArguments = ['img']
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

    def calculate(self, recalculate=False):
        if not self.argumentsMet():
            return None

        self.__arguments['img'] /= 255
        numClasses, c = cv2.connectedComponents(self.__arguments['img'])
        
        bias = 20
        
        self.__calculatedFeature = np.array([(numClasses - 1) * bias, 0.0, 0.0])
        
        _,contours, [h] = cv2.findContours(self.__arguments['img'].copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        
        bias *= 5
        
        if len(contours) > 1:
            refArea = cv2.contourArea(contours[0])
            totalArea = 0
            
            i = 1
            while i < len(contours):
                area = cv2.contourArea(contours[i])
                refArea -= abs(area)
                totalArea += area
                
                i += 1
            
            self.__calculatedFeature[1] = (abs(refArea) * bias) / totalArea

        return self

    def argumentsMet(self):
        return self.__arguments is not None and \
               all(neededArg in self.__arguments for neededArg in self.__neededArguments) and \
               all(isinstance(self.__arguments['feature_data_extractors'][featureDataExtractor], fpde.FeatureProcessableDataExtractor) \
                    for featureDataExtractor in self.__neededFeatureDataExtractors)