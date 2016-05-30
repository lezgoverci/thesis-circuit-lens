import class_feature as f
import numpy as np
import common.class_basic_functions as bf

class DispersenessFromCentroidFeature(f.Feature):
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
        
        centralAnglesExtractor = self.__arguments['central_angles']
        keyPointsExtractor = self.__arguments['keypoints']
        
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

        dispersenessFromCentroid = np.array([0, 0, 0])
    
        prevVector = angleVectorMap[centralAngles[0]]
        i = 1
        while i < len(centralAngles):
            try:
                currentVector = angleVectorMap[centralAngles[i]]
                normalizer = bf.BasicFunctions.calculatePointsDistance(currentVector, prevVector)
                
                if 0 != normalizer:
                    dispersenessFromCentroid += np.cross(prevVector, currentVector) / normalizer
                
            except Exception as e:
                print e
            
            prevVector = currentVector
            
            i += 1
        
        self.__calculatedFeature = dispersenessFromCentroid
        
        return self

    def argumentsMet(self):
        import class_feature_processable_data_extractor as fpde
        
        return len(self.__arguments) > 0 and \
               all(neededArg in self.__arguments for neededArg in self.__neededArguments) and \
               all(isinstance(featureDataExtractor, fpde.FeatureProcessableDataExtractor) in self.__arguments['feature_data_extractors'] \
                    for featureDataExtractor in self.__neededFeatureDataExtractors)