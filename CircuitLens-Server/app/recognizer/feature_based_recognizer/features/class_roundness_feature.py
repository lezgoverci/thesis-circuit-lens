import class_feature as f
import numpy as np
import recognizer.feature_based_recognizer.feature_data_extractors.class_feature_processable_data_extractor as fpde
import math, cv2

class RoundnessFeature(f.Feature):
    def __init__(self):
        self.__arguments = None
        self.__neededArguments = ['centroid', 'area', 'img', 'feature_data_extractors']
        self.__neededFeatureDataExtractors = ['edges_keypoints']
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

        keyPointsExtractor = self.__arguments['feature_data_extractors']['edges_keypoints']

        keyPointsExtractor.setArguments({
            'centroid': self.__arguments['centroid'],
            'img': self.__arguments['img']
        })
        
        _, connected = keyPointsExtractor.getExtractedData(recalculate)

        _,contours, [h] = cv2.findContours(connected.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        
        self.__calculatedFeature = np.array([0.0, 0.0, 0.0])
        
        if len(contours) > 0:
            for cnt in contours:
                area = cv2.contourArea(cnt)
                perimeter = cv2.arcLength(cnt,True)
                self.__calculatedFeature[0] += math.pow(perimeter, 2) / (2 * np.pi * area)
        
        return self

    def argumentsMet(self):
        return len(self.__arguments) > 0 and \
               all(neededArg in self.__arguments for neededArg in self.__neededArguments) and \
               all(isinstance(self.__arguments['feature_data_extractors'][featureDataExtractor], fpde.FeatureProcessableDataExtractor) \
                    for featureDataExtractor in self.__neededFeatureDataExtractors)