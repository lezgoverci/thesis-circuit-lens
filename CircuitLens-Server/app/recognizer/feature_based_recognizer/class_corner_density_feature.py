import class_feature as f
import numpy as np
import class_feature_processable_data_extractor as fpde

class CornerDensityFeature(f.Feature):
    def __init__(self):
        self.__arguments = None
        self.__neededArguments = ['area', 'centroid', 'img', 'feature_data_extractors']
        self.__neededFeatureDataExtractors = ['keypoints']
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
        if not self.__calculatedFeature or recalculate:
            self.calculate()
        
        return self.__calculatedFeature
    
    def getNeededFeatureDataExtractors(self):
        return self.__neededFeatureDataExtractors
    
    #-----------------------------------------
    # Other Functions
    #-----------------------------------------

    def calculate(self):
        if not self.argumentsMet():
            return None
        
        if 0 == self.__arguments['area']:
            return np.array([0.0, 0.0, 0.0])
        
        keyPointsExtractor = self.__arguments['feature_data_extractors']['keypoints']
        
        if not keyPointsExtractor.argumentsMet():
            keyPointsExtractor.setArguments({
                'centroid': self.__arguments['centroid'],
                'img': self.__arguments['img']
            })
        
        corners, _ = keyPointsExtractor.getExtractedData()
        
        self.__calculatedFeature = np.array([len(corners) * 10 / self.__arguments['area'], 0.0, 0.0])
        
        return self

    def argumentsMet(self):
        import class_feature_processable_data_extractor as fpde
        
        return len(self.__arguments) > 0 and \
               all(neededArg in self.__arguments for neededArg in self.__neededArguments) and \
               all(isinstance(self.__arguments['feature_data_extractors'][featureDataExtractor], fpde.FeatureProcessableDataExtractor) \
                    for featureDataExtractor in self.__neededFeatureDataExtractors)