import class_feature as f
import numpy as np
import recognizer.feature_based_recognizer.feature_data_extractors.class_feature_processable_data_extractor as fpde

class CornerDistanceFeature(f.Feature):
    def __init__(self):
        self.__arguments = None
        self.__neededArguments = ['area', 'centroid', 'img', 'feature_data_extractors']
        self.__neededFeatureDataExtractors = ['central_angles', 'corners_keypoints']
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
        
        if 0 == self.__arguments['area']:
            return np.array([0.0, 0.0, 0.0])
        
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

        cornerDistance = np.array([0.0, 0.0, 0.0])
    
        prevVector = angleVectorMap[centralAngles[0]]
        
        i = 1
        while i < len(centralAngles):
            try:
                currentVector = angleVectorMap[centralAngles[i]]
                cornerDistance += (currentVector - prevVector) 
                
            except Exception as e:
                print e
            
            prevVector = currentVector
            
            i += 1
        
        self.__calculatedFeature = (cornerDistance * 10) / self.__arguments['area']
        
        return self

    def argumentsMet(self):
        return len(self.__arguments) > 0 and \
               all(neededArg in self.__arguments for neededArg in self.__neededArguments) and \
               all(isinstance(self.__arguments['feature_data_extractors'][featureDataExtractor], fpde.FeatureProcessableDataExtractor) \
                    for featureDataExtractor in self.__neededFeatureDataExtractors)