import class_feature as f
import numpy as np
import recognizer.feature_based_recognizer.feature_data_extractors.class_feature_processable_data_extractor as fpde

class CornerDensityFeature(f.Feature):
    def __init__(self):
        self.__arguments = None
        self.__neededArguments = ['area', 'centroid', 'img', 'feature_data_extractors']
        self.__neededFeatureDataExtractors = ['corners_keypoints', 'central_angles']
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
        keyPointsExtractor = self.__arguments['feature_data_extractors']['corners_keypoints']

        keyPointsExtractor.setArguments({
            'centroid': self.__arguments['centroid'],
            'img': self.__arguments['img']
        })
        
        corners, _ = keyPointsExtractor.getExtractedData(recalculate)
        
        centralAnglesExtractor.setArguments({
            'corners': corners,
            'centroid': self.__arguments['centroid'] 
        })
        
        centralAngles, angleVectorMap = centralAnglesExtractor.getExtractedData(recalculate)
        
        self.__calculatedFeature = np.array([(len(centralAngles)) / self.__arguments['area'], 0.0, 0.0])
        
        return self

    def argumentsMet(self):
        return len(self.__arguments) > 0 and \
               all(neededArg in self.__arguments for neededArg in self.__neededArguments) and \
               all(isinstance(self.__arguments['feature_data_extractors'][featureDataExtractor], fpde.FeatureProcessableDataExtractor) \
                    for featureDataExtractor in self.__neededFeatureDataExtractors)