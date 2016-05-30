import class_feature as f
import numpy as np
import common.class_basic_functions as bf
import class_feature_processable_data_extractor as fpde

class GearnessFeature(f.Feature):
    def __init__(self):
        self.__arguments = None
        self.__neededArguments = ['centroid', 'img', 'feature_data_extractors']
        self.__neededFeatureDataExtractors = ['central_angles', 'keypoints']
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
        
        centralAnglesExtractor = self.__arguments['feature_data_extractors']['central_angles']
        keyPointsExtractor = self.__arguments['feature_data_extractors']['keypoints']
        
        if not keyPointsExtractor.argumentsMet():
            keyPointsExtractor.setArguments({
                'centroid': self.__arguments['centroid'],
                'img': self.__arguments['img']
            })
        
        corners, _ = keyPointsExtractor.getExtractedData()
        
        if not centralAnglesExtractor.argumentsMet():
            centralAnglesExtractor.setArguments({
                'corners': corners,
                'centroid': self.__arguments['centroid'] 
            })
        
        centralAngles, angleVectorMap = centralAnglesExtractor.getExtractedData()

        gearness = np.array([0, 0, 0])
    
        prevVector = angleVectorMap[centralAngles[0]]
        
        i = 1
        while i < len(centralAngles):
            try:
                currentVector = angleVectorMap[centralAngles[i]]
                
                nextVectorIndex = i + 1 if i + 1 < len(centralAngles) else 0
                nextVector = angleVectorMap[centralAngles[nextVectorIndex]]
                
                rB = prevVector - currentVector
                rA = nextVector - currentVector

                normalizer = bf.BasicFunctions.calculatePointsDistance(rB, rA)

                if 0 != normalizer:
                    gearness += np.cross(rB, rA) / normalizer
                
            except Exception as e:
                print e
            
            prevVector = currentVector
            
            i += 1
        
        self.__calculatedFeature = gearness
        
        return self

    def argumentsMet(self):
        import class_feature_processable_data_extractor as fpde
        
        return len(self.__arguments) > 0 and \
               all(neededArg in self.__arguments for neededArg in self.__neededArguments) and \
               all(isinstance(self.__arguments['feature_data_extractors'][featureDataExtractor], fpde.FeatureProcessableDataExtractor) \
                    for featureDataExtractor in self.__neededFeatureDataExtractors)