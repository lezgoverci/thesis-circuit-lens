import class_feature as f
import numpy as np
import common.class_basic_functions as bf
import recognizer.feature_based_recognizer.feature_data_extractors.class_feature_processable_data_extractor as fpde
import cv2

class SymmetricalFeature(f.Feature):
    def __init__(self):
        self.__arguments = None
        self.__neededArguments = ['centroid', 'img', 'feature_data_extractors']
        self.__neededFeatureDataExtractors = ['central_angles', 'edges_keypoints']
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

        centralAnglesExtractor = self.__arguments['feature_data_extractors']['central_angles']
        keyPointsExtractor = self.__arguments['feature_data_extractors']['edges_keypoints']
        
        keyPointsExtractor.setArguments({
            'centroid': self.__arguments['centroid'],
            'img': self.__arguments['img']
        })
        
        edges, i = keyPointsExtractor.getExtractedData(recalculate)
        
        centralAnglesExtractor.setArguments({
            'corners': edges,
            'centroid': self.__arguments['centroid'] 
        })
        
        centralAngles, angleVectorMap = centralAnglesExtractor.getExtractedData(recalculate)

        symmetrical = np.array([0.0, 0.0, 0.0])
    
        i = 1
        while i < len(centralAngles):
            try:
                currentVector = angleVectorMap[centralAngles[i]]
                
                symmetrical += currentVector / np.linalg.norm(currentVector)
            except Exception as e:
                print e

            i += 1
        
        bias = 15
        
        self.__calculatedFeature = bias * symmetrical / len(centralAngles)
        
        return self

    def argumentsMet(self):
        return self.__arguments is not None and \
               all(neededArg in self.__arguments for neededArg in self.__neededArguments) and \
               all(isinstance(self.__arguments['feature_data_extractors'][featureDataExtractor], fpde.FeatureProcessableDataExtractor) \
                    for featureDataExtractor in self.__neededFeatureDataExtractors)