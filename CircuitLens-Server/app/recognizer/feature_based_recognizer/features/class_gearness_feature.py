import class_feature as f
import numpy as np
import common.class_basic_functions as bf
import recognizer.feature_based_recognizer.feature_data_extractors.class_feature_processable_data_extractor as fpde

class GearnessFeature(f.Feature):
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

        gearness = np.array([0.0, 0.0, 0.0])
    
        prevVector = angleVectorMap[centralAngles[0]]
        
        i = 1
        while i < len(centralAngles):
            try:
                currentVector = angleVectorMap[centralAngles[i]]
                
                nextVectorIndex = i + 1 if i + 1 < len(centralAngles) else 0
                nextVector = angleVectorMap[centralAngles[nextVectorIndex]]
                
                rB = prevVector - currentVector
                rA = nextVector - currentVector

                gearness += np.cross(rB, rA)
                
            except Exception as e:
                print e
            
            prevVector = currentVector
            
            i += 1
        
        self.__calculatedFeature = gearness / self.__arguments['area']
        
        return self

    def argumentsMet(self):
        return self.__arguments is not None and \
               all(neededArg in self.__arguments for neededArg in self.__neededArguments) and \
               all(isinstance(self.__arguments['feature_data_extractors'][featureDataExtractor], fpde.FeatureProcessableDataExtractor) \
                    for featureDataExtractor in self.__neededFeatureDataExtractors)
